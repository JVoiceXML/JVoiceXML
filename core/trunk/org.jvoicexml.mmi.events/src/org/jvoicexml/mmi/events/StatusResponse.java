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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}statusInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}automaticUpdate.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}context.optional.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}statusResponse.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}target.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}requestID.attrib"/>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}source.attrib"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "statusInfo" })
@XmlRootElement(name = "StatusResponse")
public class StatusResponse implements Serializable {

    protected AnyComplexType statusInfo;
    @XmlAttribute(name = "AutomaticUpdate", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    protected boolean automaticUpdate;
    @XmlAttribute(name = "Context", namespace = "http://www.w3.org/2008/04/mmi-arch")
    protected String context;
    @XmlAttribute(name = "Status", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    protected StatusResponseType status;

    /**
     * Gets the value of the statusInfo property.
     * 
     * @return possible object is {@link AnyComplexType }
     * 
     */
    public AnyComplexType getStatusInfo() {
        return statusInfo;
    }

    /**
     * Sets the value of the statusInfo property.
     * 
     * @param value
     *            allowed object is {@link AnyComplexType }
     * 
     */
    public void setStatusInfo(AnyComplexType value) {
        this.statusInfo = value;
    }

    /**
     * Gets the value of the automaticUpdate property.
     * 
     */
    public boolean isAutomaticUpdate() {
        return automaticUpdate;
    }

    /**
     * Sets the value of the automaticUpdate property.
     * 
     */
    public void setAutomaticUpdate(boolean value) {
        this.automaticUpdate = value;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return possible object is {@link String }
     * 
     */
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
    public void setContext(String value) {
        this.context = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return possible object is {@link StatusResponseType }
     * 
     */
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
    public void setStatus(StatusResponseType value) {
        this.status = value;
    }

}
