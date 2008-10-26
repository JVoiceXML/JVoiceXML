package org.jvoicexml.systemtest.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="wait")
public class WaitAction extends Action {
    @XmlAttribute
    long timeout = 1000L;
}
