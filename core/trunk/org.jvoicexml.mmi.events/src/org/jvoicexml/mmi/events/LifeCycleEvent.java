package org.jvoicexml.mmi.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.w3c.dom.Node;

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
    @XmlAttribute(name = "RequestID", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Source", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Target", namespace = "http://www.w3.org/2008/04/mmi-arch")
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setTarget(String value) {
        this.target = value;
    }
}