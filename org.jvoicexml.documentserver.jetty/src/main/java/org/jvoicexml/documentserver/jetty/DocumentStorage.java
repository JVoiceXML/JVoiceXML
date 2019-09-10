package org.jvoicexml.documentserver.jetty;
/*
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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



import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
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
public class DocumentStorage implements ContextHandlerProvider {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(DocumentStorage.class);

    /** Generated documents per session. */
    private final Map<SessionIdentifier, Collection<GrammarDocument>> sessionDocuments;

    /** Stored documents. */
    private final Map<URI, GrammarDocument> documents;

    /** Handler for internale gramamrs. */
    private final Handler internalGrammarHandler;

    /** Handler for builtin grammars. */
    private final BuiltinGrammarHandler builtinGrammarHandler;

    /** The server base uri. */
    private URI baseUri;
    
    /**
     * Creates a new object.
     */
    public DocumentStorage() {
        sessionDocuments = new java.util.HashMap<SessionIdentifier, Collection<GrammarDocument>>();
        documents = new java.util.HashMap<URI, GrammarDocument>();
        internalGrammarHandler = new InternalGrammarDocumentHandler(this);
        builtinGrammarHandler = new BuiltinGrammarHandler();
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
     * Resolves the given URI of a builtin grammar to an URI that can be handled
     * by this document server.
     * 
     * @param uri
     *            the builtin URI
     * @return the resolved URI
     * @since 0.7.8
     */
    public URI resolveBuiltinUri(final URI uri) {
        try {
            return new URI(baseUri
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
        Collection<GrammarDocument> currentDocuments = getCurrentSessionDocuments(sessionId);
        currentDocuments.add(document);
        final URI localUri = new URI(
                InternalGrammarDocumentHandler.CONTEXT_PATH + "/" + sessionId
                        + "/" + document.hashCode());
        documents.put(localUri, document);
        final URI uri = new URI(baseUri
                + InternalGrammarDocumentHandler.CONTEXT_PATH.substring(1)
                + "/" + sessionId + "/" + document.hashCode());
        document.setURI(uri);
        LOGGER.info("added grammar document at '" + uri + "'");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added document " + document);
        }
        return uri;
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
    public void clear(final SessionIdentifier sessionId) throws URISyntaxException {
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

    @Override
    public Collection<ContextHandler> getContextHandlers() {
        final Collection<ContextHandler> handlers =
                new java.util.ArrayList<ContextHandler>();
        final ContextHandler rootContext = new ContextHandler();
        rootContext.setHandler(internalGrammarHandler);
        handlers.add(rootContext);
        final ContextHandler internalGrammarContext = new ContextHandler(
                InternalGrammarDocumentHandler.CONTEXT_PATH);
        internalGrammarContext.setHandler(internalGrammarHandler);
        handlers.add(internalGrammarContext);
        final ContextHandler builtinGrammarContext = new ContextHandler(
                BuiltinGrammarHandler.CONTEXT_PATH);
        builtinGrammarContext.setHandler(builtinGrammarHandler);
        handlers.add(builtinGrammarContext);
        return handlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerUri(final URI uri) {
        baseUri = uri;
    }
}
