/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Attributes describing a VoiceXML document to retrieve from the
 * {@link DocumentServer}.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class DocumentDescriptor {
    /** Mime type of an XML document. */
    public final static MimeType MIME_TYPE_XML;

    /** Mime type of an XML document. */
    public final static MimeType MIME_TYPE_SRGS_XML;

    /** MIME type of a JSON formatted document. */
    public final static MimeType MIME_TYPE_JSON;

    /** Mime type of a pure text document. */
    public final static MimeType MIME_TYPE_TEXT_PLAIN;
    
    /** Mime type of a pure text document. */
    public final static MimeType MIME_TYPE_TEXT_JAVASCRIPT;

    /** The URI of the document. */
    private URI uri;

    /** The request method. */
    private final RequestMethod method;

    /** The attributes governing the fetch. */
    private FetchAttributes attributes;

    /** Parameters for the request. */
    private final Collection<KeyValuePair> parameters;

    static {
        try {
            MIME_TYPE_XML = new MimeType("application", "xml");
            MIME_TYPE_SRGS_XML = new MimeType("application", "srgs+xml");
            MIME_TYPE_JSON = new MimeType("application", "json");
            MIME_TYPE_TEXT_PLAIN = new MimeType("text", "plain");
            MIME_TYPE_TEXT_JAVASCRIPT = new MimeType("text", "javascript");
        } catch (MimeTypeParseException e) {
            throw new RuntimeException("Failed to create MIME types", e);
        }
    }
    
    /**
     * <code>true</code> if the document must be loaded although the document
     * is in the cache.
     */
    private final boolean forceLoad;

    /** The MIME type of the document to retrieve. */
    private final MimeType type;
    
    /**
     * Constructs a new object with the request method set to
     * {@link RequestMethod#GET}.
     * @param documentUri the URI of the document.
     * @param mimeType the MIME type of the document to fetch
     */
    public DocumentDescriptor(final URI documentUri, final MimeType mimeType) {
        this(documentUri, mimeType, RequestMethod.GET, false);
    }

    /**
     * Constructs a new object.
     * @param documentUri the URI of the document.
     * @param mimeType the MIME type of the document to fetch
     * @param requestMethod the request method.
     */
    public DocumentDescriptor(final URI documentUri, final MimeType mimeType,
            final RequestMethod requestMethod) {
        this(documentUri, mimeType, requestMethod, false);
    }

    /**
     * Constructs a new object.
     * @param documentUri the URI of the document.
     * @param mimeType the MIME type of the document to fetch
     * @param requestMethod the request method.
     * @param force <code>true</code> if the document must be loaded
     */
    public DocumentDescriptor(final URI documentUri, final MimeType mimeType,
            final RequestMethod requestMethod, final boolean force) {
        uri = documentUri;
        type = mimeType;
        method = requestMethod;
        parameters = new java.util.ArrayList<KeyValuePair>();
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
     * @param pairs the parameters to add
     */
    public void addParameters(final Collection<KeyValuePair> pairs) {
        parameters.addAll(pairs);
    }
    
    /**
     * Adds the given parameter to the list of known parameters.
     * @param pair the parameter to add
     */
    public void addParameter(final KeyValuePair pair) {
        parameters.add(pair);
    }

    /**
     * Adds the given parameter to the list of known parameters.
     * @param key the name of the parameter to add
     * @param value the value of the parameter to add
     * @since 0.7.8
     */
    public void addParamater(final String key, final Object value) {
        final KeyValuePair pair = new KeyValuePair(key, value);
        addParameter(pair);
    }
    
    /**
     * Adds the given parameters to the list of known parameters.
     * @param parameters the parameters to add
     * @since 0.7.8
     */
    public void addParameters(final Map<String, Object> parameters) {
        for (String key : parameters.keySet()) {
            final Object value = parameters.get(key);
            addParamater(key, value);
        }
    }
    
    /**
     * Retrieves the known parameter.
     * @return known parameters
     */
    public Collection<KeyValuePair> getParameters() {
        return parameters;
    }

    /**
     * Retrieves the MIME type of the document to fetch.
     * @return the MIME type
     * @since 0.7.9
     */
    public MimeType getType() {
        return type;
    }

}
