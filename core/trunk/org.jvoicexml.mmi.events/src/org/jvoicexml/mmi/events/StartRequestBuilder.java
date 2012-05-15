/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;

/**
 * A builder to create a start request.
 * 
 * @author Dirk Schnelle
 * 
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
    public StartRequestBuilder addContextId(final String id) {
        request.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StartRequestBuilder addRequestId(final String id) {
        request.setRequestID(id);
        return this;
    }

    /**
     * Adds a href.
     * @param href the href to add
     */
    public StartRequestBuilder addHref(final String href) {
        ContentURLType urlType = request.getContentURL();
        if (urlType == null) {
            urlType = new ContentURLType();
            request.setContentURL(urlType);
        }
        urlType.setHref(href);
        return this;
    }

    /**
     * Adds a href.
     * @param href the href to add
     */
    public StartRequestBuilder addHref(final URI href) {
        ContentURLType urlType = request.getContentURL();
        if (urlType == null) {
            urlType = new ContentURLType();
            request.setContentURL(urlType);
        }
        urlType.setHref(href.toString());
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
