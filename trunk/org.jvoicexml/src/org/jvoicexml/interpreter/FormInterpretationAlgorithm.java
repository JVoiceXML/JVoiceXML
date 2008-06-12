/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Set;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.CallControl;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.jvxml.GotoNextFormItemEvent;
import org.jvoicexml.event.plain.jvxml.InternalExitEvent;
import org.jvoicexml.interpreter.event.ObjectTagEventStrategy;
import org.jvoicexml.interpreter.event.RecognitionEventStrategy;
import org.jvoicexml.interpreter.event.RecordingEventStrategy;
import org.jvoicexml.interpreter.formitem.BlockFormItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.interpreter.formitem.PromptCountable;
import org.jvoicexml.interpreter.formitem.RecordFormItem;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;
import org.jvoicexml.interpreter.formitem.TransferFormItem;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Prompt;
import org.mozilla.javascript.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Forms are interpreted by an implicit form interpretation algorithm (FIA). The
 * fia has a main loop that repeatedly selects a form item and then visits it.
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
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
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
    private Collection<FormItem> formItems;

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The current VoiceXML interpreter. */
    private final VoiceXmlInterpreter interpreter;

    /** The factory for tag strategies. */
    private final TagStrategyFactory tagstrategyFactory;

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
     *        The VoiceXML interpreter context.
     * @param ip
     *        The VoiceXML interpreter.
     * @param currentForm
     *        The Form to be interpreted.
     */
    public FormInterpretationAlgorithm(final VoiceXmlInterpreterContext ctx,
                                       final VoiceXmlInterpreter ip,
                                       final Dialog currentForm) {
        context = ctx;
        interpreter = ip;
        dialog = currentForm;

        formItems = new java.util.ArrayList<FormItem>();
        id = dialog.getId();

        tagstrategyFactory = new org.jvoicexml.interpreter.tagstrategy.
                             JVoiceXmlTagStrategyFactory();

        justFilled = new java.util.LinkedHashSet<InputItem>();
    }

    /**
     * Retrieve the current <code>VoiceXmlInterpreterContext</code>.
     *
     * @return The current <code>VoiceXmlInterpreterContext</code>.
     */
    public VoiceXmlInterpreterContext getVoiceXmlInterpreterContext() {
        return context;
    }

    /**
     * Implementation of the initialization phase.
     *
     * <p>
     * Whenever a dialog is entered, it is initialized. Internal prompt counter
     * variables (in the dialog's dialog scope) are reset to 1. Each variable
     * (form level <code>&lt;var&gt;</code> elements and {@link FormItem} variable is
     * initialized, in document order, to undefined or to the value of the
     * relevant <code>&lt;expr&gt;</code> attribute.
     * </p>
     * @throws BadFetchError
     *         Error initializing the {@link FormItem}s.
     *
     */
    public void initialize() throws BadFetchError {
        LOGGER.info("initializing FIA for dialog '" + id + "'...");

        reprompt = false;
        activeDialogChanged = true;

        formItems = dialog.getFormItems(context);

        for (FormItem item : formItems) {
            initFormItem(item);
        }

        final TagStrategyFactory factory = new org.jvoicexml.interpreter.
                                           tagstrategy.
                                           InitializationTagStrategyFactory();

        final NodeList children = dialog.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final Node currentNode = children.item(i);
            if (currentNode instanceof VoiceXmlNode) {
                final VoiceXmlNode node = (VoiceXmlNode) currentNode;
                final TagStrategy strategy = factory.getTagStrategy(node);

                if (strategy != null) {
                    try {
                        strategy.getAttributes(context, node);
                        strategy.evalAttributes(context);
                        if (LOGGER.isDebugEnabled()) {
                            strategy.dumpNode(node);
                        }
                        strategy.validateAttributes();
                        strategy.execute(context, interpreter, this, null,
                                         node);
                    } catch (JVoiceXMLEvent event) {
                        LOGGER.error("error initializing", event);
                    }
                }
            }

            final int elements = formItems.size();
            LOGGER.info("found " + elements + " form items in dialog '" + id
                    + "'...");
        }
    }

    /**
     * Initializes the given <code>FormItem</code>.
     *
     * @param item
     *        The item to initialize.
     * @since 0.4
     */
    private void initFormItem(final FormItem item) {
        final String name = item.getName();
        final String expression = item.getExpr();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing form item '" + name + "'");
        }

        createVariable(name, expression);

        if (item instanceof InputItem) {
            final InputItem field = (InputItem) item;
            if (item instanceof PromptCountable) {
                final PromptCountable countable = (PromptCountable) item;
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
        for (FormItem item : formItems) {
            final String currentname = item.getName();
            if (name.equalsIgnoreCase(currentname)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Retrieves all {@link FormItem}s of the current dialog of the current dialog.
     *
     * @return Collection of all {@link FormItem}s.
     * @since 0.3.1
     */
    public Collection<FormItem> getFormItems() {
        return formItems;
    }

    /**
     * Creates the var with the given name and a value of the evaluated
     * expression.
     *
     * @param name
     *        Name of the var to create.
     * @param expr
     *        Expression to be evaluated.
     */
    public void createVariable(final String name, final String expr) {
        if (name == null) {
            return;
        }

        try {
            final ScriptingEngine scripting = context.getScriptingEngine();
            final Object value = scripting.eval(expr);
            scripting.setVariable(name, value);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("created var: '" + name + "' with value '" + value
                             + "'");
            }
        } catch (SemanticError se) {
            LOGGER.warn("unable to evaluate the expression", se);
        }
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
        FormItem item;
        String gotoFormItemName = null;

        do {
            if (gotoFormItemName == null) {
                item = select();
            } else {
                item = select(gotoFormItemName);

                if (item == null) {
                    throw new BadFetchError("unable to find form item '"
                                            + gotoFormItemName + "'");
                }
            }

            gotoFormItemName = null;

            if (item != null) {
                final String name = item.getName();
                LOGGER.info("next form item in form '" + id + "' is '"
                        + name + "'");

                activeDialogChanged = !name.equals(lastFormItem);
                lastFormItem = name;
                try {
                    // Execute the form item
                    interpreter.setState(InterpreterState.WAITING);
                    final EventHandler handler = collect(item);

                    // Process the input or event.
                    process(item, handler);
                } catch (GotoNextFormItemEvent e) {
                    gotoFormItemName = e.getItem();
                } catch (InternalExitEvent e) {
                    LOGGER.info("exiting...");
                    break;
                }
            }
        } while (item != null);

        LOGGER.info("no next element in dialog '" + id
                + "'. Exiting mainLoop...");
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
     * @return Next unfilled {@link FormItem}, <code>null</code> if there is none.
     */
    private FormItem select() {
        LOGGER.info("selecting next form item in dialog '" + id + "'...");

        for (FormItem item : formItems) {
            /**
             * @todo Implement error throwing in case of an error while
             *       evaluating the guard condition.
             */
            if (item.isSelectable()) {
                return item;
            }
        }

        return null;
    }

    /**
     * Select the next {@link FormItem} as a result of a <code>&lt;goto&gt;</code>.
     *
     * @param name
     *        name of the next {@link FormItem}.
     * @return Next <code>FormItem</code>, <code>null</code> if there is no
     *         {@link FormItem} with that name.
     */
    private FormItem select(final String name) {
        LOGGER.info("selecting goto form item '" + name + "' in dialog '"
                + id + "'...");

        if (name == null) {
            return null;
        }

        for (FormItem item : formItems) {
            final String currentName = item.getName();
            if (name.equals(currentName)) {
                return item;
            }
        }

        LOGGER.warn("item '" + name + "' does not exist!");

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
     * @param item
     *        The {@link FormItem} to visit.
     * @return The event handler to use for the processing phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event visiting the {@link FormItem}.
     * @see #select()
     */
    private EventHandler collect(final FormItem item)
            throws JVoiceXMLEvent {
        if (item == null) {
            LOGGER.warn("no item given: cannot collect.");

            return null;
        }

        LOGGER.info("collecting '" + item.getName() + "'...");

        // unless ( the last loop iteration ended with
        //          a catch that had no <reprompt>,
        //          and the active dialog was not changed )
        if (!(!reprompt && !activeDialogChanged)) {
            if (item instanceof PromptCountable) {
                final PromptCountable countable = (PromptCountable) item;
                // Select the appropriate prompts for an input item or
                // <initial>.
                // Queue the selected prompts for play prior to
                // the next collect operation
                queuePrompts(item, countable);
                // Increment an input item's or <initial>'s prompt counter.
                countable.incrementPromptCount();
            }
        }

        reprompt = false;
        activeDialogChanged = false;

        // Activate grammars for the form item.
        activateGrammars(item);

        // Execute the form item.
        return item.accept(this);
    }

    /**
     * Implementation of the <em>process</em> phase. The purpose of the
     * process phase is to process the input or event collected during the
     * previous phases
     *
     * @param item
     *        The current {@link FormItem}.
     * @param handler
     *        The event handler.
     *
     * @exception JVoiceXMLEvent
     *            Error processing the event.
     */
    private void process(final FormItem item, final EventHandler handler)
            throws JVoiceXMLEvent {
        interpreter.setState(InterpreterState.TRANSITIONING);

        LOGGER.info("processing '" + item.getName() + "'...");

        // Clear all "just_filled" flags.
        justFilled.clear();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cleared all just_filled flags");
        }

        // If there is an event handler, wait until the events coming
        // from the implementation platform are processed.
        if ((handler != null) && !interpreter.isInFinalProcessingState()) {
            interpreter.setState(InterpreterState.WAITING);
            handler.waitEvent();
        }

        // Do some cleanup before continuing.
        final ImplementationPlatform platform =
                context.getImplementationPlatform();
        final UserInput userInput = platform.getBorrowedUserInput();
        if (userInput != null) {
            userInput.stopRecognition();
            deactivateGrammars(item);
            platform.returnUserInput(userInput);
        }
        final CallControl call = platform.getBorrowedCallControl();
        if (call != null) {
            call.stopPlay();
            call.stopRecord();
            platform.returnCallControl(call);
        }

        /** @todo Replace this by a proper solution. */
        if (item instanceof InputItem) {
            InputItem field = (InputItem) item;

            if (handler != null) {
                handler.processEvent(field);
            }
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
     * @param input The input.
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
     * Selects the appropriate prompts for an input item or
     * <code>&lt;initial&gt;</code>. Queue the selected prompts for play
     * prior to the next collect operation.
     *
     * @param item
     *        the current {@link FormItem}.
     * @param countable
     *        the prompt countable.
     * @throws JVoiceXMLEvent
     *         Error collecting the prompts or in prompt evaluation.
     */
    private void queuePrompts(final FormItem item,
            final PromptCountable countable)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queuing prompts...");
        }
        final PromptChooser promptChooser =
                new PromptChooser(countable, context);

        final Collection<Prompt> prompts = promptChooser.collect();

        for (Prompt prompt : prompts) {
            executeTagStrategy(item, prompt);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...queued prompts");
        }
    }


    /**
     * Process the given grammar tags and add them to the
     * {@link GrammarRegistry}.
     * @param grammar grammar to process.
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    public void processGrammar(final Grammar grammar)
        throws UnsupportedFormatError, NoresourceError, BadFetchError {
        final GrammarRegistry registry = context.getGrammarRegistry();
        final GrammarProcessor processor = context.getGrammarProcessor();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("preprocessing grammar '" + grammar.getSrc() + "'...");
        }
        final Application application = context.getApplication();
        final FetchAttributes attributes;
        if (application == null) {
            attributes = null;
        } else {
            attributes = application.getFetchAttributes();
        }
        processor.process(context, attributes, grammar, registry);
    }

    /**
     * Process the given grammar tags and add them to the
     * {@link GrammarRegistry}.
     * @param grammars grammars to process.
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    private void processGrammars(final Collection<Grammar> grammars)
        throws UnsupportedFormatError, NoresourceError, BadFetchError {
        if (grammars.size() == 0) {
            return;
        }

        for (Grammar grammar : grammars) {
            processGrammar(grammar);
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
     * @param item
     *        The {@link FormItem} for which the grammars should be activated.
     * @exception BadFetchError
     *            Error retrieving the grammar from the given URI.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     * @exception UnsupportedFormatError
     *            Error in the grammar's format.
     */
    private void activateGrammars(final FormItem item)
            throws BadFetchError,
            UnsupportedLanguageError, NoresourceError, UnsupportedFormatError {
        if (!(item instanceof FieldFormItem)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating grammars...");
        }

        final FieldFormItem field = (FieldFormItem) item;
        final Collection<Grammar> grammars = field.getGrammars();
        final GrammarRegistry registry = context.getGrammarRegistry();
        final Collection<GrammarImplementation<?>> dialogGrammars =
            registry.getGrammars();

        // Activate grammars only if there are already grammars with dialog
        // scope or grammars in the field.
        if ((grammars.size() > 0) || (dialogGrammars.size() > 0)) {
            final ImplementationPlatform platform =
                context.getImplementationPlatform();
            final UserInput input = platform.borrowUserInput();
            Throwable error = null;
            try {
                if (grammars.size() > 0) {
                    processGrammars(grammars);
                }
                final Collection<GrammarImplementation<? extends Object>>
                    currentGrammars = registry.getGrammars();
                if (field.isModal()) {
                    final Collection<GrammarImplementation<?>> fieldGrammars =
                        new java.util.ArrayList<GrammarImplementation<?>>(
                                currentGrammars);
                    fieldGrammars.removeAll(dialogGrammars);
                    input.activateGrammars(fieldGrammars);
                } else {
                    input.activateGrammars(currentGrammars);
                }
            } catch (Exception e) {
                error = e;
            } catch (JVoiceXMLEvent e) {
                error = e;
            } finally {
                if (error != null) {
                    platform.returnUserInput(input);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...grammars activated");
        }
    }

    /**
     * Deactivates grammars for the {@link FormItem}.
     *
     * @param item
     *        The {@link FormItem} for which the grammars should be deactivated.
     *
     * @exception BadFetchError
     *            Error retrieving the grammar from the given URI.
     * @exception NoresourceError
     *            The input resource is not available.
     *
     * @since 0.6
     */
    private void deactivateGrammars(final FormItem item)
        throws NoresourceError, BadFetchError {
        if (!(item instanceof FieldFormItem)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deactivating grammars...");
        }

        final GrammarRegistry registry = context.getGrammarRegistry();
        final Collection<GrammarImplementation<?>> grammars =
            registry.getGrammars();
        if (grammars.size() > 0) {
            final ImplementationPlatform platform = context.
                    getImplementationPlatform();
            final UserInput input = platform.getBorrowedUserInput();

            input.deactivateGrammars(grammars);
        }
    }

    /**
     * {@inheritDoc}
     *
     * A <code>&lt;block&gt;</code> element is visited by setting its dialog
     * item variable to <code>true</code>, evaluating its content, and then
     * bypassing the process phase.
     */
    public EventHandler visitBlockFormItem(final BlockFormItem block)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting block '" + block.getName() + "'...");
        }

        context.enterScope(Scope.ANONYMOUS);
        block.setVisited();

        try {
            executeChildNodes(block);
        } finally {
            context.exitScope(Scope.ANONYMOUS);
        }

        return null;
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
    public EventHandler visitFieldFormItem(final InputItem field)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting field '" + field.getName() + "'...");
        }

        // Usually the input device is borrowed when the grammars are
        // processed. If this did not happen, borrow a new input.
        final ImplementationPlatform platform = context
                .getImplementationPlatform();
        UserInput input = platform.getBorrowedUserInput();
        if (input == null) {
            input = platform.borrowUserInput();
        }

        // Add the handlers.
        final ScopeObserver observer = context.getScopeObserver();
        final EventHandler handler = new org.jvoicexml.interpreter.event.
                                     JVoiceXmlEventHandler(observer);
        handler.collect(context, interpreter, this, field);

        // We need at least a handler to process the recognition result.
        final RecognitionEventStrategy event = new RecognitionEventStrategy(
                context, interpreter, this, field);
        handler.addStrategy(event);

        platform.setEventHandler(handler);

        /** @todo Have to synch with bargein */
        platform.waitOutputQueueEmpty();

        final CallControl call = platform.borrowCallControl();
        if (call != null) {
            try {
                call.record(input, null);
            } catch (IOException e) {
                throw new BadFetchError("error recording", e);
            }
        }

        input.startRecognition();

        return handler;
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this visitInitialFormItem method.
     */
    public EventHandler visitInitialFormItem(final InitialFormItem initial)
            throws JVoiceXMLEvent {
        LOGGER.warn("visiting of initial form items is not implemented!");

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EventHandler visitObjectFormItem(final ObjectFormItem object)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting object form item '" + object.getName()
                         + "'...");
        }

        // Add the handlers.
        final ScopeObserver observer = context.getScopeObserver();
        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(observer);
        handler.collect(context, interpreter, this, object);

        // We need at least a handler to process the recognition result.
        final ObjectTagEventStrategy event = new ObjectTagEventStrategy(
                context, interpreter, this, object);
        handler.addStrategy(event);

        // Execute...
        final ObjectExecutorThread executor =
            new ObjectExecutorThread(context, object, handler);
        executor.start();

        return handler;
    }

    /**
     * {@inheritDoc}
     */
    public EventHandler visitRecordFormItem(final RecordFormItem record)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting record form item '" + record.getName()
                         + "'...");
        }

        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        platform.waitOutputQueueEmpty();

        // Obtain the needed resources.
        final CallControl call = platform.borrowCallControl();
        final UserInput input = platform.borrowUserInput();

        // Add the strategies.
        final ScopeObserver observer = context.getScopeObserver();
        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(observer);
        final AudioFormat format = call.getRecordingAudioFormat();
        final RecordingEventStrategy strategy =
                new RecordingEventStrategy(context, interpreter, this, record,
                format);
        handler.addStrategy(strategy);
        handler.collect(context, interpreter, this, record);

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

        return handler;
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this visitSubdialogFormItem method.
     */
    public EventHandler visitSubdialogFormItem(final SubdialogFormItem
                                               subdialog)
            throws JVoiceXMLEvent {
        LOGGER.warn("visiting of subdialog form items is not implemented!");

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement bridge transfer.
     * @todo Insure that all prompts are played before starting a blind transfer
     * @todo Have to send event "connection.disconnect.transfer"
     */
    public EventHandler visitTransferFormItem(final TransferFormItem transfer)
            throws JVoiceXMLEvent {

        final ImplementationPlatform implementation =
            context.getImplementationPlatform();

        final CallControl call = implementation.borrowCallControl();
        if (call == null) {
            LOGGER.warn("there's no call control to process transfer");
            return null;
        }

        //Evaluate the type of transfer (bridge or blind)
        boolean bridge =
            Boolean.valueOf(transfer.getNode().getAttribute("bridge"));
        if (bridge) {
            //Process a bridge transfer
            LOGGER.warn("bridge transfer not yet implemented!");
        } else {
            //Process a blind transfer

            String dest = transfer.getNode().getAttribute("dest");
            if ((dest == null) || (dest == "")) {
                dest = transfer.getNode().getAttribute("destexpr");
            }

            if ((dest == null) || (dest == "")) {
                throw new NoresourceError("Undefined URI to call");
            }

            call.transfer(dest);

          //  transfer.

            return null;
        }

        return null;
    }

    /**
     * Execute the tag strategies for all child nodes of the given {@link FormItem}.
     *
     * @param item
     *        The current {@link FormItem}.
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     */
    public void executeChildNodes(final FormItem item)
            throws JVoiceXMLEvent {
        final VoiceXmlNode currentNode = item.getNode();
        final NodeList children = currentNode.getChildNodes();

        executeChildNodes(item, children);
    }

    /**
     * Execute the <code>TagStrategy</code> for all child nodes of the given
     * parent node.
     *
     * @param item
     *        The current {@link FormItem}.
     * @param parent
     *        The parent node, which is in fact a child to item.
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     *
     * @see org.jvoicexml.interpreter.TagStrategy
     */
    public void executeChildNodes(final FormItem item,
                                  final VoiceXmlNode parent)
            throws JVoiceXMLEvent {
        final NodeList children = parent.getChildNodes();

        executeChildNodes(item, children);
    }

    /**
     * Execute the <code>TagStrategy</code> for all nodes of the given list.
     *
     * @param item
     *        The current {@link FormItem}.
     * @param list
     *        The list of nodes to execute.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     *
     * @see org.jvoicexml.interpreter.TagStrategy
     */
    public void executeChildNodes(final FormItem item, final NodeList list)
            throws JVoiceXMLEvent {
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.getLength(); i++) {
            final VoiceXmlNode node = (VoiceXmlNode) list.item(i);
            executeTagStrategy(item, node);
        }
    }

    /**
     * Executes the tag strategy for the given node.
     * @param item the current {@link FormItem}
     * @param node the node to execute.
     * @throws JVoiceXMLEvent
     *            Error or event executing the child node.
     * @since 0.6
     */
    private void executeTagStrategy(final FormItem item,
            final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final TagStrategy strategy = tagstrategyFactory.getTagStrategy(node);

        if (strategy == null) {
            return;
        }

        // Execute the node.
        strategy.getAttributes(context, node);
        strategy.evalAttributes(context);
        if (LOGGER.isDebugEnabled()) {
            strategy.dumpNode(node);
        }
        strategy.validateAttributes();
        strategy.execute(context, interpreter, this, item, node);
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
