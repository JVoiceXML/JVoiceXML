package org.jvoicexml.mmi.events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Foo")
public class Foo {
    @XmlAttribute(name = "Value", namespace="http://none", required = true)
    private String value;

    @XmlElement(name = "Bars")
    private AnyComplexType bars;

    public Foo() {
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(final String val) {
        value = val;
    }

    public AnyComplexType getBars() {
        return bars;
    }

    public void setBars(AnyComplexType bars) {
        this.bars = bars;
    }

}
