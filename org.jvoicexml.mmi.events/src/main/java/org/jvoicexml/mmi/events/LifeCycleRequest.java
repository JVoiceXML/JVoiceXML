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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Base class for MMI lifecycle requests.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public class LifeCycleRequest extends LifeCycleEvent {
    /** The context identifier of this request. */ 
    private String context;

    /**
     * Constructs a new object.
     */
    public LifeCycleRequest() {
        super();
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @since 0.7.9
     */
    public LifeCycleRequest(final String requestId, final String source,
            final String target) {
        super(requestId, source, target);
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @param ctx the context
     * @since 0.7.9
     */
    public LifeCycleRequest(final String requestId, final String source,
            final String target, final String ctx) {
        super(requestId, source, target);
        context = ctx;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Context", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final String getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setContext(final String value) {
        context = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(context);
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
        if (!(obj instanceof LifeCycleRequest)) {
            return false;
        }
        LifeCycleRequest other = (LifeCycleRequest) obj;
        return Objects.equals(context, other.context);
    }
}
