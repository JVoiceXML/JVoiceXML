/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/ExternalGrammarDocument.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.jvoicexml.GrammarDocument;

/**
 * A storage for documents for ASR and TTS that are generated while executing a
 * session. The main task of this component is to manage a set of URIs
 * associated with these documents.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class DocumentStorage {
    private final Server server;
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(DocumentStorage.class);

    /** Generated documents per session. */
    private final Map<String, Collection<GrammarDocument>> sessionDocuments;

    /** Stored documents. */
    private final Map<URI, GrammarDocument> documents;

    /**
     * Creates a new object.
     * 
     * @param port
     *            port number of the integrated web server.
     * @throws Exception
     *             error starting the web server.
     */
    public DocumentStorage(final int port) throws Exception {
        sessionDocuments = new java.util.HashMap<String, Collection<GrammarDocument>>();
        documents = new java.util.HashMap<URI, GrammarDocument>();
        server = new Server(port);
        final Handler handler = new DocumentHandler(this);
        server.setHandler(handler);
        server.start();
        LOGGER.info("document storage started on port " + port);
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
        Collection<GrammarDocument> currentDocuments = sessionDocuments
                .get(sessionId);
        if (currentDocuments == null) {
            currentDocuments = new java.util.ArrayList<GrammarDocument>();
            sessionDocuments.put(sessionId, currentDocuments);
            LOGGER.info("initialized document storage for session '"
                    + sessionId + "'");
        }
        currentDocuments.add(document);
        final URI localUri = new URI("/" + sessionId + "/" + document.hashCode());
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
     * Closes the storage.
     * 
     * @throws Exception
     *             error closing the storage
     * @since 0.7.7
     */
    public void close() throws Exception {
        server.stop();
        LOGGER.info("document storage stopped");
    }
}
