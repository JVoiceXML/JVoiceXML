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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jvoicexml.mmi.events.xml.AnyComplexType;

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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="ContentURL" type="{http://www.w3.org/2008/04/mmi-arch}contentURLType"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="Content" type="{http://www.w3.org/2008/04/mmi-arch}anyComplexType"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="Data" type="{http://www.w3.org/2008/04/mmi-arch}anyComplexType" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}group.allEvents.attrib"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "PrepareRequest")
public class PrepareRequest extends LifeCycleRequest implements Serializable {
    /** Nested data elements. */ 
    protected List<Object> data;

    protected ContentURLType contentURL;
    protected List<Object> content;

    /**
     * Retrieves the data property.
     * @return the data property
     */
    @XmlElementWrapper(name = "Data", namespace = "http://www.w3.org/2008/04/mmi-arch")
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
        this.data = value;
    }


    /**
     * Gets the value of the contentURL property.
     * 
     * @return possible object is {@link ContentURLType }
     * 
     */
    @XmlElement(name = "ContentURL")
    public ContentURLType getContentURL() {
        return contentURL;
    }

    /**
     * Sets the value of the contentURL property.
     * 
     * @param value
     *            allowed object is {@link ContentURLType }
     * 
     */
    public void setContentURL(ContentURLType value) {
        this.contentURL = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return possible object is {@link AnyComplexType }
     * 
     */
    @XmlElement(name = "Content")
    public List<Object> getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *            allowed object is {@link AnyComplexType }
     * 
     */
    public void setContent(List<Object> value) {
        this.content = value;
    }
}
