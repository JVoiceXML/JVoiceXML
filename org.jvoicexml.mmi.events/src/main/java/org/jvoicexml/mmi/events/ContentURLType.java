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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for contentURLType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="contentURLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="href" use="required"
 *           type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="max-age"
 *           type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fetchtimeout"
 *           type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
@XmlType(name = "contentURLType")
public final class ContentURLType implements Serializable {

    /** The serial version UID. */
    private static final long serialVersionUID = -8912528620916406797L;

    /** The referenced URI. */
    private String href;

    /** Maximum age for caching the contents. */
    private  String maxAge;

    /** Maximum fetch time out for retrieving the content. */
    private String fetchtimeout;

    /**
     * Gets the value of the href property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    @XmlSchemaType(name = "anyURI")
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setHref(final String value) {
        href = value;
    }

    /**
     * Gets the value of the maxAge property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "max-age", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public String getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the value of the maxAge property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setMaxAge(final String value) {
        maxAge = value;
    }

    /**
     * Gets the value of the fetchtimeout property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "fetchtimeout", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public String getFetchtimeout() {
        return fetchtimeout;
    }

    /**
     * Sets the value of the fetchtimeout property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setFetchtimeout(final String value) {
        fetchtimeout = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(fetchtimeout, href, maxAge);
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
        if (!(obj instanceof ContentURLType)) {
            return false;
        }
        ContentURLType other = (ContentURLType) obj;
        return Objects.equals(fetchtimeout, other.fetchtimeout)
                && Objects.equals(href, other.href)
                && Objects.equals(maxAge, other.maxAge);
    }
}
