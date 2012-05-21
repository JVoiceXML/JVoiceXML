/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;
import java.util.List;

/**
 * A builder to create a start response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class StartResponseBuilder {
    /** The created cancel responser */
    private final StartResponse response;

    /**
     * Constructs a new object.
     */
    public StartResponseBuilder() {
        response = new StartResponse();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StartResponseBuilder setContextId(final String id) {
        response.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public StartResponseBuilder setRequestId(final String id) {
        response.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StartResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public StartResponseBuilder setSource(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StartResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public StartResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public StartResponseBuilder setStatusSuccess() {
        response.setStatus(StatusType.SUCCESS);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public StartResponseBuilder setStatusFailure() {
        response.setStatus(StatusType.FAILURE);
        return this;
    }

    /**
     * Adds the given status info.
     * @param info the status info to add
     * @return this object
     */
    public StartResponseBuilder addStatusInfo(final Object info) {
        AnyComplexType type = new AnyComplexType();
        List<Object> content = type.getContent();
        content.add(info);
        response.setStatusInfo(type);
        return this;
    }

    /**
     * Retrieves the start object with all added properties.
     * @return the created start response.
     */
    public StartResponse toStartResponse() {
        return response;
    }
}
