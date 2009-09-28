/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedElementError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormItemEvent;
import org.jvoicexml.event.plain.jvxml.InternalExitEvent;
import org.jvoicexml.interpreter.event.TagStrategyExecutor;
import org.jvoicexml.interpreter.formitem.BlockFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.interpreter.formitem.RecordFormItem;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;
import org.jvoicexml.interpreter.formitem.TransferFormItem;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Prompt;
import org.mozilla.javascript.Context;

/**
 * Forms are interpreted by an implicit form interpretation algorithm (FIA). The
 * FIA has a main loop that repeatedly selects a form item and then visits it.
 * The selected form item is the first in document order whose guard condition
 * is not satisfied. For instance, a field's default guard condition tests to
 * see if the field's form item variable has a value, so that if a simple form
 * contains only fields, the user will be prompted for each field in turn.
 *
 * <p>
 * Interpreting a form item generally involves:
 * <ul>
 * <li>Selecting and playing one or more prompts.</li>
 * <li> Collecting a user input, either a response that fills in one ore more
 * input items, or a throwing of some event (help, for instance). </li>
 * <li> Interpreting any <code>&lt;filled&gt;</code> action that pertained to
 * the newly filled in input items. </li>
 * </ul>
 * </p>
 *
 * <p>
 * The FIA ends when it interprets a transfer of control statements (e.g. a
 * <code>&lt;goto&gt;</code> to another dialog or document or a
 * <code>&lt;submit&gt;</code> of data to the document server). It also ends
 * with an implied <code>&lt;exit&gt;</code> when no form item remains eglible
 * to select.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.interpreter.Dialog
 * @see org.jvoicexml.interpreter.VoiceXmlInterpreter
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class FormInterpretationAlgorithm
        implements FormItemVisitor {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FormInterpretationAlgorithm.class);

    /** The dialog to be processed by this FIA. */
    private final Dialog dialog;

    /** The id of the dialog. */
    private final String id;

    /** Form items of the current dialog. */
    private final Collection<FormItem> formItems;

    /** Map of form item names to form items. */
    private final Map<String, FormItem> formItemMap;

    /** The currently processed form item. */
    private FormItem item;

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The current VoiceXML interpreter. */
    private final VoiceXmlInterpreter interpreter;

    /** Tag strategy executor. */
    private final TagStrategyExecutor executor;

    /**
     * <code>true</code> if the last loop iteration ended with a catch that
     * had no <code>&lt;reprompt&gt;</code>.
     */
    private boolean reprompt;

    /**
     * <code>true</code> active dialog changed from the last loop iteration.
     */
    private boolean activeDialogChanged;

    /** The input items that are just filled. */
    private final Set<InputItem> justFilled;

    /**
     * Construct a new FIA object.
     *
     * @param ctx
     *        the VoiceXML interpreter context.
     * @param ip
     *        the VoiceXML interpreter.
     * @param currentDialog
     *        the dialog to be interpreted.
     */
    public FormInterpretationAlgorithm(final VoiceXmlInterpreterContext ctx,
                                       final VoiceXmlInterpreter ip,
                                       final Dialog currentDialog) {
        context = ctx;
        interpreter = ip;
        dialog = currentDialog;

        formItems = new java.util.ArrayList<FormItem>();
        formItemMap = new java.util.HashMap<String, FormItem>();

        id = dialog.getId();

        justFilled = new java.util.LinkedHashSet<InputItem>();
        executor = new TagStrategyExecutor();
    }

    /**
     * Retrieves the current dialog.
     * @return the current dialog.
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * Retrieves the current <code>VoiceXmlInterpreterContext</code>.
     *
     * @return The current <code>VoiceXmlInterpreterContext</code>.
     */
    public VoiceXmlInterpreterContext getVoiceXmlInterpreterContext() {
        return context;
    }

    /**
     * Retrieves the tag strategy executor.
     * @return the tag strategy executor.
     * @since 0.7
     */
    public TagStrategyExecutor getTagStrategyExecutor() {
        return executor;
    }

    /**
     * Implementation of the initialization phase.
     *
     * <p>
     * Whenever a dialog is entered, it is initialized. Internal prompt counter
     * variables (in the dialog's dialog scope) are reset to 1. Each variable
     * (form level <code>&lt;var&gt;</code> elements and {@link FormItem}
     * variable is initialized, in document order, to undefined or to the
     * value of the relevant <code>&lt;expr&gt;</code> attribute.
     * </p>
     * @throws JVoiceXMLEvent
     *         Error initializing the {@link FormItem}s.
     */
    public void initialize() throws JVoiceXMLEvent {
        LOGGER.info("initializing FIA for dialog '" + id + "'...");

        // Initialize internal variables.
        reprompt = false;
        activeDialogChanged = true;
        formItems.clear();
        formItemMap.clear();

        // Initialize the form items.
        final Collection<FormItem> dialogItems =
            dialog.getFormItems(context);
        for (FormItem current : dialogItems) {
            initFormItem(current);
        }

        // Initialize variables etc.
        final TagStrategyFactory factory = new org.jvoicexml.interpreter.
                                           tagstrategy.
                                           InitializationTagStrategyFactory();
        final Collection<XmlNode> children = dialog.getChildNodes();
        for (XmlNode currentNode : children) {
            if (currentNode instanceof VoiceXmlNode) {
                final VoiceXmlNode node = (VoiceXmlNode) currentNode;
                final TagStrategy strategy = factory.getTagStrategy(node);
                if (strategy != null) {
                    strategy.getAttributes(context, node);
                    strategy.evalAttributes(context);
                    if (LOGGER.isDebugEnabled()) {
                        strategy.dumpNode(node);
                    }
                    strategy.validateAttributes();
                    strategy.execute(context, interpreter, this, null,
                            node);
                }
            }
        }

        // Print a short summary.
        final int elements = formItems.size();
        LOGGER.info("found " + elements + " form items in dialog '" + id
                + "'...");
    }

    /**
     * Initializes the given {@link FormItem}.
     *
     * @param formItem
     *        The item to initialize.
     * @since 0.4
     * @exception SemanticError
     *         error initializing the {@link FormItem}s.
     * @exception BadFetchError
     *         error initializing the {@link FormItem}s.
     */
    private void initFormItem(final FormItem formItem)
        throws SemanticError, BadFetchError {
        final String name = formItem.getName();
        formItems.add(formItem);
        final FormItem previousItem = formItemMap.put(name, formItem);
        if (previousItem != null) {
            throw new BadFetchError("Duplicate form item name '"
                    + name + "'");
        }

        final Object expression = formItem.getExpression();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing form item '" + name + "'");
        }

        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(name, expression);
        LOGGER.info("initialized form item '" + name + "' with '" + expression
                + "'");

        if (formItem instanceof InputItem) {
            final InputItem field = (InputItem) formItem;
            if (formItem instanceof PromptCountable) {
                final PromptCountable countable = (PromptCountable) formItem;
                countable.resetPromptCount();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("initialized prompt counter for field '"
                                 + name + "'");
                }
            }

            field.resetEventCounter();
        }
    }

    /**
     * Retrieves the {@link FormItem} with the given name.
     *
     * @param name
     *        Name of the {@link FormItem}
     * @return Corresponding {@link FormItem}, <code>null</code> if it does not
     *         exist.
     * @since 0.3.1
     */
    public FormItem getFormItem(final String name) {
        return formItemMap.get(name);
    }

    /**
     * Retrieves all {@link FormItem}s of the current dialog of the current
     * dialog.
     *
     * @return Collection of all {@link FormItem}s.
     * @since 0.3.1
     */
    public Collection<FormItem> getFormItems() {
        return formItems;
    }

    /**
     * Retrieves the currently processed {@link FormItem}.
     * @return the current form item.
     * @since 0.7
     */
    public FormItem getFormItem() {
        return item;
    }

    /**
     * The main loop of the FIA has three phases:
     *
     * <p>
     * The <em>select</em> phase: The next unfilled {@link FormItem} for visiting.
     * </p>
     *
     * <p>
     * The <em>collect</em> phase: the selected {@link FormItem} is visited, which
     * prompts the user for input, enables the appropriate grammars, and then
     * waits for and collects an <em>input</em> (such as a spoken phrase or
     * DTMF key presses) or an <em>event</em> (such as a request for help or a
     * no input timeout.
     * </p>
     *
     * <p>
     * The <em>process</em> phase: an input is processed by filling {@link FormItem}s
     * and executing <code>&lt;var&gt;</code> elements to perform input
     * validation. An event is processed by executing the appropriate event
     * handler for that event type.
     * </p>
     *
     * @exception JVoiceXMLEvent
     *            Error or event processing the dialog.
     */
    public void mainLoop()
            throws JVoiceXMLEvent {
        LOGGER.info("starting main loop for form '" + id + "'...");

        String lastFormItem = null;
        String gotoFormItemName = null;

        do {
            if (gotoFormItemName == null) {
                item = select();
            } else {
                item = getFormItem(gotoFormItemName);
                if (item == null) {
                    throw new BadFetchError("unable to find form item '"
                                            + gotoFormItemName + "'");
                }
                gotoFormItemName = null;
            }

            if (item != null) {
                final String name = item.getName();
                LOGGER.info("next form item in form '" + id + "' is '"
                        + name + "'");

                activeDialogChanged = !name.equals(lastFormItem);
                lastFormItem = name;
                try {
                    // Execute the form item
                    interpreter.setState(InterpreterState.WAITING);
                    collect(item);

                    // Process the input or event.
                    process(item);
                } catch (InternalExitEvent e) {
                    LOGGER.info("exiting...");
                    break;
                } catch (JVoiceXMLEvent e) {
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(
                                    "caught JVoiceXML event while processing",
                                    e);
                        }
                        processEvent(e);
                    } catch (GotoNextFormItemEvent ie) {
                        gotoFormItemName = ie.getItem();
                    }
                }
            }
        } while (item != null);

        LOGGER.info("no next element in dialog '" + id
                + "'. Exiting mainLoop...");
    }

    /**
     * Tries to process the given event with the help of the
     * {@link EventHandler}.
     * @param event the event to process.
     * @exception JVoiceXMLEvent the input event if the handler was not able to
     *            process the given event.
     * @since 0.7
     */
    void processEvent(final JVoiceXMLEvent event)
        throws JVoiceXMLEvent {
        final EventHandler handler = context.getEventHandler();
        handler.notifyEvent(event);
        final InputItem inputItem;
        if (item instanceof InputItem) {
            inputItem = (InputItem) item;
        } else {
            inputItem = null;
        }
        handler.processEvent(inputItem);
    }

    /**
     * Implementation of the <em>select</em> phase: the next unfilled dialog
     * item is selected for visiting.
     * <p>
     * The purpose of the select phase is to select the next {@link FormItem}
     *  to visit. This is done as follows:
     * </p>
     * <p>
     * If a <code>&lt;goto&gt;</code> from the last main loop iteration's
     * process phase specified a <code>&lt;goto nextitem&gt;</code> then the
     * specified {@link FormItem} is selected.
     * </p>
     * <p>
     * Otherwise the first {@link FormItem} whose guard condition is false is
     * chosen to be visited. If an error occurs while checking the guard
     * conditions, the event is thrown which skips the collect phase, and is
     * handled in the process phase.
     * </p>
     * <p>
     * If no guard condition is false, and the last iteration completed the form
     * without encountering an explicit transfer of control, the FIA does an
     * implicit <code>&lt;exit&gt;</code> operation (similarly, if execution
     * proceeds outside of a form, such as when an error is generated outside of
     * a dialog, and there is no explicit transfer of control, the interpreter
     * will perform an implicit <code>&lt;exit&gt;</code> operation).
     * </p>
     *
     * @return Next unfilled {@link FormItem}, <code>null</code> if there is
     * none.
     * @exception SemanticError error evaluating the form item.
     */
    private FormItem select() throws SemanticError {
        LOGGER.info("selecting next form item in dialog '" + id + "'...");

        for (FormItem formItem : formItems) {
            /**
             * @todo Implement error throwing in case of an error while
             *       evaluating the guard condition.
             */
            if (formItem.isSelectable()) {
                return formItem;
            }
        }

        return null;
    }

    /**
     * Implementation of the <em>collect</em> phase: the selected {@link FormItem} is
     * visited, which prompts the user for input, enables the appropriate
     * grammars, and then waits for and collects an <em>input</em> (such as a
     * spoken phrase or DTMF key presses) or an <em>event</em> (such as a
     * request for help or a no input timeout).<br>
     * <p>
     * The purpose of the collect phase is to collect an input or an event. The
     * selected {@link FormItem} is visited, which performs actions that depend
     * on the type of {@link FormItem}.
     * </p>
     *
     * @param formItem
     *        The {@link FormItem} to visit.
     *
     * @exception JVoiceXMLEvent
     *            Error or event visiting the {@link FormItem}.
     * @see #select()
     */
    private void collect(final FormItem formItem)
            throws JVoiceXMLEvent {
        if (formItem == null) {
            LOGGER.warn("no item given: cannot collect.");

            return;
        }

        LOGGER.info("collecting '" + formItem.getName() + "'...");

        // Clear the event handler cache.
        final EventHandler handler = context.getEventHandler();
        handler.notifyEvent(null);

        // unless ( the last loop iteration ended with
        //          a catch that had no <reprompt>,
        //          and the active dialog was not changed )
        if (!(!reprompt && !activeDialogChanged)) {
            if (formItem instanceof PromptCountable) {
                final PromptCountable countable = (PromptCountable) formItem;
                // Select the appropriate prompts for an input item or
                // <initial>.
                // Queue the selected prompts for play prior to
                // the next collect operation
                queuePrompts(formItem, countable);
                // Increment an input item's or <initial>'s prompt counter.
                countable.incrementPromptCount();
            }
        }

        reprompt = false;
        activeDialogChanged = false;

        // Activate grammars for the form item.
        if (formItem.isModal()) {
            activateModalGrammars(formItem);
        } else {
            activateGrammars(formItem);
        }

        // Execute the form item.
        formItem.accept(this);
    }

    /**
     * Implementation of the <em>process</em> phase. The purpose of the
     * process phase is to process the input or event collected during the
     * previous phases
     *
     * @param formItem
     *        The current {@link FormItem}.
     *
     * @exception JVoiceXMLEvent
     *            Error processing the event.
     */
    private void process(final FormItem formItem)
            throws JVoiceXMLEvent {
        interpreter.setState(InterpreterState.TRANSITIONING);

        LOGGER.info("processing '" + formItem.getName() + "'...");

        // Clear all "just_filled" flags.
        justFilled.clear();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cleared all just_filled flags");
        }

        // If there is an input item or an initial form item, wait until the
        // events coming from the implementation platform are processed.
        final EventHandler handler = context.getEventHandler();
        final boolean isInputItem = formItem instanceof InputItem;
        final boolean isInitialItem = formItem instanceof InitialFormItem;
        if ((isInputItem || isInitialItem)
                && !interpreter.isInFinalProcessingState()) {
            interpreter.setState(InterpreterState.WAITING);
            handler.waitEvent();
        }

        // Do some cleanup before continuing.
        final ImplementationPlatform platform =
                context.getImplementationPlatform();
        final UserInput userInput = platform.getUserInput();
        if (userInput != null) {
            userInput.stopRecognition();
            deactivateGrammars(formItem);
        }
        final CallControl call = platform.getCallControl();
        if (call != null) {
            platform.waitNonBargeInPlayed();
            call.stopPlay();
            call.stopRecord();
        }

        // If there is an input item or an initial form item, wait for the
        // event coming from the implementation platform.
        if (isInputItem || isInitialItem) {
            final CatchContainer container = (CatchContainer) formItem;
            handler.processEvent(container);
        }

        if (reprompt) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("reprompt: clearing all just filled elements");
            }
            final Object undefined = Context.getUndefinedValue();
            final ScriptingEngine scripting = context.getScriptingEngine();

            for (InputItem input : justFilled) {
                final String name = input.getName();
                scripting.setVariable(name, undefined);
            }
        }
    }

    /**
     * Sets the <code>just_filled</code> flag for the given input item.
     * @param input the input item.
     *
     * @since 0.5.1
     */
    public void setJustFilled(final InputItem input) {
        justFilled.add(input);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set just_filled for '" + input.getName() + "'");
        }
    }

    /**
     * Checks, if the <code>just_filled</code> flag is set for the given
     * input item.
     * @param input the input item
     * @return <code>true</code> if the flag is set.
     * @since 0.7
     */
    public boolean isJustFilled(final InputItem input) {
        return justFilled.contains(input);
    }

    /**
     * Selects the appropriate prompts for an input item or
     * <code>&lt;initial&gt;</code>. Queue the selected prompts for play
     * prior to the next collect operation.
     *
     * @param formItem
     *        the current {@link FormItem}.
     * @param countable
     *        the prompt countable.
     * @throws JVoiceXMLEvent
     *         Error collecting the prompts or in prompt evaluation.
     */
    private void queuePrompts(final FormItem formItem,
            final PromptCountable countable)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queuing prompts...");
        }
        final PromptChooser promptChooser =
                new PromptChooser(countable, context);

        final Collection<Prompt> prompts = promptChooser.collect();

        for (Prompt prompt : prompts) {
            executor.executeTagStrategy(context, interpreter, this, formItem,
                    prompt);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...queued prompts");
        }
    }


    /**
     * Process the given grammar tags and add them to the
     * {@link GrammarRegistry}.
     * @param grammar grammar to process.
     * @return the processed grammar.
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    public ProcessedGrammar processGrammar(final Grammar grammar)
        throws UnsupportedFormatError, NoresourceError, BadFetchError {
        final ActiveGrammarSet grammars = context.getActiveGrammarSet();
        final GrammarProcessor processor = context.getGrammarProcessor();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("preprocessing grammar " + grammar + "...");
        }
        // TODO read the fetch attributes from the grammar
        return processor.process(context, null, grammar, grammars);
    }

    /**
     * Process the given grammar tags and add them to the
     * {@link GrammarRegistry}.
     * @param grammarContainer the field for which to process the grammars.
     * @param grammars grammars to process.
     * @return processed grammars
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    private Collection<ProcessedGrammar> processGrammars(
            final GrammarContainer grammarContainer,
            final Collection<Grammar> grammars)
        throws UnsupportedFormatError, NoresourceError, BadFetchError {
        final Collection<ProcessedGrammar> processedGrammars =
            new java.util.ArrayList<ProcessedGrammar>();
        if (grammars.size() == 0) {
            return processedGrammars;
        }

        for (Grammar grammar : grammars) {
            final ProcessedGrammar processed = processGrammar(grammar);
            processedGrammars.add(processed);
            final GrammarImplementation<?> impl = processed.getImplementation();
            grammarContainer.addGrammar(impl);
        }
        return processedGrammars;
    }

    /**
     * Activates grammars for the {@link FormItem}.
     *
     * <p>
     * Set the active grammar set to the {@link FormItem} grammars and any grammars
     * scoped to the dialog, the current document, and the application root
     * document.
     * </p>
     *
     * <p>
     * Set the active grammar set to the {@link FormItem} grammars, if any.
     * </p>
     *
     * @param formItem
     *        The {@link FormItem} for which the grammars should be activated.
     * @exception BadFetchError
     *            Error retrieving the grammar from the given URI.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     * @exception UnsupportedFormatError
     *            Error in the grammar's format.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    private void activateModalGrammars(final FormItem formItem)
            throws BadFetchError, ConnectionDisconnectHangupEvent,
            UnsupportedLanguageError, NoresourceError, UnsupportedFormatError {
        if (!(formItem instanceof GrammarContainer)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating modal grammars...");
        }

        final GrammarContainer grammarContainer = (GrammarContainer) formItem;
        final Collection<Grammar> grammars = grammarContainer.getGrammars();

        // Activate grammars only if there are grammars in the container.
        if (grammars.size() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...no modal grammars to activate");
            }
            return;
        }
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();
        final Collection<ProcessedGrammar> processedGrammars =
            processGrammars(grammarContainer, grammars);
        final Collection<GrammarImplementation<?>> grammarImplementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        for (ProcessedGrammar processed : processedGrammars) {
            final GrammarImplementation<?> impl = processed.getImplementation();
            grammarImplementations.add(impl);
        }
        input.activateGrammars(grammarImplementations);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...modal grammars activated");
        }
    }

    /**
     * Activates grammars for the {@link FormItem}.
     *
     * <p>
     * Set the active grammar set to the {@link FormItem} grammars and any grammars
     * scoped to the dialog, the current document, and the application root
     * document.
     * </p>
     *
     * <p>
     * Set the active grammar set to the {@link FormItem} grammars, if any.
     * </p>
     *
     * @param formItem
     *        The {@link FormItem} for which the grammars should be activated.
     * @exception BadFetchError
     *            Error retrieving the grammar from the given URI.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     * @exception UnsupportedFormatError
     *            Error in the grammar's format.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     * @exception SemanticError
     *            if there are no grammars to activate
     */
    private void activateGrammars(final FormItem formItem)
            throws BadFetchError, ConnectionDisconnectHangupEvent,
            UnsupportedLanguageError, NoresourceError, UnsupportedFormatError,
            SemanticError {
        final boolean isInitialItem = formItem instanceof InitialFormItem;
        final boolean isGrammarContainer = formItem instanceof GrammarContainer;
        if (!isGrammarContainer && !isInitialItem) {
            return;
        }

        // Process the grammars of the grammar container
        if (isGrammarContainer) {
            final GrammarContainer grammarContainer =
                (GrammarContainer) formItem;
            final Collection<Grammar> grammars = grammarContainer.getGrammars();
            processGrammars(grammarContainer, grammars);
        }
        final ActiveGrammarSet activeGrammars = context.getActiveGrammarSet();

        // Activate grammars only if there are already grammars with dialog
        // scope or grammars in the field.
        if (activeGrammars.size() == 0) {
            throw new SemanticError(
                    "No grammars defined for the input of form item '"
                    + formItem.getName() + "'!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating " + activeGrammars.size()
                    + " grammars...");
        }
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();
        final Collection<GrammarImplementation<?>> implementations =
            activeGrammars.getImplementations();
        input.activateGrammars(implementations);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...grammars activated");
        }
    }

    /**
     * Deactivates grammars for the {@link FormItem}.
     *
     * @param formItem
     *        The {@link FormItem} for which the grammars should be deactivated.
     *
     * @exception BadFetchError
     *            Error retrieving the grammar from the given URI.
     * @exception NoresourceError
     *            The input resource is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     *
     * @since 0.6
     */
    private void deactivateGrammars(final FormItem formItem)
        throws NoresourceError, ConnectionDisconnectHangupEvent, BadFetchError {
        if (!(formItem instanceof GrammarContainer)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deactivating grammars...");
        }

        final ActiveGrammarSet grammars = context.getActiveGrammarSet();
        final Collection<GrammarImplementation<?>> implementations =
            grammars.getImplementations();
        if (grammars.size() > 0) {
            final ImplementationPlatform platform = context.
                    getImplementationPlatform();
            final UserInput input = platform.getUserInput();
            input.deactivateGrammars(implementations);
        }
    }

    /**
     * {@inheritDoc}
     *
     * A <code>&lt;block&gt;</code> element is visited by setting its dialog
     * item variable to <code>true</code>, evaluating its content, and then
     * bypassing the process phase.
     */
    public void visitBlockFormItem(final BlockFormItem block)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting block '" + block.getName() + "'...");
        }

        context.enterScope(Scope.ANONYMOUS);
        block.setVisited();

        try {
            executor.executeChildNodes(context, interpreter, this, block);
        } finally {
            context.exitScope(Scope.ANONYMOUS);
        }
    }

    /**
     * {@inheritDoc}
     *
     * If a <code>&lt;field&gt;</code> is visited, the FIA selects and queues
     * up any prompts based on the item's prompt counter and prompt conditions.
     * Then it activates and listens for the field level grammar(s) and any
     * higher-level grammars, and waits for the item to be filled or for some
     * events to be generated.
     */
    public void visitFieldFormItem(final InputItem field)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting field '" + field.getName() + "'...");
        }

        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();

        // Add the handlers.
        final EventHandler handler = context.getEventHandler();
        handler.collect(context, interpreter, this, field);

        platform.setEventHandler(handler);
        platform.waitNonBargeInPlayed();

        final CallControl call = platform.getCallControl();
        if (call != null) {
            try {
                call.record(input, null);
            } catch (IOException e) {
                throw new BadFetchError("error recording", e);
            }
        }

        input.startRecognition();
    }

    /**
     * {@inheritDoc}
     */
    public void visitInitialFormItem(final InitialFormItem initial)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting field '" + initial.getName() + "'...");
        }

        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();

        // Add the handlers.
        final EventHandler handler = context.getEventHandler();
        handler.collect(context, interpreter, this, initial);

        platform.setEventHandler(handler);
        platform.waitNonBargeInPlayed();

        final CallControl call = platform.getCallControl();
        if (call != null) {
            try {
                call.record(input, null);
            } catch (IOException e) {
                throw new BadFetchError("error recording", e);
            }
        }

        input.startRecognition();
    }

    /**
     * {@inheritDoc}
     */
    public void visitObjectFormItem(final ObjectFormItem object)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting object form item '" + object.getName()
                         + "'...");
        }

        // Add the handlers.
        final EventHandler handler = context.getEventHandler();
        handler.collect(context, interpreter, this, object);
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        platform.waitNonBargeInPlayed();

        // Execute...
        final ObjectExecutorThread objectExecutor =
            new ObjectExecutorThread(context, object, handler);
        objectExecutor.start();
    }

    /**
     * {@inheritDoc}
     */
    public void visitRecordFormItem(final RecordFormItem record)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting record form item '" + record.getName()
                         + "'...");
        }

        // Obtain the needed resources.
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final CallControl call = platform.getCallControl();
        final UserInput input = platform.getUserInput();

        // Add the strategies.
        final EventHandler handler = context.getEventHandler();
        handler.collect(context, interpreter, this, record);
        platform.waitNonBargeInPlayed();

        // Start the monitor for the requested recording time.
        final long maxTime = record.getMaxtime();
        final RecordingReceiverThread recording =
            new RecordingReceiverThread(handler, maxTime);
        recording.start();

        // Start recording
        final OutputStream stream = recording.getOutputStream();
        try {
            call.startRecording(input, stream, null);
        } catch (IOException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this visitSubdialogFormItem method.
     */
    public void visitSubdialogFormItem(final SubdialogFormItem
                                               subdialog)
            throws JVoiceXMLEvent {
        throw new UnsupportedElementError("subdialog");
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement bridge transfer.
     * @todo Have to send event "connection.disconnect.transfer"
     */
    public void visitTransferFormItem(final TransferFormItem transfer)
            throws JVoiceXMLEvent {

        final ImplementationPlatform platform =
            context.getImplementationPlatform();

        final CallControl call = platform.getCallControl();

        // Evaluate the type of transfer (bridge or blind)
        boolean bridge = transfer.isBridged();
        final String dest = transfer.getDest();
        if (bridge) {
            // Process a bridge transfer
            LOGGER.warn("bridge transfer not yet implemented!");
        }

        // Add the handlers.
        final EventHandler handler = context.getEventHandler();
        handler.collect(context, interpreter, this, transfer);
        platform.waitNonBargeInPlayed();

        platform.setEventHandler(handler);

        // Transfer
        call.transfer(dest);
    }

    /**
     * Set if the last loop iteration ended with a <code>&lt;catch&gt;</code>
     * that had no <code>&lt;reprompt&gt;</code>.
     *
     * @param on
     *        <code>true</code> if a catch occurred that had no reprompt.
     */
    public void setReprompt(final boolean on) {
        reprompt = on;
    }
}
