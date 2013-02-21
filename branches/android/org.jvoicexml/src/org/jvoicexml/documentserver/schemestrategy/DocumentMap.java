/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/DocumentMap.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Implementation of the <code>MappedDocumentRepository</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2129 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class DocumentMap
        implements MappedDocumentRepository {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(DocumentMap.class);

    /** The singleton. */
    private static final DocumentMap REPOSITORY;

    /** A simple mapping of a URI to any document. */
    private final Map<URI, String> documents =
            new java.util.Hashtable<URI, String>();

    static {
        REPOSITORY = new DocumentMap();
    }

    /**
     * Do not create from outside.
     */
    private DocumentMap() {
    }

    /**
     * Gets the singleton.
     * @return The only mapped document repository.
     */
    public static DocumentMap getInstance() {
        return REPOSITORY;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUri(final String path) throws URISyntaxException {
        return new URI(MappedDocumentStrategy.SCHEME_NAME, null, path, null);
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final String document) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding document with URI '" + uri + "'");
            LOGGER.debug(document);
        }

        documents.put(uri, document);
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final VoiceXmlDocument document) {
        addDocument(uri, document.toString());
    }

    /**
     * Gets the document with the given URI.
     *
     * @param uri URI of the document.
     * @return Document with the given URI, <code>null</code> if
     *   there is no such document or in case of an error.
     * @exception BadFetchError
     *            There is no such document.
     */
    public String getDocument(final URI uri)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting document with URI '" + uri + "'");
        }

        final String document = documents.get(uri);
        if (document == null) {
            throw new BadFetchError("No document with URI '" + uri + "'!");
        }

        return document;
    }
}
