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
import javax.xml.bind.annotation.XmlElement;

/**
 * Basic MMI Lifecycle attributes.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class LifeCycleEvent {
    /** The request identifier. */
    private String requestID;

    /**
     * The source modality component or interaction manager issuing the event.
     */
    private String source;

    /**
     * The target modality component or interaction manager of the event.
     */
    private String target;

    /** Arbitrary data container. */
    private AnyComplexType data;

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
    @XmlAttribute(name = "RequestID", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final String getRequestId() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setRequestId(final String value) {
        requestID = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Source", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
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
    @XmlAttribute(name = "Target", namespace = "http://www.w3.org/2008/04/mmi-arch")
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

    /**
     * Retrieves the data attribute.
     * @return the data attribute
     */
    @XmlElement(name = "Data", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public final AnyComplexType getData() {
        return data;
    }

    /**
     * Sets the data attribute.
     * @param value new value for the data attribute
     */
    public final void setData(final AnyComplexType value) {
        data = value;
    }
}
