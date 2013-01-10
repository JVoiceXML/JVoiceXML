/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mmi.events.xml;

import java.net.URI;

/**
 * A builder to create a start request.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class StartRequestBuilder {
    /** The created start request. */
    private final StartRequest request;

    /**
     * Constructs a new object.
     */
    public StartRequestBuilder() {
        request = new StartRequest();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StartRequestBuilder setContextId(final String id) {
        request.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StartRequestBuilder setRequestId(final String id) {
        request.setRequestID(id);
        return this;
    }

    /**
     * Lazy instantiation of the content URL type.
     * @return content url type
     */
    private ContentURLType getContentURLType() {
        ContentURLType urlType = request.getContentURL();
        if (urlType == null) {
            urlType = new ContentURLType();
            request.setContentURL(urlType);
        }
        return urlType;
    }

    /**
     * Adds a href.
     * @param href the href to add
     * @return this object
     */
    public StartRequestBuilder setHref(final String href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href);
        return this;
    }

    /**
     * Adds a href.
     * @param href the href to add
     * @return this object
     */
    public StartRequestBuilder setHref(final URI href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href.toString());
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     * @return this object
     */
    public StartRequestBuilder setSource(final String uri) {
        request.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     * @return this object
     */
    public StartRequestBuilder setSource(final URI uri) {
        request.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     * @return this object
     */
    public StartRequestBuilder setTarget(final String uri) {
        request.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     * @return this object
     */
    public StartRequestBuilder setTarget(final URI uri) {
        request.setTarget(uri.toString());
        return this;
    }

    /**
     * Retrieves the start request object with all added properties.
     * @return the created start request.
     */
    public StartRequest toStartRequest() {
        return request;
    }
}
