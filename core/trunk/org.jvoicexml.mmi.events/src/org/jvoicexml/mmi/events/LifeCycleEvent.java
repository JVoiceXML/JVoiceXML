/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
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

import javax.xml.bind.annotation.XmlAttribute;

public class LifeCycleEvent {

    private String requestID;
    private String source;
    private String target;

    /**
     * Constructs a new object.
     */
    public LifeCycleEvent() {
        super();
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "RequestID",
            namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setRequestID(final String value) {
        requestID = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Source",
            namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setSource(final String value) {
        source = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Target",
            namespace = "http://www.w3.org/2008/04/mmi-arch")
    public final String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setTarget(final String value) {
        target = value;
    }
}