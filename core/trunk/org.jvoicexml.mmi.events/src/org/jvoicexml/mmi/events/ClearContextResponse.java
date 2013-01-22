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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/2008/04/mmi-arch}statusInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}group.allResponseEvents.attrib"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "ClearContextResponse")
public final class ClearContextResponse extends LifeCycleResponse
        implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -7682979762925815150L;

    /** Nested data elements. */ 
    private List<Object> statusInfo;

    /** Nested data elements. */ 
    private List<Object> data;

    /**
     * Retrieves the data property.
     * @return the data property
     */
    @XmlElementWrapper(name = "Data",
            namespace = "http://www.w3.org/2008/04/mmi-arch")
    public List<Object> getData() {
        if (data == null) {
            data = new ArrayList<Object>();
        }
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value new value for data attribute 
     */
    public void setData(final List<Object> value) {
        data = value;
    }

    /**
     * Retrieves the statusInfo property.
     * @return the statusInfo property
     */
    @XmlElementWrapper(name = "StatusInfo",
            namespace = "http://www.w3.org/2008/04/mmi-arch")
    public List<Object> getStatusInfo() {
        if (statusInfo == null) {
            statusInfo = new ArrayList<Object>();
        }
        return statusInfo;
    }

    /**
     * Sets the value of the statusInfo property.
     * 
     * @param value new value for statusInfo attribute 
     */
    public void setStatusInfo(final List<Object> value) {
        statusInfo = value;
    }
}
