/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;

/**
 * A builder to create a clear context request.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class ClearContextRequestBuilder {
    /** The created start request. */
    private final ClearContextRequest request;

    /**
     * Constructs a new object.
     */
    public ClearContextRequestBuilder() {
        request = new ClearContextRequest();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ClearContextRequestBuilder setContextId(final String id) {
        request.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ClearContextRequestBuilder setRequestId(final String id) {
        request.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ClearContextRequestBuilder setSource(final String uri) {
        request.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ClearContextRequestBuilder setSource(final URI uri) {
        request.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ClearContextRequestBuilder setTarget(final String uri) {
        request.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ClearContextRequestBuilder setTarget(final URI uri) {
        request.setTarget(uri.toString());
        return this;
    }

    /**
     * Retrieves the clear context request object with all added properties.
     * @return the created clear context request.
     */
    public ClearContextRequest toClearContextRequest() {
        return request;
    }
}
