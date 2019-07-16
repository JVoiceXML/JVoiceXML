/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Scheme strategy for the {@link MappedDocumentRepository}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class MappedDocumentStrategy
        implements SchemeStrategy {
    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "jvxmlmap";

    /**
     * Constructs a new object.
     */
    public MappedDocumentStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScheme() {
        return SCHEME_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(final SessionIdentifier sessionId,
            final URI uri, final RequestMethod method, final long timeout,
            final Collection<KeyValuePair> parameters)
            throws BadFetchError {
        if (uri == null) {
            throw new BadFetchError("Unable to retrieve a document for null!");
        }
        final DocumentMap repository = DocumentMap.getInstance();

        // A jvxmlmap based URI may only have a scheme and a path.
        final String scheme = uri.getScheme();
        final String path = uri.getPath();
        final URI fragmentLessUri;
        try {
            fragmentLessUri = new URI(scheme, null, path, null);
        } catch (URISyntaxException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        final String document = repository.getDocument(fragmentLessUri);
        if (document == null) {
            return null;
        }

        String encoding = getEncoding(document);
        if (encoding == null) {
            encoding = System.getProperty("jvoicexml.xml.encoding", "UTF-8");
        }
        try {
            return new ByteArrayInputStream(document.getBytes(encoding));
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }

    /**
     * Simple hack to determine the encoding of an XML document.
     * @param document the document to analyze.
     * @return encoding of the document, <code>null</code> if there is no
     *         encoding.
     */
    private String getEncoding(final String document) {
        if (document.startsWith("<?xml")) {
            return null;
        }

        int encStart = document.indexOf("encoding");
        if (encStart < 0) {
            return null;
        }

        int encValueStart = document.indexOf('\"', encStart);
        int encValueEnd = document.indexOf('\"', encValueStart + 1);
        return document.substring(encValueStart + 1, encValueEnd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(final SessionIdentifier sessionId) {
    }
}
