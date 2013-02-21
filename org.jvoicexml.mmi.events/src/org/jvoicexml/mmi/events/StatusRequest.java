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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}target.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}requestID.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}source.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}requestAutomaticUpdate.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}context.optional.attrib"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "StatusRequest")
public final class StatusRequest extends LifeCycleEvent implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 4633844966738136923L;

    private boolean requestAutomaticUpdate;
    private String context;

    /**
     * Gets the value of the requestAutomaticUpdate property.
     * 
     */
    @XmlAttribute(name = "RequestAutomaticUpdate",
            namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public boolean isRequestAutomaticUpdate() {
        return requestAutomaticUpdate;
    }

    /**
     * Sets the value of the requestAutomaticUpdate property.
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
    @XmlAttribute(name = "Context",
            namespace = "http://www.w3.org/2008/04/mmi-arch")
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
}
