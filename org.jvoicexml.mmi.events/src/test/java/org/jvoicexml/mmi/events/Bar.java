package org.jvoicexml.mmi.events;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Bar")
public class Bar {
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
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
        if (!(obj instanceof Bar)) {
            return false;
        }
        Bar other = (Bar) obj;
        return Objects.equals(value, other.value);
    }

    @XmlAttribute(name = "Value", namespace="http://something", required = true)
    private String value;
    
    public Bar() {
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(final String val) {
        value = val;
    }
}
