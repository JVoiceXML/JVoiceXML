/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/event/FormLevelRecognitionEventStrategy.java $
 * Version: $LastChangedRevision: 2947 $
 * Date:    $Date: 2012-02-08 03:18:26 -0600 (mié, 08 feb 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategyExecutor;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.variables.ApplicationShadowVarContainer;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.FilledMode;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

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
 * @version $Revision: 2947 $
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
                scripting.eval(ApplicationShadowVarContainer.VARIABLE_NAME
                        + ";");

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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing filled elements...");
        }
        final Collection<Filled> filledElements =
            dialog.getFilledElements();
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        final ScriptingEngine scripting = context.getScriptingEngine();
        final FormInterpretationAlgorithm fia =
            getFormInterpretationAlgorithm();
        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();
        for (Filled filled : filledElements) {
            if (shouldExecute(filled, scripting)) {
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
     * @param filled the filled node to check
     * @param scripting the scripting engine
     * @return <code>true</code> if the filled node should be executed.
     */
    private boolean shouldExecute(final Filled filled,
            final ScriptingEngine scripting) {
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
            return areAllFilled(tokens, scripting);
        } else {
            return isAnyFilled(tokens, scripting);
        }
    }

    /**
     * Checks if all of the tokens are contained in the just filled items.
     * @param tokens tokens to be processed.
     * @param scripting the scripting engine
     * @return <code>true</code> if all input items are filled
     * @since 0.7.3
     */
    private boolean areAllFilled(final TokenList tokens,
            final ScriptingEngine scripting) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if all input items of '" + tokens
                    + "' are filled");
        }
        for (String token : tokens) {
            final Object object = scripting.getVariable(token);
            if ((object == null) || (object == Context.getUndefinedValue())) {
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
     * @param tokens tokens to be processed.
     * @param scripting the scripting engine
     * @return <code>true</code> if any input items are filled
     * @since 0.7.3
     */
    private boolean isAnyFilled(final TokenList tokens,
            final ScriptingEngine scripting) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if any input items of '" + tokens
                    + "' are filled");
        }
        for (String token : tokens) {
            final Object object = scripting.getVariable(token);
            if ((object != null) && (object != Context.getUndefinedValue())) {
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
     * @param result the current recognition result
     * @param filtered input items to be set
     * @throws SemanticError
     *         error setting the input item
     */
    private void setFilledInputItems(final RecognitionResult result,
            final Collection<InputItem> filtered) throws SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting the filled form items...");
        }
        final FormInterpretationAlgorithm fia =
            getFormInterpretationAlgorithm();
        setApplicationLastResult(result);
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

        final Object interpretation =
            result.getSemanticInterpretation();
        if ((interpretation == null)
                || (!(interpretation instanceof ScriptableObject))) {
            LOGGER.warn("result has no sematic interpretation: "
                    + "can not be processed!");
            return null;
        }
        final ScriptableObject inter = (ScriptableObject) interpretation;
        final String str = ScriptingEngine.toJSON(inter);
        LOGGER.info("semantic interpretation: '" + str + "'");
        final Collection<String> props = getResultProperties(inter);
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
            if (props != null) {
                for (String prop : props) {
                    if (prop.equals(slot)) {
                        filtered.add(item);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("added input item '"
                                    + item.getName() + "'");
                        }
                        break;
                    }
                }
            }
        }
        return filtered;
    }

    /**
     * Retrieves all result properties from the given object.
     * @param interpretation the semantic interpretation
     * @return result properties 
     */
    private Collection<String> getResultProperties(
            final ScriptableObject interpretation) {
        final Collection<String> props = new java.util.ArrayList<String>();
        addResultProperties(interpretation, "", props);
        if (LOGGER.isDebugEnabled()) {
            for (String prop : props) {
                LOGGER.debug("result property '" + prop + "'"); 
            }
        }
        return props;
    }

    /**
     * Iterate through the given object to determine all nested properties.
     * @param object the current scriptable
     * @param prefix the current prefix
     * @param props collected properties
     * @since 0.7.1
     */
    private void addResultProperties(final ScriptableObject object,
            final String prefix,
            final Collection<String> props) {
        final Object[] ids = object.getAllIds();
        for (Object o : ids) {
            final String name;
            if (prefix.isEmpty()) {
                name = o.toString();
            } else {
                name = prefix + "." + o.toString();
            }
            props.add(name);
            final Object value = object.get(name, null);
            if (value instanceof ScriptableObject) {
                final ScriptableObject scriptable = (ScriptableObject) value;
                addResultProperties(scriptable, name, props);
            }
        }
    }

    /**
     * Sets all initial form items to <code>true</code>.
     * @exception BadFetchError
     *        error obtaining the form items
     * @exception SemanticError
     *        error setting the value of the form item
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
