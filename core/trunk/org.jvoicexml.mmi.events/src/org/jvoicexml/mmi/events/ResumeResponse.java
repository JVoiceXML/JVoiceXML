//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.14 at 08:23:58 PM CET 
//

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
 *       &lt;attGroup ref="{http://www.w3.org/2008/04/mmi-arch}group.allResponseEvents.attrib"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "statusInfo" })
@XmlRootElement(name = "ResumeResponse")
public class ResumeResponse extends LifeCycleEvent implements Serializable {

    protected AnyComplexType statusInfo;
    @XmlAttribute(name = "Context", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    protected String context;
    @XmlAttribute(name = "Status", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    protected StatusType status;

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
     * @return possible object is {@link StatusType }
     * 
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *            allowed object is {@link StatusType }
     * 
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

}
