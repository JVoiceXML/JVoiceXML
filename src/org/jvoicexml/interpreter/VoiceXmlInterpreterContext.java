/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.Application;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlSession;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.interpreter.grammar.ExternalGrammar;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedMap;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
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
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class VoiceXmlInterpreterContext {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VoiceXmlInterpreterContext.class);

    /** Reference to the related session. */
    private final JVoiceXmlSession session;

    /** The scope observer for this session. */
    private final ScopeObserver scopeObserver;

    /** The grammar registry. */
    private final GrammarRegistry grammars;

    /**
     * The uri of the last acquired document.
     * @see #acquireVoiceXmlDocument(java.net.URI)
     */
    private URI lastUri;

    /**
     * A container for the properties, specified by the
     * <code>&lt;property&gt;</code> tag.
     */
    private ScopedMap<String, String> properties;

    /**
     * Create a new object.
     *
     * @param currentSession
     *        The current session
     */
    public VoiceXmlInterpreterContext(final JVoiceXmlSession currentSession) {
        session = currentSession;
        scopeObserver = session.getScopeObserver();
        grammars = new org.jvoicexml.interpreter.grammar.
                   JVoiceXmlGrammarRegistry(this);
        properties = new ScopedMap<String, String>(scopeObserver);

        scopeObserver.enterScope(Scope.SESSION);
    }


    /**
     * Retreive the scope observer for this session.
     * @return The scope observer.
     */
    public ScopeObserver getScopeObserver() {
        return scopeObserver;
    }

    /**
     * Retrieve a reference to the used imlementation platform.
     *
     * @return The used imlementation platform.
     */
    public ImplementationPlatform getImplementationPlatform() {
        return session.getImplementationPlatform();
    }

    /**
     * Retreives a reference to the grammar processor.
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
        return session.getScriptingEngine();
    }

    /**
     * Enter a new scope for var resolving. this is usefull if we enter a new
     * block, but at least every file should have it's own scope.
     * @param scope The new scope.
     */
    public void enterScope(final Scope scope) {
        scopeObserver.enterScope(scope);
    }

    /**
     * Return from a previously created scope. i.e. pop current scope from
     * stack.
     * @param scope The scope which was left.
     */
    public void exitScope(final Scope scope) {
        scopeObserver.exitScope(scope);
    }

    /**
     * Closes all open resources.
     */
    public void close() {
        scopeObserver.exitScope(Scope.SESSION);
    }

    /**
     * Retrievse the grammar registry.
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
     * Starts processing the given application.
     *
     * @param application
     *        URI of the application's root doucment to process.
     * @exception ErrorEvent
     *            Error processing the document.
     */
    public void process(final Application application)
            throws ErrorEvent {
        final URI uri = application.getCurrentUri();
        VoiceXmlDocument document = acquireVoiceXmlDocument(uri);

        while (document != null) {
            try {
                document = interpret(document);
            } catch (JVoiceXMLEvent ev) {
                if (ev instanceof ErrorEvent) {
                    throw (ErrorEvent) ev;
                } else {
                    throw new BadFetchError("unhandled event", ev);
                }
            }
        }
    }

    /**
     * Retrieves a hierarchical URI. If a relative URI is given, the scheme and
     * authority of the last document are used to create a hierarchical URI for
     * the next document.
     *
     * @param uri URI.
     * @return Hierarchical URI.
     *
     * @since 0.3
     * @todo Take respect to different schemes.
     *
     * @exception BadFetchError
     *            Error creating a hiearchical URI.
     */
    private URI getHierarchicalURI(final URI uri)
            throws BadFetchError {
        final URI nextUri;

        final String nextPath = uri.getPath();
        if (nextPath == null) {
            return uri;
        }

        if (nextPath.startsWith("/")) {
            nextUri = uri;
        } else {
            String last = lastUri.toString();
            final int index = last.lastIndexOf('/');
            if (index > 0) {
                last = last.substring(0, index);
            }

            try {
                final URI generatedUri = new URI(last + "/" + uri);
                nextUri = generatedUri.normalize();
            } catch (java.net.URISyntaxException use) {
                throw new BadFetchError(use);
            }
        }

        return nextUri;
    }

    /**
     * Retrieves a reference to the document server.
     * @return Reference to the document server.
     */
    public DocumentServer getDocumentServer() {
        return session.getDocumentServer();
    }

    /**
     * Acquires the VoiceXML document with the given uri.
     *
     * <p>
     * If a relative URI is given, the scheme and authority of the last
     * document are used to create a hierarchical URI for the next document.
     * </p>
     *
     * @param uri
     *        URI of the next document ro process.
     * @return VoiceXML document with the given URI or <code>null</code> if
     *         the document cannot be obtained.
     * @exception BadFetchError
     *            Error retrieving the document.
     */
    public VoiceXmlDocument acquireVoiceXmlDocument(final URI uri)
            throws BadFetchError {
        final URI nextUri;

        if (lastUri == null) {
            nextUri = uri;
        } else {
            nextUri = getHierarchicalURI(uri);
        }

        lastUri = nextUri;

        final DocumentServer server = session.getDocumentServer();

        final VoiceXmlDocument document = server.getDocument(lastUri);

        final String base = document.getBaseURI();
        if (base != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("new base uri '" + base + "'");
            }

            try {
                lastUri = new URI(base);
            } catch (java.net.URISyntaxException use) {
                throw new BadFetchError(use);
            }
        }

        return document;
    }

    /**
     * Acquire the VoiceXML document with the given uri.
     *
     * <p>
     * If a relative URI is given, the scheme and authority of the last
     * document are used to create a hierarchical URI for the next document.
     * </p>
     *
     * @param uri
     *        URI of the next document ro process.
     * @return VoiceXML document with the given URI or <code>null</code> if
     *         the document cannot be obtained.
     * @exception BadFetchError
     *            Error retrieving the document.
     *
     * @since 0.3
     */
    public ExternalGrammar acquireExternalGrammar(final URI uri)
            throws BadFetchError {
        final URI nextUri = getHierarchicalURI(uri);

        final DocumentServer server = session.getDocumentServer();

        return server.getGrammar(nextUri);
    }

    /**
     * Acquire the audio file with the given uri.
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
        getHierarchicalURI(uri);

        final DocumentServer server = session.getDocumentServer();

        return server.getAudioInputStream(uri);
    }

    /**
     * Interpret the given VoiceXML document.
     *
     * @param document
     *        VoiceXML document to interpret.
     * @return Next document to process or <code>null</code> if there is no
     *         next doxument.
     * @exception JVoiceXMLEvent
     *            Error or event processing the document.
     */
    private VoiceXmlDocument interpret(final VoiceXmlDocument document)
            throws JVoiceXMLEvent {
        final VoiceXmlInterpreter interpreter = new VoiceXmlInterpreter(this);

        interpreter.setDocument(document);

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

        ExecutableForm next = interpreter.getNextForm();

        while (next != null) {
            try {
                interpreter.processForm(next);
                next = interpreter.getNextForm();
            } catch (GotoNextFormEvent gnfe) {
                final String id = gnfe.getForm();
                next = interpreter.getForm(id);
            } catch (GotoNextDocumentEvent gnde) {
                final URI uri = gnde.getUri();
                return acquireVoiceXmlDocument(uri);
            }
        }

        return null;
    }
}
