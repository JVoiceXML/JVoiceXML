/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;
import java.util.List;

/**
 * A builder to create a status response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class StatusResponseBuilder {
    /** The created cancel responser */
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
     */
    public StatusResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StatusResponseBuilder setSource(final URI uri) {
        response.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StatusResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StatusResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the automatic update flag.
     * @param automaticUpdate <code>true</<code> if automatic updates were
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
