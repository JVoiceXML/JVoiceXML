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
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anyComplexType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="anyComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='skip' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anyComplexType", propOrder = { "content" })
public final class AnyComplexType implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 2818338467950863521L;

    /** The complex content of this element. */
    @XmlMixed
    @XmlAnyElement
    private List<Object> content;

    public void addContent(final Object o) {
        if (content == null) {
            content = new java.util.ArrayList<Object>();
        }
        content.add(o);
    }

    /**
     * Gets the value of the content property.
     * <p>
     * To add a new item, use {@link #addContent(Object)}.
     * </p> 
     * @return current content
     */
    public List<Object> getContent() {
        return content;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AnyComplexType other = (AnyComplexType) obj;
        return Objects.equals(content, other.content);
    }
}
