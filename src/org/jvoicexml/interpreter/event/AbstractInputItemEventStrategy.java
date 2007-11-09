/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.xml.vxml.Filled;

/**
 * Base class to process a result that has been received by an
 * {@link org.jvoicexml.interpreter.formitem.InputItem}.
 *
 * <p>
 * The {@link #process(JVoiceXMLEvent)} method first calls the
 * {@link #handleEvent(InputItem, JVoiceXMLEvent)} before the form item
 * variable is set and the <code>&lt;filled&gt;</code> elements are
 * executed.
 * </p>
 *
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
abstract class AbstractInputItemEventStrategy
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractInputItemEventStrategy.class);

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
                                    final AbstractFormItem formItem,
                                    final String type) {
        super(ctx, interpreter, algorithm, formItem, null, type);
    }

    /**
     * {@inheritDoc}
     */
    void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event " + event + "...");
        }

        final InputItem item = (InputItem) getFormItem();
        boolean continueProcessing = handleEvent(item, event);
        if (!continueProcessing) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("processing aborted");
            }
            return;
        }

        final AbstractInputEvent inputEvent = (AbstractInputEvent) event;
        final Object result = inputEvent.getInputResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting form item variable to '" + result + "'");
        }
        item.setFormItemVariable(result);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing filled elements...");
        }

        final Collection<Filled> filledElements = item.getFilledElements();
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();

        for (Filled filled : filledElements) {
            fia.executeChildNodes(item, filled);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done executing filled element");
        }

        fia.setJustFilled(item);
    }

    /**
     * Does some processing with the received event before the form item
     * variable is set and before the filled elements are executed.
     * @param item the input item to handle.
     * @param event the received event.
     * @return <code>true</code> if the processing should be continued.
     * @exception JVoiceXMLEvent
     *            error processig the event.
     */
    protected abstract boolean handleEvent(final InputItem item,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent;
}
