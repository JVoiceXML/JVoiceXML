/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.net.URI;
import java.net.URISyntaxException;

import javax.activation.MimeType;
import javax.sound.sampled.AudioInputStream;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * A <em>document server</em> processes <em>requests</em> from a client
 * application.
 *
 * <p>
 * Usually these requests serve to obtain resources like VoiceXML documents,
 * grammar documents and audio files from a repository.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 *
 * @since 0.5.5
 */
public interface DocumentServer {
    /**
     * Starts this document server.
     * 
     * @throws Exception
     *             error starting the document server.
     * @since 0.7.7
     */
    void start() throws Exception;

    /**
     * Gets the document with the given URI.
     *
     * @param sessionId
     *            the Id of the current JVoiceXML session
     * @param descriptor
     *            descriptor for the document to fetch.
     * @return VoiceXML document with the given URI.
     * @exception BadFetchError
     *                The URI does not reference a document or an error occurred
     *                retrieving the document.
     */
    VoiceXmlDocument getDocument(final SessionIdentifier sessionId,
            final DocumentDescriptor descriptor) throws BadFetchError;

    /**
     * Resolves the given URI of a builtin grammar to an URI that can be
     * handled by this document server.
     * @param uri the builtin URI
     * @return the resolved URI
     * @since 0.7.8
     */
    URI resolveBuiltinUri(final URI uri);

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
     * @since 0.7.7
     */
    URI addGrammarDocument(final SessionIdentifier sessionId,
            final GrammarDocument document) throws URISyntaxException;

    /**
     * Returns the external Grammar referenced by <code>URI</code>.
     *
     * <p>
     * If more than one grammar is available at the given URI, the grammar with
     * the optional type is preferred. If preferredType is null, the
     * ContentServer does not have to care about the preferredType.
     * </p>
     *
     * @param sessionId
     *            the Id of the current JVoiceXML session
     * @param uri
     *            Where to find the grammar.
     * @param type
     *            the MIME type of the grammar
     * @param attributes
     *            attributes governing the fetch.
     *
     * @return the grammar referenced by the URI.
     *
     * @throws BadFetchError
     *             The URI does not reference a document or an error occurred
     *             retrieving the document.
     */
    GrammarDocument getGrammarDocument(final SessionIdentifier sessionId,
            final URI uri, final MimeType type,
            final FetchAttributes attributes)
                    throws BadFetchError;

    /**
     * Retrieves an <code>AudioStream</code> to the audio file with the given
     * <code>URI</code>.
     *
     * @param sessionId
     *            the Id of the current JVoiceXML session
     * @param uri
     *            URI of the audio file.
     * @return <code>AudioInputStream</code> for the audio file.
     * @exception BadFetchError
     *                Error retrieving the audio file.
     */
    AudioInputStream getAudioInputStream(final SessionIdentifier sessionId,
            final URI uri) throws BadFetchError;

    /**
     * Retrieves an object of the given type from the given URI.
     * 
     * @param sessionId
     *            the Id of the current JVoiceXML session
     * @param descriptor
     *            descriptor for the document to fetch.
     * @return retrieved object
     * @throws BadFetchError
     *             Error retrieving the object.
     * @since 0.6
     */
    Object getObject(final SessionIdentifier sessionId,
            final DocumentDescriptor descriptor)
            throws BadFetchError;

    /**
     * Stores the audio from the given stream.
     * 
     * @param in
     *            stream to read the audio data from
     * @return URI of the file.
     * @throws org.jvoicexml.event.error.BadFetchError
     *             Error writing.
     */
    URI storeAudio(final AudioInputStream in) throws BadFetchError;

    /**
     * Notification that the given session is closed. Now the document server
     * may free any resources related to the given session.
     * 
     * @param sessionId
     *            the Id of the current JVoiceXML session.
     * @since 0.7
     */
    void sessionClosed(final SessionIdentifier sessionId);

    /**
     * Stops this document server.
     * 
     * @since 0.7.7
     */
    void stop();
}
