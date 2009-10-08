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
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SemanticInterpretation;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.variables.ApplicationShadowVarContainer;

/**
 * Strategy to process a recognition event coming from the implementation
 * platform.
 *
 * <p>
 * A {@link FormLevelRecognitionEventStrategy} may be responsible to handle events
 * for multiple fields if the form contains more than one field.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.ImplementationPlatform
 */
final class FormLevelRecognitionEventStrategy
        extends AbstractEventStrategy
        implements EventStrategyPrototype {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FormLevelRecognitionEventStrategy.class);

    /** The current dialog. */
    private final Dialog dialog;

    /** Observed input items. */
    private Collection<InputItem> inputItems;

    /** The initial form items of the current dialog. */
    private Collection<InitialFormItem> initalItems;

    /**
     * Constructs a new object.
     */
    FormLevelRecognitionEventStrategy() {
        dialog = null;
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
     * @param dlg
     *        The current dialog.
     */
    public FormLevelRecognitionEventStrategy(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm algorithm,
            final Dialog dlg) {
        super(ctx, interpreter, algorithm, null, null,
                RecognitionEvent.EVENT_TYPE);
        dialog = dlg;
    }

    /**
     * Retrieves the input items of the dialog.
     * @return input items
     * @exception BadFetchError
     *        error obtaining the input items
     */
    private Collection<InputItem> getInputItems() throws BadFetchError {
        if (inputItems == null) {
            final VoiceXmlInterpreterContext ctx =
                getVoiceXmlInterpreterContext();
            final Collection<FormItem> formItems = dialog.getFormItems(ctx);
            inputItems = new java.util.ArrayList<InputItem>();
            for (FormItem formItem : formItems) {
                if (formItem instanceof InputItem) {
                    final InputItem item = (InputItem) formItem;
                    inputItems.add(item);
                }
            }
        }
        return inputItems;
    }

    /**
     * Retrieves the initial form items of the dialog.
     * @return initial form items
     * @exception BadFetchError
     *        error obtaining the initial form items
     */
    private Collection<InitialFormItem> getInitialItems() throws BadFetchError {
        if (initalItems == null) {
            final VoiceXmlInterpreterContext ctx =
                getVoiceXmlInterpreterContext();
            final Collection<FormItem> formItems = dialog.getFormItems(ctx);
            initalItems = new java.util.ArrayList<InitialFormItem>();
            for (FormItem formItem : formItems) {
                if (formItem instanceof InitialFormItem) {
                    final InitialFormItem item = (InitialFormItem) formItem;
                    initalItems.add(item);
                }
            }
        }
        return initalItems;
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

    @Override
    public void process(final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event " + event + "...");
        }
        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result =
                recognitionEvent.getRecognitionResult();

        final Collection<InputItem> filtered = filterEvent(result);
        if ((filtered == null) || filtered.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no matching form items: processing aborted");
            }
            return;
        }
        setFilledInputItems(result, filtered);
        setInitialFormItems();
    }

    /**
     * Sets all filled input items.
     * @param result the current recognition result
     * @param filtered input items to be set
     * @throws SemanticError
     *         error setting the input item
     */
    private void setFilledInputItems(final RecognitionResult result,
            final Collection<InputItem> filtered) throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set the filled form items...");
        }
        setApplicationLastResult(result);
        for (InputItem item : filtered) {
            item.setFormItemVariable(result);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done setting the filled form items");
        }
    }

    /**
     * Determines all input items that can be filled using the given event.
     * @param result the recognition result
     * @return filtered input items
     * @exception BadFetchError
     *        error obtaining the form items
     */
    private Collection<InputItem> filterEvent(final RecognitionResult result)
        throws BadFetchError {
        if (!result.isAccepted()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result not accepted");
            }

            return null;
        }

        final SemanticInterpretation interpretation =
            result.getSemanticInterpretation();
        if (interpretation == null) {
            LOGGER.warn("result has no sematic interpretation: "
                    + "can not be processed!");
            return null;
        }
        final Collection<String> props = interpretation.getResultProperties();
        final Collection<InputItem> filtered =
            new java.util.ArrayList<InputItem>();
        final Collection<InputItem> items = getInputItems();
        for (InputItem item : items) {
            final String slot;
            if (item instanceof FieldFormItem) {
                final FieldFormItem field = (FieldFormItem) item;
                slot = field.getSlot();
            } else {
                slot = item.getName();
            }
            if (props != null) {
                for (String prop : props) {
                    if (prop.equals(slot)) {
                        filtered.add(item);
                        break;
                    }
                }
            }
        }
        return filtered;
    }

    /**
     * Sets all initial form items to <code>true</code>.
     * @exception BadFetchError
     *        error obtaining the form items
     */
    private void setInitialFormItems() throws BadFetchError {
        final Collection<InitialFormItem> items = getInitialItems();
        for (InitialFormItem item : items) {
            item.setFormItemVariable(Boolean.TRUE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventStrategy newInstance(final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm algorithm,
            final FormItem formItem) {
        final Dialog currentDialog;
        if (algorithm == null) {
            currentDialog = null;
        } else {
            currentDialog = algorithm.getDialog();
        }
        return new FormLevelRecognitionEventStrategy(ctx, interpreter,
                algorithm, currentDialog);
    }
}
