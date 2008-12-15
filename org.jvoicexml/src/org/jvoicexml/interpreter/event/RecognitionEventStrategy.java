/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.AbstractInputEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.ApplicationShadowVarContainer;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Strategy to process a recognition event coming from the implementation
 * platform.
 *
 * <p>
 * A {@link RecognitionEventStrategy} may be responsible to handle events
 * for multiple fields if the form contains more than one field.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.ImplementationPlatform
 */
final class RecognitionEventStrategy
        extends AbstractInputItemEventStrategy<FieldFormItem>
        implements CollectiveEventStrategy<FieldFormItem> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecognitionEventStrategy.class);

    /** Observed fields. */
    private Collection<FieldFormItem> items;

    /**
     * Constructs a new object.
     */
    RecognitionEventStrategy() {
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     */
    public RecognitionEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final FormItem formItem) {
        super(ctx, interpreter, algorithm, formItem,
                RecognitionEvent.EVENT_TYPE);
        items = new java.util.ArrayList<FieldFormItem>();
        addItem((FieldFormItem) getFormItem());
    }

    /**
     * Sets the result in the application shadow variable.
     * @param result the current recognition result.
     * @throws SemanticError
     *         Error creating the shadow variable.
     * @since 0.6
     */
    private void setApplicationLastResult(final RecognitionResult result)
        throws SemanticError {
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        final ScriptingEngine scripting = context.getScriptingEngine();
        if (scripting.isVariableDefined(
                ApplicationShadowVarContainer.VARIABLE_NAME)) {
            final ApplicationShadowVarContainer application =
                (ApplicationShadowVarContainer)
                scripting.eval(ApplicationShadowVarContainer.VARIABLE_NAME);

            application.setRecognitionResult(result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final FieldFormItem field,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result =
                recognitionEvent.getRecognitionResult();

        if (!result.isAccepted()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result not accepted");
            }

            return false;
        }

        setApplicationLastResult(result);

        final String markname = result.getMark();
        if (markname != null) {
            field.setMarkname(markname);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractInputItemEventStrategy<FieldFormItem> newInstance(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final FormItem item) {
        return new RecognitionEventStrategy(ctx, interpreter, fia, item);
    }

    /**
     * {@inheritDoc}
     */
    public void addItem(final FieldFormItem item) {
        if (item == null) {
            LOGGER.warn("cannot add a null form item");
            return;
        }
        items.add(item);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added observed field '" + item.getName() + "'");
        }
    }

    /**
     * Determines, which of the observed items is able to accept the
     * recognized utterance.
     * @param event the current input event.
     * @return the responsible input items.
     */
    private Collection<FieldFormItem> getResponsibleItem(
            final JVoiceXMLEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("colecting responsible fields for event "
                    + event.getEventType());
        }
        final Collection<FieldFormItem> responsibleItems =
            new java.util.ArrayList<FieldFormItem>();
        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result =
                recognitionEvent.getRecognitionResult();
        for (final FieldFormItem item : items) {
            if (item.accepts(result)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("form item '" + item.getName()
                            + "' accepts the input '" + result.getUtterance()
                            + "'");
                }
                responsibleItems.add(item);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("collected " + responsibleItems.size()
                    + " form items to be responsible for event "
                    + event);
        }
        return responsibleItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setResult(final FieldFormItem item,
            final AbstractInputEvent event, final Object result) {
        final Collection<FieldFormItem> responsibleItems =
            getResponsibleItem(event);
        for (FieldFormItem current : responsibleItems) {
            super.setResult(current, event, result);
        }
    }
}
