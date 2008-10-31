package org.jvoicexml.systemtest.script;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name = "scriptDoc")
public class ScriptDocNode {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptDocNode.class);
    /** Debug flag. */
    private static final boolean DEBUG = false;

    /* IR test id. */
    @XmlAttribute
    public String id;

    /** actions. */
    @XmlElementRef(type = org.jvoicexml.systemtest.script.Action.class)
    List<Action> action = new LinkedList<Action>();


}
