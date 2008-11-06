package org.jvoicexml.systemtest.testcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.TestCase;

public class IRTestCase implements TestCase {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(IRTestCase.class
            .getName());

    final static String OPTIONAL = "optional";

    private final static boolean DEBUG = false;

    @XmlAttribute
    int id;

    @XmlElement(name = "assert")
    Description description;

    @XmlElement
    Start start;

    @XmlElement
    List<Dep> dep = new ArrayList<Dep>();

    private String ignoreReason = null;

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

    public String getSpec() {
        return description.spec.trim();
    }

    public int getId() {
        return description.id;
    }

    public boolean isSinglePage() {
        return dep.size() > 0 ? false : true;
    }

    public String getIgnoreReason() {
        return ignoreReason;
    }

    public void setIgnoreReason(String ignoreReason) {
        this.ignoreReason = ignoreReason;
    }

    public boolean isRequest() {
        if (OPTIONAL.equalsIgnoreCase(description.confLevel)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canAutoExec() {
        if (description.execManual == 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean completenessCheck() {
        URI checkedURI = null;
        try {
            String startPage = start.uri;
            checkedURI = baseURI.resolve(startPage.trim());
            readTextStream(checkedURI);
            for (Dep d : dep) {
                String u = d.uri;
                checkedURI = baseURI.resolve(u.trim());
                if(isText(d.type)){
                    readTextStream(checkedURI);
                } else {
                    // to do read other resource.
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("the uri " + checkedURI + " can not read. ignore this test case.", e);
            ignoreReason = "can not read. ignore this test case";
            return false;
        }
    }

    private void readTextStream(URI startUri) throws IOException,
            MalformedURLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                startUri.toURL().openStream()));
        String line = null;
        do {
            line = reader.readLine();
            if(DEBUG){
                LOGGER.debug(line);
            }
        } while (line != null);
    }
    
     boolean isText(String type){
         if(type.startsWith("text")){
             return true;
         } 
         if(type.endsWith("ircgi")){
             return true;
         } 
         return false;
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

        @XmlAttribute(name = "conf_level")
        String confLevel;

        @XmlAttribute(name = "exec_manual")
        int execManual = 0;

        @XmlAttribute(name = "abs_uri")
        int absUri = 0;

        @XmlAttribute
        String spec;

        @XmlValue
        String text;
    }

}
