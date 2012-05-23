/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.util.List;
import java.net.URI;

/**
 * A builder to create a pause response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class PauseResponseBuilder {
    /** The created pause responser */
    private final PauseResponse response;

    /**
     * Constructs a new object.
     */
    public PauseResponseBuilder() {
        response = new PauseResponse();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public PauseResponseBuilder setContextId(final String id) {
        response.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public PauseResponseBuilder setRequestId(final String id) {
        response.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public PauseResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public PauseResponseBuilder setSource(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public PauseResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public PauseResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public PauseResponseBuilder setStatusSuccess() {
        response.setStatus(StatusType.SUCCESS);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public PauseResponseBuilder setStatusFailure() {
        response.setStatus(StatusType.FAILURE);
        return this;
    }

    /**
     * Adds the given status info.
     * @param info the status info to add
     * @return this object
     */
    public PauseResponseBuilder addStatusInfo(final Object info) {
        AnyComplexType type = new AnyComplexType();
        List<Object> content = type.getContent();
        content.add(info);
        response.setStatusInfo(type);
        return this;
    }

    /**
     * Retrieves the pause response object with all added properties.
     * @return the created pause response.
     */
    public PauseResponse toPauseResponse() {
        return response;
    }
}
