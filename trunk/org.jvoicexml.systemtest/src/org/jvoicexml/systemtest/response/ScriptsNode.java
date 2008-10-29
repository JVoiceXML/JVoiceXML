package org.jvoicexml.systemtest.response;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name="scripts")
public class ScriptsNode {
    
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptsNode.class);
    /** Debug flag. */
    private static final boolean DEBUG = true;
    
    /** actions. */
    @XmlElement(name="scriptDoc")
    List<ScriptDocNode> list = new LinkedList<ScriptDocNode>();
    
    public List<ScriptDocNode> getList() {
        return list;
    }

    /**
     * Load XML from InputStream.
     * 
     * @param source
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ScriptsNode load(final InputStream source) {

        List<Class> names = new ArrayList<Class>();
        names.add(ScriptsNode.class);
        names.add(ScriptDocNode.class);
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

            return (ScriptsNode) um.unmarshal(new InputStreamReader(source));

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
