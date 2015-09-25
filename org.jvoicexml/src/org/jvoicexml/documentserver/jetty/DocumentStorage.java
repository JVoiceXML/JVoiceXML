/*
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.jvoicexml.GrammarDocument;
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
    private static final Logger LOGGER = Logger
            .getLogger(DocumentStorage.class);

    /** Generated documents per session. */
    private final Map<String, Collection<GrammarDocument>> sessionDocuments;

    /** Stored documents. */
    private final Map<URI, GrammarDocument> documents;

    /** The integrated web server. */
    private Server server;

    /** Port of the document storage. */
    private int storagePort;

    /** Known grammar creators. */
    private final Map<String, GrammarCreator> creators;

    /**
     * Creates a new object.
     * 
     */
    public DocumentStorage() {
        sessionDocuments =
                new java.util.HashMap<String, Collection<GrammarDocument>>();
        documents = new java.util.HashMap<URI, GrammarDocument>();
        storagePort = 9595;
        creators = new java.util.HashMap<String, GrammarCreator>();
    }

    /**
     * Sets the storage port.
     * @param port port number for the integrated web server
     * @since 0.7.8
     */
    public void setStoragePort(final int port) {
        storagePort = port;
    }

    /**
     * Adds the specified grammar creators to the list of known grammar
     * creators.
     * @param col the creators to add
     * @since 0.7.5
     */
    public void setGrammarCreators(final Collection<GrammarCreator> col) {
        for (GrammarCreator creator : col) {
            addGrammarCreator(creator);
        }
    }

    /**
     * Adds the specified grammar creator to the list of known grammar creators.
     * @param creator the creator to add
     * @since 0.7.5
     */
    public void addGrammarCreator(final GrammarCreator creator) {
        final String type = creator.getTypeName();
        creators.put(type, creator);
        LOGGER.info("added builtin grammar creator '" + creator.getClass()
                + "' for type '" + type + "'");
    }

    /**
     * Starts the document storage. Afterwards it will be ready to serve
     * documents.
     * @throws Exception error starting the web server
     * 
     * @since 0.7.8
     */
    public void start() throws Exception {
        if (storagePort < 0) {
            return;
        }
        server = new Server(storagePort);
        final Handler handler = new DocumentHandler(this);
        server.setHandler(handler);
        server.start();
        LOGGER.info("document storage started on port " + storagePort);
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
    public URI addGrammarDocument(final String sessionId,
            final GrammarDocument document) throws URISyntaxException {
        Collection<GrammarDocument> currentDocuments =
                getCurrentSessionDocuments(sessionId);
        currentDocuments.add(document);
        final URI localUri = new URI("/" + sessionId + "/"
                + document.hashCode());
        documents.put(localUri, document);
        URI serverUri = server.getURI();
        final URI uri = new URI(serverUri +sessionId + "/"
                + document.hashCode());
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
     * @param sessionId the identifier for the session
     * @return documents for the session
     * @since 0.7.8
     */
    private Collection<GrammarDocument> getCurrentSessionDocuments(
            final String sessionId) {
        Collection<GrammarDocument> currentDocuments = sessionDocuments
                .get(sessionId);
        if (currentDocuments != null) {
            return currentDocuments;
        }
        currentDocuments = new java.util.ArrayList<GrammarDocument>();
        sessionDocuments.put(sessionId, currentDocuments);
        LOGGER.info("initialized document storage for session '"
                + sessionId + "'");
        return currentDocuments;
    }
    
    /**
     * Retrieves the content of the document.
     * 
     * @param uri
     *            URI of the document to retrieve, {@code null} if there is no
     *            such document
     * @return the document
     */
    public GrammarDocument getDocument(final URI uri) {
        return documents.get(uri);
    }

    /**
     * Clears all documents associated with the given session.
     * 
     * @param sessionId
     *            the id of the session
     */
    public void clear(final String sessionId) {
        final Collection<GrammarDocument> currentDocuments = sessionDocuments
                .get(sessionId);
        if (currentDocuments == null) {
            LOGGER.warn("session '" + sessionId + "' unknown. cannot clear");
            return;
        }
        for (GrammarDocument document : currentDocuments) {
            final URI uri = document.getURI();
            documents.remove(uri);
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
