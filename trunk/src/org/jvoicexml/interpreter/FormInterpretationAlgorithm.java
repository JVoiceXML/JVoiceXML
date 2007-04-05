/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;
import java.util.Set;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.jvxml.GotoNextFormItemEvent;
import org.jvoicexml.event.plain.jvxml.InternalExitEvent;
import org.jvoicexml.implementation.SpeakableSsmlText;
import org.jvoicexml.interpreter.event.RecognitionEventStrategy;
import org.jvoicexml.interpreter.formitem.BlockFormItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.interpreter.formitem.PromptCountable;
import org.jvoicexml.interpreter.formitem.RecordFormItem;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;
import org.jvoicexml.interpreter.formitem.TransferFormItem;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.ssml.SsmlDocument;
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
 * @see org.jvoicexml.interpreter.VoiceXmlInterpreter
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class FormInterpretationAlgorithm
        implements FormItemVisitor {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FormInterpretationAlgorithm.class);

    /** The form to be processed by this FIA. */
    private final ExecutableForm form;

    /** The id of the form. */
    private final String id;

    /** Form items of the current form. */
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
                                       final ExecutableForm currentForm) {
        context = ctx;
        interpreter = ip;
        form = currentForm;

        formItems = new java.util.ArrayList<FormItem>();
        id = form.getId();

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
     * Whenever a form is entered, it is initialized. Internal prompt counter
     * variables (in the form's dialog scope) are reset to 1. Each variable
     * (form level <code>&lt;var&gt;</code> elements and form item variable is
     * initialized, in document order, to undefined or to the value of the
     * relevant <code>&lt;expr&gt;</code> attribute.
     * </p>
     *
     */
    public void initialize() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("initializing FIA for form '" + id + "'...");
        }

        reprompt = false;

        formItems = form.getFormItems(context);

        for (FormItem item : formItems) {
            initFormItem(item);
        }

        final TagStrategyFactory factory = new org.jvoicexml.interpreter.
                                           tagstrategy.
                                           InitializationTagStrategyFactory();

        final NodeList children = form.getChildNodes();

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

            if (LOGGER.isInfoEnabled()) {
                final int elements = formItems.size();

                LOGGER.info("found " + elements + " form items in form '" + id
                            + "'...");
            }
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

        if (item instanceof FieldFormItem) {
            final FieldFormItem field = (FieldFormItem) item;
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
     * Retrieves the form item with the given name.
     *
     * @param name
     *        Name of the form item
     * @return Corresponding form item, <code>null</code> if it does not
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
     * Retrieves all form items of the current form.
     *
     * @return Collectionof all form items.
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
     * The <em>select</em> phase: The next unfilled form item for visiting.
     * </p>
     *
     * <p>
     * The <em>collect</em> phase: the selected form item is visited, which
     * prompts the user for input, enables the appropriate grammars, and then
     * waits for and collects an <em>input</em> (such as a spoken phrase or
     * DTMF key presses) or an <em>event</em> (such as a request for help or a
     * no input timeout.
     * </p>
     *
     * <p>
     * The <em>process</em> phase: an input is processed by filling form items
     * and executing <code>&lt;var&gt;</code> elements to perform input
     * validation. An event is processed by executing the appropriate event
     * handler for that event type.
     * </p>
     *
     * @exception JVoiceXMLEvent
     *            Error or event processing the form.
     */
    public void mainLoop()
            throws JVoiceXMLEvent {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("starting main loop for form '" + id + "'...");
        }

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

                if (LOGGER.isInfoEnabled()) {
                    final String name = item.getName();
                    LOGGER.info("next form item in form '" + id + "' is '"
                                + name + "'");
                }

                try {
                    // Execute the form item
                    interpreter.setState(InterpreterState.WAITING);
                    final EventHandler handler = collect(item);

                    // Process the input or event.
                    process(item, handler);
                } catch (GotoNextFormItemEvent gnfie) {
                    gotoFormItemName = gnfie.getItem();
                } catch (InternalExitEvent iexte) {
                    // Exit event
                    break;
                }
            }
        } while (item != null);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("no next element in form '" + id
                        + "'. Exiting mainLoop...");
        }
    }

    /**
     * Implementation of the <em>select</em> phase: the next unfilled form
     * item is selected for visiting.
     * <p>
     * The purpose of the select phase is to select the next form item to visit.
     * This is done as follows:
     * </p>
     * <p>
     * If a <code>&lt;goto&gt;</code> from the last main loop iteration's
     * process phase specified a <code>&lt;goto nextitem&gt;</code> then the
     * specified form item is selected.
     * </p>
     * <p>
     * Otherwise the first form item whose guard condition is false is chosen to
     * be visited. If an error occurs while checking the guard conditions, the
     * event is thrown which skips the collect phase, and is handled in the
     * process phase.
     * </p>
     * <p>
     * If no guard condition is false, and the last iteration completed the form
     * without encountering an explicit transfer of control, the FIA does an
     * implicit <code>&lt;exit&gt;</code> operation (similarly, if execution
     * proceeds outside of a form, such as when an error is generated outside of
     * a form, and there is no explicit transfer of control, the interpreter
     * will perform an implicit <code>&lt;exit&gt;</code> operation).
     * </p>
     *
     * @return Next unfilled form item, <code>null</code> if there is none.
     */
    private FormItem select() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("selecting next form item in form '" + id + "'...");
        }

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
     * Select the next form item as a result of a <code>&lt;goto&gt;</code>.
     *
     * @param name
     *        name of the next form item.
     * @return Next <code>FormItem</code>, <code>null</code> if there is no
     *         form item with that name.
     */
    private FormItem select(final String name) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("selecting goto form item '" + name + "' in form '"
                        + id + "'...");
        }

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
     * Implementation of the <em>collect</em> phase: the selected form item is
     * visited, which prompts the user for input, enables the appropriate
     * grammars, and then waits for and collects an <em>input</em> (such as a
     * spoken phrase or DTMF key presses) or an <em>event</em> (such as a
     * request for help or a no input timeout).<br>
     * <p>
     * The purpose of the collect phase is to collect an input or an event. The
     * selected form item is visited, which performs actions that depend on the
     * type of form item.
     * </p>
     *
     * @param item
     *        The form item to visit.
     * @return The event handler to use for the processing phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event visiting the form item.
     * @see #select()
     */
    private EventHandler collect(final FormItem item)
            throws JVoiceXMLEvent {
        if (item == null) {
            LOGGER.warn("no item given: cannot collect.");

            return null;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("collecting '" + item.getName() + "'...");
        }

        if (!reprompt && (item instanceof PromptCountable)) {
            final PromptCountable countable = (PromptCountable) item;
            // Select the appropriate prompts for an input item or <initial>.
            // Queue the selected prompts for play prior to
            // the next collect operation
            queuePrompts(countable);

            // Increment an input item's or <initial>'s prompt counter.
            countable.incrementPromptCount();
        }

        reprompt = false;

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
     *        The current form item.
     * @param handler
     *        The event handler.
     *
     * @exception JVoiceXMLEvent
     *            Error processing the event.
     */
    private void process(final FormItem item, final EventHandler handler)
            throws JVoiceXMLEvent {
        interpreter.setState(InterpreterState.TRANSITIONING);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("processing '" + item.getName() + "'...");
        }

        // Clear all "just_filled" flags.
        justFilled.clear();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cleared all just_filled flags");
        }

        if ((handler != null) && !interpreter.isInFinalProcessingState()) {
            interpreter.setState(InterpreterState.WAITING);
            handler.waitEvent();
        }

        final ImplementationPlatform implementation =
                context.getImplementationPlatform();
        try {
            final UserInput input = implementation.getUserInput();

            input.stopRecognition();
        } catch (NoresourceError nre) {
            LOGGER.warn("cannot stop recognition: no input device available",
                        nre);
        }

        /** @todo Replace this by a proper solution. */
        if (item instanceof FieldFormItem) {
            FieldFormItem field = (FieldFormItem) item;

            if (handler != null) {
                handler.processEvent(field);
            }
        }

        if (reprompt) {
            final Object undefined = Context.getUndefinedValue();
            final ScriptingEngine scripting = context.getScriptingEngine();

            for (InputItem input : justFilled) {
                final String name = input.getName();
                scripting.setVariable(name, undefined);
            }
        }
    }

    /**
     * Sets the <code>just_filled</code> flag fot the given input item.
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
     * @param countable
     *        The prompt countable.
     * @throws JVoiceXMLEvent
     *         Error collecting the prompts or in ptompt evaluation.
     */
    private void queuePrompts(final PromptCountable countable)
            throws JVoiceXMLEvent {
        final PromptChooser promptChooser =
                new PromptChooser(countable, context);

        final Collection<Prompt> prompts = promptChooser.collect();

        for (Prompt prompt : prompts) {
            queuePrompt(prompt);
        }
    }

    /**
     * Queue the prompt to the output device.
     *
     * @param prompt
     *        The prompt to play back
     * @throws NoresourceError
     *         No output device configured.
     * @throws BadFetchError
     *         Error evaluating a script within the prompt.
     * @throws SemanticError
     *         Error evaluating a script within the prompt.
     */
    private void queuePrompt(final Prompt prompt)
            throws NoresourceError,
            BadFetchError, SemanticError {
        final ImplementationPlatform implementation = context
                .getImplementationPlatform();
        final SynthesizedOuput output = implementation.getSystemOutput();

        if (output == null) {
            LOGGER.warn("no audio autput. cannot speak: " + prompt);
            return;
        }

        final ScriptingEngine scripting = context.getScriptingEngine();
        final SsmlParser parser = new SsmlParser(prompt, scripting);
        final SsmlDocument document;

        try {
            document = parser.getDocument();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError("Error converting to SSML!", pce);
        }

        final SpeakableText speakable = new SpeakableSsmlText(document);

        final boolean bargein = prompt.isBargein();
        final DocumentServer documentServer = context.getDocumentServer();

        output.queueSpeakable(speakable, bargein, documentServer);
    }

    /**
     * Activate grammars for the form item.
     *
     * @param item
     *        The form item for which the grammars should be activated.
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
        final GrammarProcessor processor = context.getGrammarProcessor();

        for (Grammar grammar : grammars) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("preprocessing grammar '" + grammar.getSrc()
                        + "...");
            }
            processor.process(context, grammar, registry);
        }

        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();
        final Collection<GrammarImplementation<? extends Object>>
            currentGrammars = registry.getGrammars();
        input.activateGrammars(currentGrammars);
    }

    /**
     * {@inheritDoc}
     *
     * A <code>&lt;block&gt;</code> element is visited by setting its form
     * item variable to <code>true</code>, evaluating its content, and then
     * bypassing the process phase.
     */
    public EventHandler visitBlockFormItem(final BlockFormItem block)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting block '" + block.getName() + "'...");
        }

        block.setVisited();

        executeChildNodes(block);

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * If a <code>&lt;field&gt;</code> is visited, the FIA selects and queues
     * up any prompts based on the item'sprompt counter and prompt conditions.
     * Then it activates and listens for the field level grammar(s) and any
     * higher-level grammars, and waits for the item to be filled or for some
     * events to be generated.
     */
    public EventHandler visitFieldFormItem(final FieldFormItem field)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting field '" + field.getName() + "'...");
        }

        final ImplementationPlatform implementation = context
                .getImplementationPlatform();
        final UserInput input = implementation.getUserInput();

        final EventHandler handler = new org.jvoicexml.interpreter.event.
                                     JVoiceXmlEventHandler();

        handler.collect(context, interpreter, this, field);

        final RecognitionEventStrategy event = new RecognitionEventStrategy(
                context, interpreter, this, field);
        handler.addStrategy(event);

        implementation.setEventHandler(handler);

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
     *
     * @todo Implement this visitObjectFormItem method.
     */
    public EventHandler visitObjectFormItem(final ObjectFormItem object)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visiting object form item '" + object.getName()
                         + "'...");
        }

        /** @todo Implement event handler. */
        final ObjectExecutor executor = new ObjectExecutor();
        executor.execute(context, interpreter, this, object);

        /** @todo Set the return value. */
        executeChildNodes(object);

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this visitRecordFormItem method.
     */
    public EventHandler visitRecordFormItem(final RecordFormItem record)
            throws JVoiceXMLEvent {
        LOGGER.warn("visiting of record form items is not implemented!");

        return null;
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
     * @todo Implement this visitTransferFormItem method.
     */
    public EventHandler visitTransferFormItem(final TransferFormItem transfer)
            throws JVoiceXMLEvent {
        LOGGER.warn("visiting of transfer form items is not implemented!");

        return null;
    }

    /**
     * Execute the tag strategies for all child nodes of the given form item.
     *
     * @param item
     *        The current form item.
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
     *        The current form item.
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
     *        The current form item.
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
            final TagStrategy strategy = tagstrategyFactory
                                         .getTagStrategy(node);

            if (strategy != null) {
                strategy.getAttributes(context, node);
                strategy.evalAttributes(context);
                if (LOGGER.isDebugEnabled()) {
                    strategy.dumpNode(node);
                }
                strategy.validateAttributes();
                strategy.execute(context, interpreter, this, item, node);
            }
        }
    }

    /**
     * Set if the last loop iteration ended with a <code>&lt;catch&gt;</code>
     * that had no <code>&lt;reprompt&gt;</code>.
     *
     * @param on
     *        <code>true</code> if a catch occured that had no reprompt.
     */
    public void setReprompt(final boolean on) {
        reprompt = on;
    }
}
