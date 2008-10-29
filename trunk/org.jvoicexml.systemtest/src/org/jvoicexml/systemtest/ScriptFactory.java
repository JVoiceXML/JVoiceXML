package org.jvoicexml.systemtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.response.ExpectResultAction;
import org.jvoicexml.systemtest.response.GuessAnswerAction;
import org.jvoicexml.systemtest.response.IgnoreAction;
import org.jvoicexml.systemtest.response.Script;
import org.jvoicexml.systemtest.response.ScriptDocNode;
import org.jvoicexml.systemtest.response.ScriptsNode;
import org.jvoicexml.systemtest.response.WaitAction;

public class ScriptFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptFactory.class);
    
    private static boolean useDefaultScript = true;
    
    private File home;

    private final String suffix = ".script.xml";
    
    private List<ScriptDocNode> scripts = null;
    
    public ScriptFactory(){
        
    }
    
    public ScriptFactory(String dir){
        home = new File(dir);
    }

    public Script create(String id) {
        Script s;
        if(scripts != null){
            s = findInIgnoreList(id);
            if(s != null){
                return s;
            }
        }
        File scriptFile = new File(home, id + suffix);
        LOGGER.debug("file path = " + scriptFile.getAbsolutePath());
        LOGGER.debug( "exists : " + scriptFile.exists());

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
    
    private Script findInIgnoreList(String id) {
        Script script = null;
        for(ScriptDocNode s : scripts){
            if(s.id.equals(id)){
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
    
    public void setScriptsDirectory(String dir){
        home = new File(dir);
    }
    
    public void setIgnoreList(String file){
        File ignoreFile = new File(file);
        try {
            ScriptsNode list = ScriptsNode.load(new FileInputStream(ignoreFile));
            scripts = list.getList();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
