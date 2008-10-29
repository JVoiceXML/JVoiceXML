package org.jvoicexml.systemtest;

import java.io.File;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.response.ExpectResultAction;
import org.jvoicexml.systemtest.response.GuessAnswerAction;
import org.jvoicexml.systemtest.response.IgnoreAction;
import org.jvoicexml.systemtest.response.Script;
import org.jvoicexml.systemtest.response.WaitAction;

public class ScriptFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptFactory.class);
    
    private static boolean useDefaultScript = true;
    
    private final File home;
    private final String suffix = ".script.xml";
    
    public ScriptFactory(String dir){
        home = new File(dir);
    }

    public Script create(String id) {
        File scriptFile = new File(home, id + suffix);
        LOGGER.debug("file path = " + scriptFile.getAbsolutePath());
        LOGGER.debug( "exists : " + scriptFile.exists());
        Script s;
        if(scriptFile.exists()){
           
            try {
                s = new Script(scriptFile.toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
                s =  new Script(id);
            }
            s.insertAt(0, new WaitAction());
            s.append(new ExpectResultAction());
        } else if (useDefaultScript){
            s = createDefault(id) ;
        } else {
            s = new Script(id);
            s.append(new IgnoreAction("script not found."));
        }

        return s;
    }
    
    Script createDefault(String id) {
        Script s = new Script(id);
        s.append(new WaitAction());
        s.append(new GuessAnswerAction());
        s.append(new ExpectResultAction());
        return s;
    }

}
