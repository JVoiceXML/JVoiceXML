/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.mmi.events;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * An MMI status request.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class StatusResponse extends LifeCycleEvent
    implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -6344529758381887906L;

    /** Flag, if automatic updates should be sent. */
    private boolean automaticUpdate;

    /** The context identifier, maybe, <code>null</code>. */
    private String context;

    /** Status message. */
    private StatusResponseType status;

    /** Additional status info. */ 
    private AnyComplexType statusInfo;

    /**
     * Constructs a new object.
     */
    public StatusResponse() {
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @since 0.7.9
     */
    public StatusResponse(final String requestId, final String source,
            final String target) {
        super(requestId, source, target);
    }

    /**
     * Gets the value of the automaticUpdate property.
     * @return value of the automaticUpdate property
     */
    @XmlAttribute(name = "AutomaticUpdate", required = true)
    public boolean isAutomaticUpdate() {
        return automaticUpdate;
    }

    /**
     * Sets the value of the automaticUpdate property.
     * @param value new value of the automaticUpdate property
     */
    public void setAutomaticUpdate(final boolean value) {
        this.automaticUpdate = value;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Context", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public String getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setContext(final String value) {
        context = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return possible object is {@link StatusResponseType }
     * 
     */
    @XmlAttribute(name = "Status", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public StatusResponseType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *            allowed object is {@link StatusResponseType }
     * 
     */
    public void setStatus(final StatusResponseType value) {
        status = value;
    }

    /**
     * Retrieves the statusInfo property.
     * @return the statusInfo property
     */
    @XmlElement(name = "StatusInfo", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public AnyComplexType getStatusInfo() {
        return statusInfo;
    }

    /**
     * Sets the value of the statusInfo property.
     * 
     * @param value new value for statusInfo attribute 
     */
    public void setStatusInfo(final AnyComplexType value) {
        statusInfo = value;
    }

    /**
     * Adds the value of the status info property to the given text message.
     * @param text the status message to set
     */
    public void addStatusInfo(final String text) {
        if (statusInfo == null) {
            statusInfo = new AnyComplexType();
        }
        statusInfo.addContent(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + Objects.hash(automaticUpdate, context, status, statusInfo);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof StatusResponse)) {
            return false;
        }
        StatusResponse other = (StatusResponse) obj;
        return automaticUpdate == other.automaticUpdate
                && Objects.equals(context, other.context)
                && status == other.status
                && Objects.equals(statusInfo, other.statusInfo);
    }
}
