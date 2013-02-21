/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/DocumentDescriptor.java $
 * Version: $LastChangedRevision: 2509 $
 * Date:    $Date: 2011-01-16 07:40:14 -0600 (dom, 16 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.net.URI;
import java.util.Map;

import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Attributes describing a VoiceXML document to retrieve from the
 * {@link DocumentServer}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2509 $
 * @since 0.7
 */
public final class DocumentDescriptor {
    /** The URI of the document. */
    private URI uri;

    /** The request method. */
    private final RequestMethod method;

    /** The attributes governing the fetch. */
    private FetchAttributes attributes;

    /** Parameters for the request. */
    private final Map<String, Object> parameters;

    /**
     * <code>true</code> if the document must be loaded although the document
     * is in the cache.
     */
    private final boolean forceLoad;

    /**
     * Constructs a new object with the request method set to
     * {@link RequestMethod#GET}.
     * @param documentUri the URI of the document.
     */
    public DocumentDescriptor(final URI documentUri) {
        this(documentUri, RequestMethod.GET, false);
    }

    /**
     * Constructs a new object.
     * @param documentUri the URI of the document.
     * @param requestMethod the request method.
     */
    public DocumentDescriptor(final URI documentUri,
            final RequestMethod requestMethod) {
        this(documentUri, requestMethod, false);
    }

    /**
     * Constructs a new object.
     * @param documentUri the URI of the document.
     * @param requestMethod the request method.
     * @param force <code>true</code> if the document must be loaded
     */
    public DocumentDescriptor(final URI documentUri,
            final RequestMethod requestMethod, final boolean force) {
        uri = documentUri;
        method = requestMethod;
        parameters = new java.util.HashMap<String, Object>();
        forceLoad = force;
    }

    /**
     * Sets the URI of the document.
     * @param value URI of the document.
     */
    public void setURI(final URI value) {
        uri = value;
    }

    /**
     * Retrieves the URI of the document to fetch.
     * @return URI of the document.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Checks if the document has to be retrieved although the document is in
     * the cache.
     * @return <code>true</code> if the document must be retrieved.
     * @since 0.7.5
     */
    public boolean isForceLoad() {
        return forceLoad;
    }

    /***
     * Retrieves the request method.
     * @return the request method.
     */
    public RequestMethod getMethod() {
        return method;
    }

    /**
     * Retrieve the fetch attributes.
     * @return the attributes
     */
    public FetchAttributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the fetch attributes.
     * @param value the attributes to set
     */
    public void setAttributes(final FetchAttributes value) {
        attributes = value;
    }

    /**
     * Adds the given parameter to the list of known parameters.
     * @param name name of the parameter
     * @param value value of the parameter
     */
    public void addParameter(final String name, final Object value) {
        parameters.put(name, value);
    }

    /**
     * Retrieves the known parameter.
     * @return known parameters
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
