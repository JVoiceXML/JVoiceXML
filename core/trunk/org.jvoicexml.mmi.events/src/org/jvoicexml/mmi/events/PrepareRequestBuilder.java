/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;

/**
 * A builder to create a prepare request.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class PrepareRequestBuilder {
    /** The created start request. */
    private final PrepareRequest request;

    /**
     * Constructs a new object.
     */
    public PrepareRequestBuilder() {
        request = new PrepareRequest();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public PrepareRequestBuilder setContextId(final String id) {
        request.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public PrepareRequestBuilder setRequestId(final String id) {
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
     */
    public PrepareRequestBuilder setHref(final String href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href);
        return this;
    }

    /**
     * Adds a href.
     * @param href the href to add
     */
    public PrepareRequestBuilder setHref(final URI href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href.toString());
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public PrepareRequestBuilder setSource(final String uri) {
        request.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public PrepareRequestBuilder setSource(final URI uri) {
        request.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public PrepareRequestBuilder setTarget(final String uri) {
        request.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public PrepareRequestBuilder setTarget(final URI uri) {
        request.setTarget(uri.toString());
        return this;
    }

    /**
     * Retrieves the start request object with all added properties.
     * @return the created start request.
     */
    public PrepareRequest toPrepareRequest() {
        return request;
    }
}
