/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.documentserver;

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SessionIdentifier;

/**
 * A repository that might be used for storing intermediate documents that
 * are generated while processing a VoiceXML document.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public interface DocumentRepository {
    /**
     * Starts this document repository.
     * 
     * @throws Exception
     *             error starting the document repository.
     */
    void start() throws Exception;

    /**
     * Adds the given grammar document to the documents store and retrieves the
     * URI to access it.
     * 
     * @param sessionId
     *            the id of the initiating session
     * @param document
     *            the document to add
     * @return the URI to access the document
     * @exception URISyntaxException
     *                error generating the URI for the document
     */
    URI addGrammarDocument(final SessionIdentifier sessionId,
            final GrammarDocument document) throws URISyntaxException;

    /**
     * Resolves the given URI of a builtin grammar to an URI that can be
     * handled by this document repository.
     * @param uri the builtin URI
     * @return the resolved URI
     */
    URI resolveBuiltinUri(final URI uri);
    
    /**
     * Notification that the given session is closed. Now the document
     * repository may free any resources related to the given session.
     * 
     * @param sessionId
     *            the Id of the current JVoiceXML session.
     */
    void sessionClosed(final SessionIdentifier sessionId);

    /**
     * Stops this document repository.
     * 
     * @throws Exception
     *             error document the document repository.
     */
    void stop() throws Exception;

}
