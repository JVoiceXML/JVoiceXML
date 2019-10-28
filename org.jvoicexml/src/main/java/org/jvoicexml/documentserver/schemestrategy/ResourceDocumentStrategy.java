/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Scheme strategy for the resource based access..
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public final class ResourceDocumentStrategy implements SchemeStrategy {
    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "res";

    /**
     * Constructs a new object.
     */
    public ResourceDocumentStrategy() {
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
            final Collection<KeyValuePair> parameters) throws BadFetchError {
        if (uri == null) {
            throw new BadFetchError("Unable to retrieve a document for null!");
        }
        final String path = uri.getSchemeSpecificPart().substring(1);
        final InputStream in = ResourceDocumentStrategy.class
                .getResourceAsStream(path);
        if (in == null) {
            throw new BadFetchError(
                    "resource '" + path + "' could not be found");
        }
        return in;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(final SessionIdentifier sessionId) {
    }
}
