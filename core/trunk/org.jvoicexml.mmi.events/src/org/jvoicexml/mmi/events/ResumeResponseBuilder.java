/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.util.List;
import java.net.URI;

/**
 * A builder to create a resume response.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6

 */
public final class ResumeResponseBuilder {
    /** The created pause responser */
    private final ResumeResponse response;

    /**
     * Constructs a new object.
     */
    public ResumeResponseBuilder() {
        response = new ResumeResponse();
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ResumeResponseBuilder setContextId(final String id) {
        response.setContext(id);
        return this;
    }

    /**
     * Adds a context Id.
     * @param id the context id.
     * @return this object
     */
    public ResumeResponseBuilder setRequestId(final String id) {
        response.setRequestID(id);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ResumeResponseBuilder setSource(final String uri) {
        response.setSource(uri);
        return this;
    }

    /**
     * Adds a source.
     * @param uri the uri to add
     */
    public ResumeResponseBuilder setSource(final URI uri) {
        response.setSource(uri.toString());
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ResumeResponseBuilder setTarget(final String uri) {
        response.setTarget(uri);
        return this;
    }

    /**
     * Adds a target.
     * @param uri the uri to add
     */
    public ResumeResponseBuilder setTarget(final URI uri) {
        response.setTarget(uri.toString());
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public ResumeResponseBuilder setStatusSuccess() {
        response.setStatus(StatusType.SUCCESS);
        return this;
    }

    /**
     * Sets the status to success.
     * @return this object.
     */
    public ResumeResponseBuilder setStatusFailure() {
        response.setStatus(StatusType.FAILURE);
        return this;
    }

    /**
     * Adds the given status info.
     * @param info the status info to add
     * @return this object
     */
    public ResumeResponseBuilder addStatusInfo(final Object info) {
        AnyComplexType type = new AnyComplexType();
        List<Object> content = type.getContent();
        content.add(info);
        response.setStatusInfo(type);
        return this;
    }

    /**
     * Retrieves the resume response object with all added properties.
     * @return the created resume response.
     */
    public ResumeResponse toResumeResponse() {
        return response;
    }
}
