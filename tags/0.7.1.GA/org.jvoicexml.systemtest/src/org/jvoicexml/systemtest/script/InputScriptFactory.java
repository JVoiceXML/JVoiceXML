package org.jvoicexml.systemtest.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.jvoicexml.systemtest.ScriptFactory;

public class InputScriptFactory implements ScriptFactory {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(InputScriptFactory.class);

    private final static boolean DEBUG = true;

    static boolean useDefaultScript = true;

    File home;

    final String suffix = ".script.xml";

    public InputScriptFactory() {

    }

    public InputScriptFactory(String dir) {
        home = new File(dir);
    }

    public InputScript create(String id) {
        InputScript script ;

        File scriptFile = new File(home, id + suffix);
        LOGGER.debug("file path = " + scriptFile.getAbsolutePath());
        LOGGER.debug("exists : " + scriptFile.exists());

        if (scriptFile.exists()) {
            try {
                script = create(new FileInputStream(scriptFile));
            } catch (FileNotFoundException e) {
                script = null;
            }
        } else if (useDefaultScript) {
            script = createDefault(id);
        } else {
            script = null;
        }

        return script;
    }

    InputScript create(InputStream is) {
        InputScript script = new InputScript();

        Class[] names = new Class[4];
        int i = 0;
        names[i++] = ScriptDocNode.class;
        names[i++] = SpeakAction.class;
        names[i++] = NoInputAction.class;
        names[i++] = DTMFAction.class;

        ScriptDocNode root = loadObject(ScriptDocNode.class, names, is);

        for (Action action : root.action) {
            script.append(action);
        }

        return script;
    }

    InputScript createDefault(String id) {
        InputScript script = new InputScript(id);
//        script.append(new WaitAction());
        script.append(new GuessAnswerAction());
//        script.append(new ExpectResultAction());
        return script;
    }

    public void setScriptsDirectory(String dir) {
        home = new File(dir);
    }

    /**
     * Load XML from InputStream
     * 
     * @param source
     * @return
     */
    private <T extends Object> T loadObject(Class<T> clazz,
            Class[] classNames, final InputStream source) {

        Map<String, Object> prep = new HashMap<String, Object>();
        
        try {

            JAXBContext jc = JAXBContext.newInstance(classNames, prep);
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

            return clazz.cast(um.unmarshal(new InputStreamReader(source)));

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @XmlRootElement(name = "scriptDoc")
    static class ScriptDocNode {

        /* IR test id. */
        @XmlAttribute
        public String id;

        /** actions. */
        @XmlElementRef(type = org.jvoicexml.systemtest.script.Action.class)
        List<Action> action = new LinkedList<Action>();


    }
}
