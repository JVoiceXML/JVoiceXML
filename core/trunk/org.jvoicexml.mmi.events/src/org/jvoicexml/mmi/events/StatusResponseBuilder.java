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

package org.jvoicexml.mmi.events;

import java.net.URI;

/**
 * A builder to create a status response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class StatusResponseBuilder {
    /** The created cancel response. */
    private final StatusResponse response;

    /**
     * Constructs a new object.
     */
    public StatusResponseBuilder() {
        response = new StatusResponse();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StatusResponseBuilder setContextId(final String id) {
        response.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StatusResponseBuilder setRequestId(final String id) {
        response.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     * @return this object
     */
    public StatusResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     * @return this object
     */
    public StatusResponseBuilder setSource(final URI uri) {
        response.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     * @return this object
     */
    public StatusResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     * @return this object
     */
    public StatusResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the automatic update flag.
     * @param automaticUpdate <code>true</code> if automatic updates were
     *          requested
     * @return this object
     */
    public StatusResponseBuilder setAutomaticUpdate(
            final boolean automaticUpdate) {
        response.setAutomaticUpdate(automaticUpdate);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public StatusResponseBuilder setStatusAlive() {
        response.setStatus(StatusResponseType.ALIVE);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public StatusResponseBuilder setStatusDead() {
        response.setStatus(StatusResponseType.DEAD);
        return this;
    }

    /**
     * Retrieves the status response object with all added properties.
     * @return the created status response.
     */
    public StatusResponse toStatusResponse() {
        return response;
    }
}
