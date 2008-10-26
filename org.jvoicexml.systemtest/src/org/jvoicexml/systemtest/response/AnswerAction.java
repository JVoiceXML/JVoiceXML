package org.jvoicexml.systemtest.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="answer")
public class AnswerAction extends Action {
    @XmlAttribute
    String speak;
}
