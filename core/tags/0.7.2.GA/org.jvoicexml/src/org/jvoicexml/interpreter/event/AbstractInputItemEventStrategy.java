/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.event;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.AbstractInputEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.vxml.Filled;

/**
 * Base class to process a result that has been received by an
 * {@link InputItem}.
 *
 * <p>
 * The {@link #process(JVoiceXMLEvent)} method first calls the
 * {@link #handleEvent(InputItem, JVoiceXMLEvent)}, thus decorating the
 * event processing, before the form item variable is set and the
 * <code>&lt;filled&gt;</code> elements are executed.
 * </p>
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 * @param <T> Type of the {@link InputItem}.
 */
abstract class AbstractInputItemEventStrategy<T extends InputItem>
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractInputItemEventStrategy.class);

    /**
     * Constructs a new object.
     */
    AbstractInputItemEventStrategy() {
    }

    /**
     * Construct a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     * @param type
     *        The event type.
     */
    public AbstractInputItemEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final FormItem formItem,
                                    final String type) {
        super(ctx, interpreter, algorithm, formItem, null, type);
    }

    /**
     * {@inheritDoc}
     */
    public void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event " + event + "...");
        }

        @SuppressWarnings("unchecked")
        final T item = (T) getFormItem();
        boolean continueProcessing = handleEvent(item, event);
        if (!continueProcessing) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("processing aborted");
            }
            return;
        }

        final AbstractInputEvent inputEvent = (AbstractInputEvent) event;
        final Object result = inputEvent.getInputResult();
        setResult(item, inputEvent, result);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing filled elements...");
        }

        final Collection<Filled> filledElements = item.getFilledElements();
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        final Dialog dialog = fia.getDialog();
        final Collection<Filled> dialogFilledElements =
            dialog.getFilledElements();
        if (dialogFilledElements != null) {
            filledElements.addAll(dialogFilledElements);
        }
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();
        for (Filled filled : filledElements) {
            if (fia.isJustFilled(item)) {
                executor.executeChildNodes(context, interpreter, fia, item,
                        filled);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done executing filled element");
        }
    }

    /**
     * Sets the input result.
     * @param item the current form item.
     * @param event the caught event.
     * @param result the input result.
     */
    protected void setResult(final T item, final AbstractInputEvent event,
            final Object result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting form item variable to '" + result + "'");
        }
        item.setFormItemVariable(result);

        final FormInterpretationAlgorithm fia =
            getFormInterpretationAlgorithm();
        if (fia != null) {
            fia.setJustFilled(item);
        }
    }

    /**
     * Does some processing with the received event before the form item
     * variable is set and before the filled elements are executed.
     * @param item the input item to handle.
     * @param event the received event.
     * @return <code>true</code> if the processing should be continued.
     * @exception JVoiceXMLEvent
     *            error processing the event.
     */
    protected abstract boolean handleEvent(final T item,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent;
}
