/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * An MMI status request.
 * Since the {@code context} attribute is optional, this {@link LifeCycleEvent}
 * must not inherit from {@link org.jvoicexml.mmi.events.LifeCycleRequest}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class StatusRequest extends LifeCycleEvent
    implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 4633844966738136923L;

    /** Flag, if automatic updates should be sent. */
    private boolean requestAutomaticUpdate;

    /** The context identifier, maybe, <code>null</code>. */
    private String context;

    /**
     * Constructs a new object.
     */
    public StatusRequest() {
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @since 0.7.9
     */
    public StatusRequest(final String requestId, final String source,
            final String target) {
        super(requestId, source, target);
    }

    /**
     * Gets the value of the requestAutomaticUpdate property.
     * @return  value of the requestAutomaticUpdate property
     * 
     */
    @XmlAttribute(name = "RequestAutomaticUpdate", required = true)
    public boolean isRequestAutomaticUpdate() {
        return requestAutomaticUpdate;
    }

    /**
     * Sets the value of the requestAutomaticUpdate property.
     * @param value new value for the requestAutomaticUpdate property
     * 
     */
    public void setRequestAutomaticUpdate(final boolean value) {
        requestAutomaticUpdate = value;
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(context, requestAutomaticUpdate);
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
        if (!(obj instanceof StatusRequest)) {
            return false;
        }
        StatusRequest other = (StatusRequest) obj;
        return Objects.equals(context, other.context)
                && requestAutomaticUpdate == other.requestAutomaticUpdate;
    }
}
