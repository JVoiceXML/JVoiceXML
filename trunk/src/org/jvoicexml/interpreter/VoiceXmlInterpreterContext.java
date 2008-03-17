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

import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
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
 * @author Dirk Schnelle
 * @author Torben Hardt
 *
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
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
    private ScopedMap<String, String> properties;

    /** The current application to process. */
    private Application application;

    /** The scripting engine. */
    private ScriptingEngine scripting;

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
        grammars.setScopeObserver(scopeObserver);
        properties = new ScopedMap<String, String>(scopeObserver);

        enterScope(Scope.SESSION);
    }


    /**
     * Retrieve the scope observer for this session.
     * @return The scope observer.
     */
    public ScopeObserver getScopeObserver() {
        return scopeObserver;
    }

    /**
     * Retrieve a reference to the used implementation platform.
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
     * Enter a new scope for resolving variables. this is useful if we enter a
     * new block, but at least every file should have it's own scope.
     * @param scope The new scope.
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
     * Retrieves the fetch attributes for the current document.
     * @return fetch attributes;
     * @since 0.6
     */
    public FetchAttributes getFetchAttributes() {
        return application.getFetchAttributes();
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
                final URI uri = interpret(document);
                if (uri == null) {
                    document = null;
                } else {
                    final FetchAttributes attributes =
                        application.getFetchAttributes();
                    document = acquireVoiceXmlDocument(uri, attributes);
                    if (document != null) {
                        application.addDocument(uri, document);
                    }
                }
            } catch (ErrorEvent e) {
                throw e;
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
     * @throws BadFetchError
     *         Error loading the root document.
     * @since 0.6
     */
    private void loadRootDocument(final URI uri) throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading root document...");
        }
        final FetchAttributes attributes =
            application.getFetchAttributes();
        final VoiceXmlDocument document =
            acquireVoiceXmlDocument(uri, attributes);
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
     * @param uri
     *        URI of the next document to process.
     * @param attributes
     *        attributes governing the fetch.
     * @return VoiceXML document with the given URI or <code>null</code> if
     *         the document cannot be obtained.
     * @exception BadFetchError
     *            Error retrieving the document.
     */
    public VoiceXmlDocument acquireVoiceXmlDocument(final URI uri,
            final FetchAttributes attributes)
            throws BadFetchError {
        final URI nextUri;
        if (application == null) {
            nextUri = uri;
        } else {
            nextUri = application.resolve(uri);
        }

        final DocumentServer server = session.getDocumentServer();

        final VoiceXmlDocument document = server.getDocument(nextUri,
                attributes);

        return document;
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

        return server.getGrammarDocument(grammarUri, attributes);
    }

    /**
     * Acquire the audio file with the given URI.
     *
     * <p>
     * If a relative URI is given, the scheme and authority of the last
     * document are used to create a hierarchical URI for the next document.
     * </p>
     *
     * @param uri
     *        URI of the audio file.
     * @return Stream to read the audio file.
     * @exception BadFetchError
     *            Error retrieving the audio file.
     *
     * @since 0.3
     */
    public AudioInputStream acquireAudio(final URI uri)
            throws BadFetchError {
        final URI audioUri = application.resolve(uri);

        final DocumentServer server = session.getDocumentServer();

        return server.getAudioInputStream(audioUri);
    }

    /**
     * Interpret the given VoiceXML document.
     *
     * @param document
     *        VoiceXML document to interpret.
     * @return Next document to process or <code>null</code> if there is no
     *         next document.
     * @exception JVoiceXMLEvent
     *            Error or event processing the document.
     */
    private URI interpret(final VoiceXmlDocument document)
            throws JVoiceXMLEvent {
        final VoiceXmlInterpreter interpreter = new VoiceXmlInterpreter(this);

        interpreter.setDocument(document);

        initDocument(document, interpreter);

        ExecutableForm next = interpreter.getNextForm();

        while (next != null) {
            try {
                enterScope(Scope.DIALOG);
                interpreter.processForm(next);
                next = interpreter.getNextForm();
            } catch (GotoNextFormEvent gnfe) {
                final String id = gnfe.getForm();
                next = interpreter.getForm(id);
            } catch (GotoNextDocumentEvent gnde) {
                return gnde.getUri();
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
