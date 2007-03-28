/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URISyntaxException;

import org.jvoicexml.Application;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Implementation of the {@link Application}.
 *
 * @see org.jvoicexml.Application
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlApplication
        implements Application {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlApplication.class);

    /** The root document of this application. */
    private VoiceXmlDocument root;

    /** The current document. */
    private VoiceXmlDocument current;

    /** Base URI of the application root document. */
    private URI application;

    /** The base URI to resolve relative URIs. */
    private URI baseUri;

    /**
     * Creates a new object.
     */
    public JVoiceXmlApplication() {
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final VoiceXmlDocument doc)
        throws BadFetchError {
        if (doc == null) {
            LOGGER.warn("cannot add null document to application");

            return;
        }

        final Vxml vxml = doc.getVxml();
        try {
            baseUri = vxml.getXmlBaseUri();
            final URI currentApplication = vxml.getApplicationUri();

            if (application == null) {
                application = currentApplication;
                root = doc;
            } else if (!application.equals(currentApplication)) {
                application = baseUri;
                root = doc;
            }
        } catch (URISyntaxException e) {
            throw new BadFetchError(
                    "Error adding the document to the application", e);
        }

        current = doc;
    }

    /**
     * {@inheritDoc}
     */
    public URI getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    public URI getXmlBase() {
        return baseUri;
    }

    /**
     * {@inheritDoc}
     */
    public VoiceXmlDocument getCurrentDocument() {
        return current;
    }

    /**
     * {@inheritDoc}
     */
    public URI resolve(final URI uri) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolving URI '" + uri + "'...");
        }

        if (uri == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cannot resolve null");
            }

            return null;
        }

        if (baseUri == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Can not resolve '" + uri + "'. No base URI set.");
            }

            return uri;
        }

        final URI resolvedUri = baseUri.resolve(uri);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolved to '" + resolvedUri + "'");
        }

        return resolvedUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (application != null) {
            return application.toString();
        }

        return "Unknown application";
    }
}
