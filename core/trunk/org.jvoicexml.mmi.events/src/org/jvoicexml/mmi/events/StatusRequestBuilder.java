/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;

/**
 * A builder to create a status request.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class StatusRequestBuilder {
    /** The created start request. */
    private final StatusRequest request;

    /**
     * Constructs a new object.
     */
    public StatusRequestBuilder() {
        request = new StatusRequest();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StatusRequestBuilder setContextId(final String id) {
        request.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StatusRequestBuilder setRequestId(final String id) {
        request.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StatusRequestBuilder setSource(final String uri) {
        request.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StatusRequestBuilder setSource(final URI uri) {
        request.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StatusRequestBuilder setTarget(final String uri) {
        request.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StatusRequestBuilder setTarget(final URI uri) {
        request.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the flag to enable automatic status update sending
     * @param enable <code>true</code> if periodic status update messages
     *          should be sent
     * @return this object
     */
    public StatusRequestBuilder setAutomaticUpdate(final boolean enable) {
        request.setRequestAutomaticUpdate(enable);
        return this;
    }

    /**
     * Retrieves the status request object with all added properties.
     * @return the created status request.
     */
    public StatusRequest toStatusRequest() {
        return request;
    }
}
