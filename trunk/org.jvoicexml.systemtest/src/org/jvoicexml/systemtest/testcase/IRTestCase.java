package org.jvoicexml.systemtest.testcase;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

public class IRTestCase {

    @XmlAttribute
    int id;

    @XmlElement(name = "assert")
    Description description;

    @XmlElement
    Start start;

    @XmlElement
    List<Dep> dep = new ArrayList<Dep>();

    private URI baseURI = null;

    void setBaseURI(URI base) {
        baseURI = base;
    }

    public URI getStartURI() {
        try {
            if (baseURI == null) {
                return new URI(start.uri);
            } else {
                return baseURI.resolve(start.uri);
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public int getId() {
        return description.id;
    }
    
    public boolean hasDeps(){
        return dep.size() > 0 ? true : false;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(getId());
        buff.append(" [");
        buff.append(description.spec);
        buff.append("] ");
        buff.append(" start=");
        buff.append(start.uri);
        buff.append(" deps=");
        buff.append(dep.size());
        buff.append(" \"");
        buff.append(description.text.trim());
        buff.append("\"");
        return buff.toString();
    }

    static class Dep {

        @XmlAttribute
        int id;

        @XmlAttribute
        String uri;

        @XmlAttribute
        String type;
    }

    static class Start {

        @XmlAttribute
        String uri;

        @XmlAttribute
        String type;
    }

    static class Description {

        @XmlAttribute
        int id;

        @XmlAttribute
        String spec;

        @XmlValue
        String text;
    }

}
