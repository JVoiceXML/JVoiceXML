/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.net.URI;
import java.util.List;

/**
 * A builder to create a clear context response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class ClearContextResponseBuilder {
    /** The created cancel responser */
    private final ClearContextResponse response;

    /**
     * Constructs a new object.
     */
    public ClearContextResponseBuilder() {
        response = new ClearContextResponse();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ClearContextResponseBuilder setContextId(final String id) {
        response.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ClearContextResponseBuilder setRequestId(final String id) {
        response.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ClearContextResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ClearContextResponseBuilder setSource(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ClearContextResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ClearContextResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public ClearContextResponseBuilder setStatusSuccess() {
        response.setStatus(StatusType.SUCCESS);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public ClearContextResponseBuilder setStatusFailure() {
        response.setStatus(StatusType.FAILURE);
        return this;
    }

    /**
     * Adds the given status info.
     * @param info the status info to add
     * @return this object
     */
    public ClearContextResponseBuilder addStatusInfo(final Object info) {
        AnyComplexType type = new AnyComplexType();
        List<Object> content = type.getContent();
        content.add(info);
        response.setStatusInfo(type);
        return this;
    }

    /**
     * Retrieves the clear context response object with all added properties.
     * @return the created clear context response.
     */
    public ClearContextResponse toClearContextResponse() {
        return response;
    }
}
