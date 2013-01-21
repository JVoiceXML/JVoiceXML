package org.jvoicexml.mmi.events;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class LifeCycleRequest extends LifeCycleEvent {

    private String context;

    public LifeCycleRequest() {
        super();
    }

    /**
     * Gets the value of the context property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Context", namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
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

}