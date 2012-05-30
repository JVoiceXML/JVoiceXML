/**
 * 
 */
package org.jvoicexml.mmi.events;

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
     */
    public StartRequestBuilder setHref(final String href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href);
        return this;
    }

    /**
     * Adds a href.
     * @param href the href to add
     */
    public StartRequestBuilder setHref(final URI href) {
        ContentURLType urlType = getContentURLType();
        urlType.setHref(href.toString());
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StartRequestBuilder setSource(final String uri) {
        request.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StartRequestBuilder setSource(final URI uri) {
        request.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StartRequestBuilder setTarget(final String uri) {
        request.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
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
