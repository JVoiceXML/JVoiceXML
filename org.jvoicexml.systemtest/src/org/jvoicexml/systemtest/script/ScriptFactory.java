package org.jvoicexml.systemtest.script;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class ScriptFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptFactory.class);

    private static boolean useDefaultScript = true;

    private File home;

    private final String suffix = ".script.xml";

    private List<ScriptDocNode> scripts = null;

    public ScriptFactory() {

    }

    public ScriptFactory(String dir) {
        home = new File(dir);
    }

    public Script create(String id) {
        Script s;
        if (scripts != null) {
            s = findInIgnoreList(id);
            if (s != null) {
                return s;
            }
        }
        File scriptFile = new File(home, id + suffix);
        LOGGER.debug("file path = " + scriptFile.getAbsolutePath());
        LOGGER.debug("exists : " + scriptFile.exists());

        if (scriptFile.exists()) {

            try {
                s = new Script(scriptFile.toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
                s = new Script(id);
            }
            s.insertAt(0, new WaitAction());
            s.append(new ExpectResultAction());
        } else if (useDefaultScript) {
            s = createDefault(id);
        } else {
            s = null;
        }

        return s;
    }

    private Script findInIgnoreList(String id) {
        Script script = null;
        for (ScriptDocNode s : scripts) {
            if (s.id.equals(id)) {
                script = new Script(s);
            }
        }
        return script;
    }

    Script createDefault(String id) {
        Script s = new Script(id);
        s.append(new WaitAction());
        s.append(new GuessAnswerAction());
        s.append(new ExpectResultAction());
        return s;
    }

    public void setScriptsDirectory(String dir) {
        home = new File(dir);
    }

}
