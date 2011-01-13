/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.jvxml.ExceptionWrapper;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.event.plain.jvxml.InternalExitEvent;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedMap;
import org.jvoicexml.interpreter.variables.ApplicationShadowVarContainer;
import org.jvoicexml.interpreter.variables.DialogShadowVarContainer;
import org.jvoicexml.interpreter.variables.DocumentShadowVarContainer;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Component that uses a <em>VoiceXML interpreter</em> to interpret a
 * <em>VoiceXML document</em> and that may also interact with the
 * <em>implementation platform</em> independent of the <em>VoiceXML
 * interpreter</em>.
 *
 * @author Dirk Schnelle-Walka
 * @author Torben Hardt
 *
 * @version $LastChangedRevision$
 */
public final class VoiceXmlInterpreterContext {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(VoiceXmlInterpreterContext.class);

    /** Reference to the related session. */
    private final JVoiceXmlSession session;

    /** The scope observer for this session. */
    private final ScopeObserver scopeObserver;

    /** The active grammar set. */
    private final ActiveGrammarSet grammars;

    /**
     * A container for the properties, specified by the
     * <code>&lt;property&gt;</code> tag.
     */
    private final ScopedMap<String, String> properties;

    /** The current application to process. */
    private Application application;

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The event handler to use in this context. */
    private final EventHandler eventHandler;

    /**
     * <code>true</code> if the interpreter is in the initialzing phase of a
     * subdialog.
     */
    private boolean initializingSubdialog;

    /** The configuration to use. */
    private Configuration configuration;

    /**
     * Create a new object.
     *
     * @param currentSession
     *        the current session
     * @param config
     *        the configuration to use.
     */
    public VoiceXmlInterpreterContext(final JVoiceXmlSession currentSession,
            final Configuration config) {
        session = currentSession;
        configuration = config;
        if (session != null) {
            scopeObserver = session.getScopeObserver();
        } else {
            LOGGER.warn("no session given: Cannot create scope observer!");
            scopeObserver = null;
        }

        grammars = new ActiveGrammarSet(scopeObserver);
        if (session != null) {
            final ImplementationPlatform platform =
                session.getImplementationPlatform();
            final GrammarDeactivator deactivator =
                new GrammarDeactivator(platform);
            grammars.addObserver(deactivator);
        }
        properties = new ScopedMap<String, String>(scopeObserver);
        eventHandler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(scopeObserver);
    }

    /**
     * Create a new object.
     *
     * @param currentSession
     *        The current session
     * @param config
     *        the configuration to use.
     * @param observer
     *        the scope observer (not taken from the session).
     */
    public VoiceXmlInterpreterContext(final JVoiceXmlSession currentSession,
            final Configuration config, final ScopeObserver observer) {
        session = currentSession;
        scopeObserver = observer;

        grammars = new ActiveGrammarSet(scopeObserver);
        if (session != null) {
            final ImplementationPlatform platform =
                session.getImplementationPlatform();
            final GrammarDeactivator deactivator =
                new GrammarDeactivator(platform);
            grammars.addObserver(deactivator);
        }
        properties = new ScopedMap<String, String>(scopeObserver);
        eventHandler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(scopeObserver);
    }

    /**
     * Retrieves the current session.
     * @return the current session.
     * @since 0.7
     */
    public Session getSession() {
        return session;
    }

    /**
     * Retrieves the scope observer for this session.
     * @return The scope observer.
     */
    public ScopeObserver getScopeObserver() {
        return scopeObserver;
    }

    /**
     * Retrieves a reference to the used implementation platform.
     *
     * @return The used implementation platform.
     */
    public ImplementationPlatform getImplementationPlatform() {
        return session.getImplementationPlatform();
    }

    /**
     * Retrieves a reference to the grammar processor.
     *
     * @return The grammar processor.
     *
     * @since 0.3
     */
    public GrammarProcessor getGrammarProcessor() {
        return session.getGrammarProcessor();
    }

