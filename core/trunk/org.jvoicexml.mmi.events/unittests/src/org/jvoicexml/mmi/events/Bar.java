package org.jvoicexml.mmi.events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Bar")
public class Bar {
    @XmlAttribute(name = "Value", namespace="http://something", required = true)
    private String value;
    
    public Bar() {
        // TODO Auto-generated constructor stub
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(final String val) {
        value = val;
    }
}
