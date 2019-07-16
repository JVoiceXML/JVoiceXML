/*
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.jetty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.schemestrategy.builtin.GrammarCreator;

/**
 * A storage for documents for ASR and TTS that are generated while executing a
 * session. The main task of this component is to manage a set of URIs
 * associated with these documents.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class DocumentStorage {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(DocumentStorage.class);

    /** Generated documents per session. */
    private final Map<SessionIdentifier, Collection<GrammarDocument>> sessionDocuments;

    /** Stored documents. */
    private final Map<URI, GrammarDocument> documents;

    /** The integrated web server. */
    private Server server;

    /** Port of the document storage. */
    private int storagePort;

    /** Handler for internale gramamrs. */
    private final Handler internalGrammarHandler;

    /** Handler for builtin grammars. */
    private final BuiltinGrammarHandler builtinGrammarHandler;

    /**
     * Creates a new object.
     * 
     */
    public DocumentStorage() {
        sessionDocuments =
                new java.util.HashMap<SessionIdentifier, Collection<GrammarDocument>>();
        documents = new java.util.HashMap<URI, GrammarDocument>();
        storagePort = 9595;
        internalGrammarHandler = new InternalGrammarDocumentHandler(this);
        builtinGrammarHandler = new BuiltinGrammarHandler();
    }

    /**
     * Sets the storage port.
     * 
     * @param port
     *            port number for the integrated web server
     * @since 0.7.8
     */
    public void setStoragePort(final int port) {
        storagePort = port;
    }

    /**
     * Adds the specified grammar creators to the list of known grammar
     * creators.
     * 
     * @param col
     *            the creators to add
     * @since 0.7.8
     */
    public void setGrammarCreators(final Collection<GrammarCreator> col) {
        builtinGrammarHandler.setGrammarCreators(col);
    }

    /**
     * Starts the document storage. Afterwards it will be ready to serve
     * documents.
     * 
     * @throws Exception
     *             error starting the web server
     * 
     * @since 0.7.8
     */
    public void start() throws Exception {
        if (storagePort < 0) {
            return;
        }
        server = new Server(storagePort);
        ContextHandler rootContext = new ContextHandler();
        rootContext.setHandler(internalGrammarHandler);
        ContextHandler internalGrammarContext = new ContextHandler(
                InternalGrammarDocumentHandler.CONTEXT_PATH);
        internalGrammarContext.setHandler(internalGrammarHandler);
        ContextHandler builtinGrammarContext = new ContextHandler(
                BuiltinGrammarHandler.CONTEXT_PATH);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        builtinGrammarContext.setHandler(builtinGrammarHandler);
        ContextHandler[] handlers = new ContextHandler[] { rootContext,
                internalGrammarContext, builtinGrammarContext };
        contexts.setHandlers(handlers);
        server.setHandler(contexts);
        server.start();
        LOGGER.info("document storage started on port " + storagePort);
    }

    /**
     * Resolves the given URI of a builtin grammar to an URI that can be handled
     * by this document server.
     * 
     * @param uri
     *            the builtin URI
     * @return the resolved URI
     * @since 0.7.8
     */
    public URI resolveBuiltinUri(final URI uri) {
        final URI serverUri = server.getURI();
        try {
            return new URI(serverUri
                    + BuiltinGrammarHandler.CONTEXT_PATH.substring(1) + "/"
                    + uri.getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            LOGGER.warn("unable to resolve '" + uri + "'");
            return uri;
        }
    }

    /**
     * Adds the given grammar document to the documents store and retrieves the
     * URI to access it from external.
     * 
     * @param sessionId
     *            the id of the initiating session
     * @param document
     *            the document to add
     * @return the URI to access the document
     * @throws URISyntaxException
     *             if the URI could not be created
     */
    public URI addGrammarDocument(final SessionIdentifier sessionId,
            final GrammarDocument document) throws URISyntaxException {
        Collection<GrammarDocument> currentDocuments =
                getCurrentSessionDocuments(sessionId);
        currentDocuments.add(document);
        final URI localUri = new URI(
                InternalGrammarDocumentHandler.CONTEXT_PATH + "/" + sessionId
                        + "/" + document.hashCode());
        documents.put(localUri, document);
        final URI serverUri = server.getURI();
        final URI uri = new URI(serverUri
                + InternalGrammarDocumentHandler.CONTEXT_PATH.substring(1)
                + "/" + sessionId + "/" + document.hashCode());
        document.setURI(uri);
        LOGGER.info("added grammar document at '" + uri + "'");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added document " + document);
        }
        return null;
    }

    /**
     * Retrieves the documents for the given session. If no documents exist so
     * far a new document storage for the session is created.
     * 
     * @param sessionId
     *            the identifier for the session
     * @return documents for the session
     * @since 0.7.8
     */
    private Collection<GrammarDocument> getCurrentSessionDocuments(
            final SessionIdentifier sessionId) {
        Collection<GrammarDocument> currentDocuments = sessionDocuments
                .get(sessionId);
        if (currentDocuments != null) {
            return currentDocuments;
        }
        currentDocuments = new java.util.ArrayList<GrammarDocument>();
        sessionDocuments.put(sessionId, currentDocuments);
        LOGGER.info("initialized document storage for session '" + sessionId
                + "'");
        return currentDocuments;
    }

    /**
     * Retrieves the content of the document.
     * 
     * @param uri
     *            URI of the document to retrieve, {@code null} if there is no
     *            such document
     * @return the document
     * @throws URISyntaxException if the URI does not feature a valid path
     */
    public GrammarDocument getDocument(final URI uri) throws URISyntaxException {
        final String path = uri.getPath();
        final URI pathUri = new URI(path);
        return documents.get(pathUri);
    }

    /**
     * Clears all documents associated with the given session.
     * 
     * @param sessionId
     *            the id of the session
     * @throws URISyntaxException if the URI does not feature a valid path
     */
    public void clear(final SessionIdentifier sessionId)
            throws URISyntaxException {
        final Collection<GrammarDocument> currentDocuments = sessionDocuments
                .get(sessionId);
        if (currentDocuments == null) {
            LOGGER.warn("session '" + sessionId + "' unknown. cannot clear");
            return;
        }
        for (GrammarDocument document : currentDocuments) {
            final URI uri = document.getURI();
            final String path = uri.getPath();
            final URI pathUri = new URI(path);
            documents.remove(pathUri);
        }
        sessionDocuments.remove(sessionId);
        LOGGER.info("cleared document storage for session '" + sessionId + "'");
    }

    /**
     * Stops the storage.
     * 
     * @throws Exception
     *             error closing the storage
     * @since 0.7.7
     */
    public void stop() throws Exception {
        if (storagePort < 0) {
            return;
        }
        server.stop();
        LOGGER.info("document storage stopped");
    }
}
