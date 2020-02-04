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

package org.jvoicexml.interpreter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Implementation of the {@link Application}.
 *
 * @see org.jvoicexml.Application
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 */
public final class JVoiceXmlApplication
        implements Application {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(JVoiceXmlApplication.class);

    /** The root document of this application. */
    private VoiceXmlDocument root;

    /** The current document. */
    private VoiceXmlDocument current;

    /** Currently loaded documents. */
    private final Map<URI, VoiceXmlDocument> loadedDocuments;

    /** Base URI of the application root document. */
    private URI application;

    /** The base URI to resolve relative URIs. */
    private URI baseUri;

    /** The scope observer. */
    private final transient ScopeObserver observer;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlApplication() {
        this(null);
    }

    /**
     * Creates a new object.
     * @param scopeObserver the scope observer.
     */
    public JVoiceXmlApplication(final ScopeObserver scopeObserver) {
        observer = scopeObserver;
        loadedDocuments = new java.util.HashMap<URI, VoiceXmlDocument>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDocument(final URI uri, final VoiceXmlDocument doc)
        throws BadFetchError {
        if (uri == null) {
            LOGGER.warn("no URI specified");
            return;
        }

        if (doc == null) {
            LOGGER.warn("cannot add a null document to application");
            return;
        }

        final Vxml vxml = doc.getVxml();
        if (vxml == null) {
            LOGGER.warn("Is this a VoiceXML document? No vxml tag found");
            return;
        }
        try {
            baseUri = vxml.getXmlBaseUri();
            if (baseUri == null) {
                baseUri = uri;
            }
            final URI currentApplication = vxml.getApplicationUri();
            if (currentApplication == null) {
                loadedDocuments.clear();
                root = null;
            }

            if (application == null) {
                application = currentApplication;
            } else if (!application.equals(currentApplication)) {
                application = baseUri;
                loadedDocuments.clear();
                root = null;
            }
        } catch (URISyntaxException e) {
            throw new BadFetchError(
                    "Error adding the document to the application", e);
        }

        current = doc;
        final URI resolved = resolve(uri);
        final URI fragmentLess = removeFragment(resolved);
        loadedDocuments.put(fragmentLess, current);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loaded documents:");
            final Collection<URI> keys = loadedDocuments.keySet();
            for (URI loadedUri : keys) {
                LOGGER.debug("- " + loadedUri);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootDocument(final VoiceXmlDocument document)
        throws BadFetchError {
        if (root != null) {
            observer.exitScope(Scope.APPLICATION);
        }

        root = document;
        final URI applicationUri = getApplication();
        loadedDocuments.put(applicationUri, root);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loaded documents:");
            final Collection<URI> keys = loadedDocuments.keySet();
            for (URI loadedUri : keys) {
                LOGGER.debug("- " + loadedUri);
            }
        }

        observer.enterScope(Scope.APPLICATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getApplication() throws BadFetchError {
        return resolve(application);
    }

    @Override
    public List<URI> getLoadedDocuments() {
        final Collection<URI> keys = loadedDocuments.keySet();
        List<URI> list = new java.util.ArrayList<URI>();
        list.addAll(keys);
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public URI getXmlBase() {
        return baseUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlDocument getCurrentDocument() {
        return current;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI uri) throws BadFetchError {
        return resolve(baseUri, uri);
    }

    /**
     * Removes the fragment from the given URI.
     * @param uri the URI to remove the fragment from
     * @return URI without a fragment.
     */
    private URI removeFragment(final URI uri) {
        final String fragment = uri.getFragment();
        if (fragment == null) {
            return uri;
        }

        try {
            return new URI(uri.getScheme(), uri.getUserInfo(),
                    uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(),
                    null);
        } catch (URISyntaxException e) {
            LOGGER.warn("unable to remove the fragment from '" + uri + "'", e);
            return uri;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI base, final URI uri) throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolving URI '" + uri + "'...");
        }

        if (uri == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cannot resolve null");
            }

            return null;
        }
        final URI currentBase;
        if (base == null) {
            if (baseUri == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Can not resolve '" + uri
                            + "'. No base URI set.");
                }

                return uri;
            } else {
                currentBase = baseUri;
            }
        } else {
            currentBase = base;
        }

        final String host = currentBase.getHost();
        final URI resolvedUri;
        if ((host == null) && !uri.isAbsolute()) {
            final String scheme = currentBase.getScheme();
            final String path = uri.getSchemeSpecificPart();
            try {
                resolvedUri = new URI(scheme + "://" + path);
            } catch (URISyntaxException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        } else {
            resolvedUri = currentBase.resolve(uri);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolved to '" + resolvedUri + "'");
        }

        return resolvedUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded(final URI uri) {
        if (uri == null) {
            return false;
        }
        final String fragment = uri.getFragment();
        if (fragment == null) {
            return loadedDocuments.containsKey(uri);
        } else {
            final URI fragmentLess = removeFragment(uri);
            return loadedDocuments.containsKey(fragmentLess);
        }
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
