/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.profile.TagStrategyExecutor;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.FilledMode;

/**
 * Strategy to process a recognition event coming from the implementation
 * platform.
 *
 * <p>
 * A {@link FormLevelRecognitionEventStrategy} may be responsible to handle
 * events for multiple fields if the form contains more than one field.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 *
 * @see org.jvoicexml.ImplementationPlatform
 */
final class FormLevelRecognitionEventStrategy extends AbstractEventStrategy
        implements EventStrategyPrototype {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(FormLevelRecognitionEventStrategy.class);

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
     *            The VoiceXML interpreter context.
     * @param interpreter
     *            The VoiceXML interpreter.
     * @param algorithm
     *            The FIA.
     * @param dlg
     *            The current dialog.
     */
    public FormLevelRecognitionEventStrategy(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm algorithm, final Dialog dlg) {
        super(ctx, interpreter, algorithm, null, null,
                RecognitionEvent.EVENT_TYPE);
        dialog = dlg;
    }

    /**
     * Retrieves the input items of the dialog.
     * 
     * @return input items
     * @exception BadFetchError
     *                error obtaining the input items
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
     * 
     * @return initial form items
     * @exception BadFetchError
     *                error obtaining the initial form items
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
     * 
     * @param result
     *            the current recognition result.
     * @throws SemanticError
     *             Error creating the shadow variable.
     * @since 0.6
     */
    private void setApplicationLastResult(final RecognitionResult result)
            throws SemanticError {
        final VoiceXmlInterpreterContext context =
                getVoiceXmlInterpreterContext();
        final DataModel model = context.getDataModel();
        model.resizeArray("lastresult$", 1, Scope.APPLICATION);
        final Object value = model.readArray("lastresult$", 0,
                Scope.APPLICATION, Object.class);
        model.createVariableFor(value, "confidence", result.getConfidence());
        model.createVariableFor(value, "utterance", result.getUtterance());
        model.createVariableFor(value, "inputmode", result.getMode().name());
        model.createVariableFor(value, "interpretation",
                result.getSemanticInterpretation(model));
        model.updateArray("lastresult$", 0, value, Scope.APPLICATION);
        model.createVariable("lastresult$.interpretation",
                result.getSemanticInterpretation(model), Scope.APPLICATION);
        model.createVariable("lastresult$.confidence", result.getConfidence(),
                Scope.APPLICATION);
        model.createVariable("lastresult$.utterance", result.getUtterance(),
                Scope.APPLICATION);
        model.createVariable("lastresult$.inputmode", result.getMode().name(),
                Scope.APPLICATION);
    }

    @Override
    public void process(final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event " + event + "...");
        }
        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result = recognitionEvent
                .getRecognitionResult();
        final VoiceXmlInterpreterContext context =
                getVoiceXmlInterpreterContext();
        final DataModel model = context.getDataModel();
        setApplicationLastResult(result);
        final Collection<InputItem> filtered = filterEvent(model, result);
        if ((filtered == null) || filtered.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no matching form items: processing aborted");
            }
            return;
        }
        setFilledInputItems(result, filtered);
        setInitialFormItems();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing filled elements...");
        }
        final Collection<Filled> filledElements = dialog.getFilledElements();
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();
        for (Filled filled : filledElements) {
            if (shouldExecute(filled, model)) {
                executor.executeChildNodes(context, interpreter, fia, null,
                        filled);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done executing filled element");
        }
    }

    /**
     * Checks if the filled node should be executed.
     * 
     * @param filled
     *            the filled node to check
     * @param model
     *            the employed data model
     * @return <code>true</code> if the filled node should be executed.
     * @throws SemanticError
     *             error evaluating the variables in the namelist
     */
    private boolean shouldExecute(final Filled filled, final DataModel model)
            throws SemanticError {
        final FilledMode mode = filled.getModeObject();
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        final TokenList tokens = filled.getNameListObject();
        final Collection<FormItem> formItems = fia.getFormItems();
        if (tokens.isEmpty()) {
            for (FormItem formItem : formItems) {
                if (formItem instanceof InputItem) {
                    final String name = formItem.getName();
                    tokens.add(name);
                }
            }
        }
        // TODO check if control items are references
        if (mode == FilledMode.ALL) {
            return areAllFilled(tokens, model);
        } else {
            return isAnyFilled(tokens, model);
        }
    }

    /**
     * Checks if all of the tokens are contained in the just filled items.
     * 
     * @param tokens
     *            tokens to be processed.
     * @param model
     *            the employed data model the scripting engine
     * @return <code>true</code> if all input items are filled
     * @throws SemanticError
     *             if one of the tokens could not be evaluated
     * @since 0.7.3
     */
    private boolean areAllFilled(final TokenList tokens, final DataModel model)
            throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if all input items of '" + tokens
                    + "' are filled");
        }
        for (String token : tokens) {
            final Object object = model.readVariable(token, Object.class);
            if ((object == null) || (object == model.getUndefinedValue())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("not all filled: '" + token
                            + "' is not defined");
                }
                return false;
            }
        }
        LOGGER.info("all input items of '" + tokens + "' are filled");
        return true;
    }

    /**
     * Checks if any of the tokens are contained in the just filled items.
     * 
     * @param tokens
     *            tokens to be processed.
     * @param model
     *            the employed data model
     * @return <code>true</code> if any input items are filled
     * @throws SemanticError
     *             if one of the tokens could not be evaluated
     * @since 0.7.3
     */
    private boolean isAnyFilled(final TokenList tokens, final DataModel model)
            throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if any input items of '" + tokens
                    + "' are filled");
        }
        for (String token : tokens) {
            final Object object = model.readVariable(token, Object.class);
            if ((object != null) && (object != model.getUndefinedValue())) {
                LOGGER.info("any filled: '" + token + "' is defined");
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("none of input items of '" + tokens + "' are filled");
        }
        return false;
    }

    /**
     * Sets all filled input items.
     * 
     * @param result
     *            the current recognition result
     * @param filtered
     *            input items to be set
     * @throws SemanticError
     *             error setting the input item
     */
    private void setFilledInputItems(final RecognitionResult result,
            final Collection<InputItem> filtered) throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting the filled form items...");
        }
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        for (InputItem item : filtered) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("setting form item variable to '" + result + "'");
            }
            item.setFormItemVariable(result);
            if (fia != null) {
                fia.setJustFilled(item);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done setting the filled form items");
        }
    }

    /**
     * Determines all input items that can be filled using the given event.
     * 
     * @param model
     *            the employed data model
     * @param result
     *            the recognition result
     * @return filtered input items, {@code null} if no input items match
     * @exception BadFetchError
     *                error obtaining the form items
     * @exception SemanticError
     *                error evaluating the semantic interpretation
     */
    private Collection<InputItem> filterEvent(final DataModel model,
            final RecognitionResult result)
                    throws BadFetchError, SemanticError {
        if (!result.isAccepted()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result not accepted");
            }
            return null;
        }

        final Object interpretation = result.getSemanticInterpretation(model);
        if (interpretation == null) {
            LOGGER.warn("result has no sematic interpretation: "
                    + "can not be processed!");
            return null;
        }
        final String str = model.toString(interpretation);
        LOGGER.info("semantic interpretation: '" + str + "'");
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("checking input item '" + item.getName()
                        + "' with slot '" + slot + "'");
            }
            try {
                final Object value = model.readVariable(
                        "application.lastresult$.interpretation." + slot,
                        Object.class);
                if (value != null) {
                    filtered.add(item);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("added input item '" + item.getName()
                                + "'");
                    }
                }
            } catch (SemanticError e) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        return filtered;
    }

    /**
     * Sets all initial form items to {@code true}.
     * 
     * @exception BadFetchError
     *                error obtaining the form items
     * @exception SemanticError
     *                error setting the value of the form item
     */
    private void setInitialFormItems() throws BadFetchError, SemanticError {
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
