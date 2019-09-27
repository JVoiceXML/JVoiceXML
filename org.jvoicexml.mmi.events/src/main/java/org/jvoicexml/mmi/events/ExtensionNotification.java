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

/**
 * An MMI extension notification.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class ExtensionNotification extends LifeCycleRequest
        implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -2806457933946563396L;

    /** Name of the extension. */
    private String name;

    /**
     * Constructs a new object.
     */
    public ExtensionNotification() {
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @param context the context
     * @since 0.7.9
     */
    public ExtensionNotification(final String requestId, final String source,
            final String target, final String context) {
        super(requestId, source, target, context);
    }
    
    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Name", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(name);
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
        if (!(obj instanceof ExtensionNotification)) {
            return false;
        }
        ExtensionNotification other = (ExtensionNotification) obj;
        return Objects.equals(name, other.name);
    }
}
