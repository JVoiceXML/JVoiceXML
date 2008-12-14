/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
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

import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedMap;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Component that uses a <em>VoiceXML interpreter</em> to interpret a
 * <em>VoceXML document</em> and that may also interact with the
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

    /** The grammar registry. */
    private final GrammarRegistry grammars;

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
     * Create a new object.
     *
     * @param currentSession
     *        The current session
     */
    public VoiceXmlInterpreterContext(final JVoiceXmlSession currentSession) {
        session = currentSession;

        if (session != null) {
            scopeObserver = session.getScopeObserver();
        } else {
            LOGGER.warn("no session given: Cannot create scope observer!");
            scopeObserver = null;
        }

        final JVoiceXmlConfiguration configuration =
            JVoiceXmlConfiguration.getInstance();
        grammars = configuration.loadObject(GrammarRegistry.class,
                GrammarRegistry.CONFIG_KEY);
        if (grammars != null) {
            grammars.setScopeObserver(scopeObserver);
        }
        properties = new ScopedMap<String, String>(scopeObserver);
        eventHandler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(scopeObserver);
        enterScope(Scope.SESSION);
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
     * Retrieves the scripting engine.
     * @return The scripting engine.
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
        scopeObserver.exitScope(Scope.SESSION);
    }

    /**
     * Retrieves the grammar registry.
     * @return The used <code>GrammarRegistry</code>.
     */
    public GrammarRegistry getGrammarRegistry() {
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created application variable");
        }

        while (document != null) {
            final URI rootUri = application.getApplication();
            if (rootUri != null) {
                if (!application.isLoaded(rootUri)) {
                    loadRootDocument(rootUri);
                }
            }
            try {
                enterScope(Scope.DOCUMENT);
                final DocumentDescriptor descriptor = interpret(document);
                if (descriptor == null) {
                    document = null;
                } else {
                    document = application.getCurrentDocument();
                    final URI uri = descriptor.getUri();
                    if (document != null && !application.isLoaded(uri)) {
                        // TODO merge the fetch attributes
                        final FetchAttributes attributes =
                            application.getFetchAttributes();
                        descriptor.setAttributes(attributes);
                        document = acquireVoiceXmlDocument(descriptor);
                        if (document != null) {
                            application.addDocument(uri, document);
                        }
                    }
                }
            } catch (ErrorEvent e) {
                throw e;
            } catch (ConnectionDisconnectHangupEvent e) {
                LOGGER.info("user hung up. terminating processing");
                document = null;
            } catch (JVoiceXMLEvent e) {
                throw new BadFetchError("unhandled event", e);
            } finally {
                exitScope(Scope.DOCUMENT);
            }
        }
        exitScope(Scope.APPLICATION);
    }


    /**
     * Loads the root document with the given <code>uri</code>.
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
        final FetchAttributes attributes =
            application.getFetchAttributes();
        descriptor.setAttributes(attributes);
        final VoiceXmlDocument document = acquireVoiceXmlDocument(descriptor);
        application.setRootDocument(document);
        initDocument(document, null);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...done loading root document");
        }
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
     * @return VoiceXML document with the given URI or <code>null</code> if
     *         the document cannot be obtained.
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
     * @return Grammar document with the given URI or <code>null</code> if
     *         the document cannot be obtained.
     * @exception BadFetchError
     *            Error retrieving the document.
     *
     * @since 0.3
     */
    public GrammarDocument acquireExternalGrammar(final URI uri,
            final FetchAttributes attributes)
            throws BadFetchError {
        final DocumentServer server = session.getDocumentServer();

        final URI grammarUri = application.resolve(uri);

        return server.getGrammarDocument(session, grammarUri, attributes);
    }

    /**
     * Interprets the given VoiceXML document.
     *
     * @param document
     *        VoiceXML document to interpret.
     * @return Descriptor of the next document to process or <code>null</code>
     *          if there is no next document.
     * @exception JVoiceXMLEvent
     *            Error or event processing the document.
     */
    private DocumentDescriptor interpret(final VoiceXmlDocument document)
            throws JVoiceXMLEvent {
        final VoiceXmlInterpreter interpreter = new VoiceXmlInterpreter(this);

        interpreter.setDocument(document);

        initDocument(document, interpreter);

        Dialog dialog = interpreter.getNextDialog();

        while (dialog != null) {
            try {
                enterScope(Scope.DIALOG);
                interpreter.process(dialog);
                dialog = interpreter.getNextDialog();
            } catch (GotoNextFormEvent e) {
                final String id = e.getForm();
                dialog = interpreter.getDialog(id);
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
     * @since 0.6
     */
    private void initDocument(final VoiceXmlDocument document,
            final VoiceXmlInterpreter interpreter) {
        LOGGER.info("initializing document...");
        final Vxml vxml = document.getVxml();

        final NodeList list = vxml.getChildNodes();

        final TagStrategyFactory factory =
                new org.jvoicexml.interpreter.tagstrategy.
                InitializationTagStrategyFactory();

        for (int i = 0; i < list.getLength(); i++) {
            final Node currentNode = list.item(i);
            if (currentNode instanceof VoiceXmlNode) {
                final VoiceXmlNode node = (VoiceXmlNode) currentNode;
                final TagStrategy strategy = factory.getTagStrategy(node);

                if (strategy != null) {
                    try {
                        strategy.getAttributes(this, node);
                        strategy.evalAttributes(this);
                        if (LOGGER.isDebugEnabled()) {
                            strategy.dumpNode(node);
                        }
                        strategy.validateAttributes();
                        strategy.execute(this, interpreter, null, null, node);
                    } catch (JVoiceXMLEvent event) {
                        LOGGER.error("error initializing", event);
                    }
                }
            }
        }
        LOGGER.info("...done initializing document");
    }
}
