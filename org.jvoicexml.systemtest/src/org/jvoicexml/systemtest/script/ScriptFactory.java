package org.jvoicexml.systemtest.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class ScriptFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptFactory.class);

    private final static boolean DEBUG = true;

    private static boolean useDefaultScript = true;

    private File home;

    private final String suffix = ".script.xml";

    public ScriptFactory() {

    }

    public ScriptFactory(String dir) {
        home = new File(dir);
    }

    public Script create(String id) {
        Script script ;

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

    Script create(InputStream is) {
        Script script = new Script();

        Class[] names = new Class[3];
        int i = 0;
        names[i++] = ScriptDocNode.class;
        names[i++] = AnswerAction.class;

        ScriptDocNode root = loadObject(ScriptDocNode.class, names, is);

        script.append(new WaitAction());
        for (Action action : root.action) {
            script.append(action);
        }
        script.append(new GuessAnswerAction());
        return script;
    }

    Script createDefault(String id) {
        Script script = new Script(id);
        script.append(new WaitAction());
        script.append(new GuessAnswerAction());
        script.append(new ExpectResultAction());
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
}
