package org.jvoicexml.systemtest.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * For each ir test, there are a script to control test application.
 * 
 * @author lancer
 */
public class Script {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Script.class.getName());
    /** Debug flag. */
    private static final boolean DEBUG = false;

    /** Root Document. */
    private ScriptDoc rootElement;

    /**
     * @param source
     * @throws IOException
     */
    public Script(URL source) throws IOException {
        rootElement = load(source.openStream());
    }

    /**
     * Load XML from InputStream.
     * 
     * @param source
     * @return
     */
    @SuppressWarnings("unchecked")
    ScriptDoc load(final InputStream source) {

        List<Class> names = new ArrayList<Class>();
        names.add(ScriptDoc.class);
        names.add(WaitAction.class);
        names.add(AnswerAction.class);
        Map<String, Object> prep = new HashMap<String, Object>();

        try {

            JAXBContext jc = JAXBContext.newInstance(names.toArray(new Class[names.size()]), prep);
            Unmarshaller um = jc.createUnmarshaller();

            if (DEBUG) {
                um.setListener(new Unmarshaller.Listener() {
                    @Override
                    public void afterUnmarshal(Object arg0, Object arg1) {
                        super.afterUnmarshal(arg0, arg1);
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                    }

                    @Override
                    public void beforeUnmarshal(Object arg0, Object arg1) {
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                        super.beforeUnmarshal(arg0, arg1);
                    }
                });
            }

            return (ScriptDoc) um.unmarshal(new InputStreamReader(source));

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return action collection in this script
     */
    public final Collection<Action> getActions() {
        return rootElement.action;
    }

    /**
     * @return IR test ID
     */
    public final int getId() {
        return rootElement.id;
    }

    /**
     * Script XML document.
     */
    @XmlRootElement
    static class ScriptDoc {
        /* IR test id. */
        @XmlAttribute
        int id;

        /** actions. */
        @XmlElementRef(type = org.jvoicexml.systemtest.response.Action.class)
        List<Action> action = new ArrayList<Action>();
    }
}