    /**
     * Retrieves the configuration.
     * @return the configuration
     * @since 0.7.4
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Lazy instantiation of the scripting engine.
     * @return the scripting engine.
     *
     * @since 0.3.1
     */
    public ScriptingEngine getScriptingEngine() {
        // TODO make sure that all accesses to the scripting engine
        // are related to one thread per session.
        if (scripting == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating scripting engine...");
            }

            scripting = new ScriptingEngine(scopeObserver);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...scripting engine created");
            }
        }

        return scripting;
    }

    /**
     * Retrieves the event handler to use in this context.
     * @return the event handler.
     * @since 0.7
     */
    public EventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * Enter a new scope for resolving variables. this is useful if we enter a
     * new block, but at least every file should have it's own scope.
     * @param scope the new scope.
     */
    public void enterScope(final Scope scope) {
        if (scopeObserver == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no scope observer set. Cannot propagate entering "
                        + "of scope '" + scope + "'");
            }
            return;
        }
        scopeObserver.enterScope(scope);
    }

    /**
     * Return from a previously created scope. i.e. pop current scope from
     * stack.
     * @param scope The scope which was left.
     */
    public void exitScope(final Scope scope) {
        if (scopeObserver == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no scope observer set. Cannot propagate exiting "
                        + "of scope '" + scope + "'");
            }
            return;
        }

        scopeObserver.exitScope(scope);
    }

    /**
     * Closes all open resources.
     */
    public void close() {
    }

    /**
     * Retrieves the active grammar set.
     * @return the active grammar set.
     */
    public ActiveGrammarSet getActiveGrammarSet() {
        return grammars;
    }

    /**
     * Sets the property with the given name to the given value.
     * @param name Name of the property.
     * @param value Value of the property.
     */
    public void setProperty(final String name, final String value) {
        properties.put(name, value);
    }

    /**
     * Retrieves the value of the given property.
     * @param name Name of the property.
     * @return Value of the property.
     */
    public String getProperty(final String name) {
        return properties.get(name);
    }

    /**
     * Retrieves the value of the given property.
     * @param name name of the property.
     * @param defValue the default value, if the property is not defined.
     * @return value of the property or default value if no property is defined
     * @since 0.7.4
     */
    public String getProperty(final String name, final String defValue) {
        final String value = properties.get(name);
        if (value == null) {
            return defValue;
        }
        return value;
    }

    /**
     * Retrieves the application.
     * @return the application.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Starts processing the given application.
     *
     * @param appl
     *        The application to process.
     * @exception ErrorEvent
     *            Error processing the document.
     */
    public void process(final Application appl)
            throws ErrorEvent {
        application = appl;
        VoiceXmlDocument document = application.getCurrentDocument();

        enterScope(Scope.APPLICATION);
        final ScriptingEngine scriptingEngine = getScriptingEngine();
        scriptingEngine.createHostObject(
                ApplicationShadowVarContainer.VARIABLE_NAME,
                ApplicationShadowVarContainer.class);

        DocumentDescriptor descriptor = null;
        while (document != null) {
            final URI rootUri = application.getApplication();
            if (rootUri != null) {
                if (!application.isLoaded(rootUri)) {
                    loadRootDocument(rootUri);
                }
            }
            try {
                enterScope(Scope.DOCUMENT);
                scriptingEngine.createHostObject(
                        DocumentShadowVarContainer.VARIABLE_NAME,
                        DocumentShadowVarContainer.class);
                final String dialog;
                if (descriptor != null) {
                    final URI uri = descriptor.getUri();
                    dialog = uri.getFragment();
                } else {
                    dialog = null;
                }
                descriptor = interpret(document, dialog);
                if (descriptor == null) {
                    document = null;
                } else {
                    document = application.getCurrentDocument();
                    final URI uri = descriptor.getUri();
                    if ((document != null) && (descriptor.isForceLoad()
                            || !application.isLoaded(uri))) {
                        document = loadDocument(descriptor);
                    }
                }
            } catch (InternalExitEvent e) {
                LOGGER.info("exit request. terminating processing");
                document = null;
            } catch (ErrorEvent e) {
                throw e;
            } catch (ConnectionDisconnectHangupEvent e) {
                LOGGER.info("user hung up. terminating processing");
                document = null;
            } catch (JVoiceXMLEvent e) {
                throw new BadFetchError("unhandled event '" + e.getEventType()
                        + "'", e);
            } finally {
                exitScope(Scope.DOCUMENT);
            }
        }
        LOGGER.info("no more documents to process for '" + application + "'");
        exitScope(Scope.APPLICATION);
    }


    /**
     * Starts processing the given application.
     *
     * @param appl
     *        the application to process.
     * @param desc
     *        the documnet descriptor for the subdialog
     * @exception JVoiceXMLEvent
     *            Error processing the document.
     */
    public void processSubdialog(final Application appl,
            final DocumentDescriptor desc)
            throws JVoiceXMLEvent {
        initializingSubdialog = true;
        application = appl;
        VoiceXmlDocument document = application.getCurrentDocument();

        final ScriptingEngine scriptingEngine = getScriptingEngine();
        DocumentDescriptor descriptor = desc;
        while (document != null) {
            try {
                enterScope(Scope.DOCUMENT);
                scriptingEngine.createHostObject(
                        DocumentShadowVarContainer.VARIABLE_NAME,
                        DocumentShadowVarContainer.class);
                final String dialog;
                if (descriptor != null) {
                    final URI uri = descriptor.getUri();
                    dialog = uri.getFragment();
                } else {
                    dialog = null;
                }
                descriptor = interpret(document, dialog);
                if (descriptor == null) {
                    document = null;
                } else {
                    document = application.getCurrentDocument();
                    final URI uri = descriptor.getUri();
                    if ((document != null) && !application.isLoaded(uri)) {
                        document = loadDocument(descriptor);
                    }
                }
            } catch (InternalExitEvent e) {
                LOGGER.info(
                        "exit request. terminating processing of subdialog");
                document = null;
            } finally {
                exitScope(Scope.DOCUMENT);
            }
        }
    }

    /**
     * Checks if the interpreter is in the initialzing phase of a
     * subdialog.
     * @return <code>true</code> if the interpreter is in the initialzing phase
     *          of a subdialog.
     * @since 0.7.4
     */
    public boolean isInitializingSubdialog() {
        return initializingSubdialog;
    }

    /**
     * The FIA finalized the initialization phase.
     * @since 0.7.4
     */
    void finalizedInitialization() {
        initializingSubdialog = false;
    }

    /**
     * Loads the root document with the given <code>URI</code>.
     * @param uri the URI of the root document.
     * @exception BadFetchError
     *            Error loading the root document.
     * @exception SemanticError
     *            The document contains semantic errors.
     * @since 0.6
     */
    private void loadRootDocument(final URI uri)
        throws BadFetchError, SemanticError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading root document...");
        }
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final VoiceXmlDocument document = acquireVoiceXmlDocument(descriptor);
        // If a document's application attribute refers to a document that also
        // has an application attribute specified, an error.semantic event is
        // thrown.
        final Vxml vxml = document.getVxml();
        final URI applicationUri;
        try {
            applicationUri = vxml.getApplicationUri();
        } catch (URISyntaxException e) {
            throw new SemanticError("Application root document '" + uri
                    + "' does not contain a valid URI");
        }
        if (applicationUri != null) {
            throw new SemanticError("Application root document '" + uri
                    + "' must not have an application attribute");
        }
        application.setRootDocument(document);
        try {
            initDocument(document, null);
        } catch (BadFetchError e) {
            throw e;
        } catch (SemanticError e) {
            throw e;
        } catch (JVoiceXMLEvent e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done loading root document");
        }
    }

    /**
     * Loads the document of the descriptor with the associated attributes and
     * adds it to the list of loaded documents.
     * <p>
     * If an application is active, the descriptor is also modified to contain
     * the resolved URI.
     * </p>
     * @param descriptor
     *        descriptor of the next document to process.
     * @return VoiceXML document with the given URI
     * @exception BadFetchError
     *            Error retrieving the document.
     * @since 0.7
     */
    public VoiceXmlDocument loadDocument(final DocumentDescriptor descriptor)
        throws BadFetchError {
        final VoiceXmlDocument doc = acquireVoiceXmlDocument(descriptor);
        final URI uri = descriptor.getUri();
        if (application != null) {
            final URI resolvedUri = application.resolve(uri);
            application.addDocument(resolvedUri, doc);
        }
        return doc;
    }

    /**
     * Retrieves a reference to the document server.
     * @return Reference to the document server.
     */
    public DocumentServer getDocumentServer() {
        return session.getDocumentServer();
    }

    /**
     * Acquires the VoiceXML document with the given URI.
     *
     * <p>
     * If a relative URI is given, the scheme and authority of the last
     * document are used to create a hierarchical URI for the next document.
     * </p>
     *
     * @param descriptor
     *        descriptor of the next document to process.
     * @return VoiceXML document with the given URI
     * @exception BadFetchError
     *            Error retrieving the document.
     */
    public VoiceXmlDocument acquireVoiceXmlDocument(
            final DocumentDescriptor descriptor)
            throws BadFetchError {
        final URI uri = descriptor.getUri();
        final URI nextUri;
        if (application == null) {
            nextUri = uri;
        } else {
            nextUri = application.resolve(uri);
        }
        descriptor.setURI(nextUri);
        final DocumentServer server = session.getDocumentServer();

        return server.getDocument(session, descriptor);
    }

    /**
     * Acquire the VoiceXML document with the given URI.
     *
     * <p>
     * If a relative URI is given, the scheme and authority of the last
     * document are used to create a hierarchical URI for the next document.
     * </p>
     *
     * @param uri
     *        URI of the next document to process.
     * @param attributes
     *        attributes governing the fetch.
     *
     * @return Grammar document with the given URI.
     * @exception BadFetchError
     *            Error retrieving the document.
     *
     * @since 0.3
     */
    public GrammarDocument acquireExternalGrammar(final URI uri,
            final FetchAttributes attributes)
            throws BadFetchError {
        final DocumentServer server = session.getDocumentServer();
        final URI grammarUri;
        if (application == null) {
            grammarUri = uri;
        } else {
            grammarUri = application.resolve(uri);
        }

        return server.getGrammarDocument(session, grammarUri, attributes);
    }

    /**
     * Interprets the given VoiceXML document.
     *
     * @param document
     *        VoiceXML document to interpret.
     * @param startDialog
     *        the dialog where to start interpretation
     * @return Descriptor of the next document to process or <code>null</code>
     *          if there is no next document.
     * @exception JVoiceXMLEvent
     *            Error or event processing the document.
     */
    private DocumentDescriptor interpret(final VoiceXmlDocument document,
            final String startDialog)
            throws JVoiceXMLEvent {
        final VoiceXmlInterpreter interpreter = new VoiceXmlInterpreter(this);
        try {
            interpreter.init(configuration);
        } catch (ConfigurationException e) {
            throw new ExceptionWrapper(e.getMessage(), e);
        }

        interpreter.setDocument(document, startDialog);
        if (startDialog != null) {
            final Dialog dialog = interpreter.getNextDialog();
            if (dialog == null) {
                throw new BadFetchError("Target of goto '" + startDialog
                        + "' not found in current document");
            }
        }
        initDocument(document, interpreter);

        Dialog dialog = interpreter.getNextDialog();
        while (dialog != null) {
            try {
                enterScope(Scope.DIALOG);
                scripting.createHostObject(
                        DialogShadowVarContainer.VARIABLE_NAME,
                        DialogShadowVarContainer.class);
                interpreter.process(dialog);
                dialog = interpreter.getNextDialog();
            } catch (GotoNextFormEvent e) {
                final String id = e.getForm();
                dialog = interpreter.getDialog(id);
                if (dialog == null) {
                    throw new BadFetchError("Target of goto '" + id
                            + "'not found in current document");
                }
            } catch (GotoNextDocumentEvent e) {
                final URI uri = e.getUri();
                return new DocumentDescriptor(uri);
            } catch (SubmitEvent e) {
                return e.getDocumentDescriptor();
            } finally {
                exitScope(Scope.DIALOG);
            }
        }

        return null;
    }

    /**
     * Initializes the given document.
     * @param document the document to initialize.
     * @param interpreter the current interpreter, if any.
     * @exception JVoiceXMLEvent
     *            error initializing document
     * @since 0.6
     */
    private void initDocument(final VoiceXmlDocument document,
            final VoiceXmlInterpreter interpreter) throws JVoiceXMLEvent {
        LOGGER.info("initializing document...");

        eventHandler.collect(this, interpreter, document);

        final Vxml vxml = document.getVxml();
        final NodeList list = vxml.getChildNodes();
        final InitializationTagStrategyFactory factory;
        try {
            factory = configuration.loadObject(
                    InitializationTagStrategyFactory.class);
        } catch (ConfigurationException e) {
            throw new ExceptionWrapper(e.getMessage(), e);
        }
        for (int i = 0; i < list.getLength(); i++) {
            final Node currentNode = list.item(i);
            if (currentNode instanceof VoiceXmlNode) {
                final VoiceXmlNode node = (VoiceXmlNode) currentNode;
                final TagStrategy strategy = factory.getTagStrategy(node);
                if (strategy != null) {
                    strategy.getAttributes(this, node);
                    strategy.evalAttributes(this);
                    if (LOGGER.isDebugEnabled()) {
                        strategy.dumpNode(node);
                    }
                    strategy.validateAttributes();
                    strategy.execute(this, interpreter, null, null, node);
                }
            }
        }
        LOGGER.info("...done initializing document");
    }
}
