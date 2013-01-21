package org.jvoicexml.mmi.events;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Foo")
public class Foo {
    @XmlAttribute(name = "Value", namespace="http://none", required = true)
    private String value;

    @XmlElementWrapper(name = "Bars")
    private List<Object> bars;

    public Foo() {
        // TODO Auto-generated constructor stub
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(final String val) {
        value = val;
    }

    public List<Object> getBars() {
        return bars;
    }

    public void setBars(List<Object> bars) {
        this.bars = bars;
    }

}
