package org.jvoicexml.systemtest.response;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.TestExecutor;

/**
 * For each ir test, there are a script to control test application.
 * 
 * @author lancer
 */
public class Script {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Script.class.getName());


    /** Root Document. */
    private ScriptXMLDocument rootElement;

    /**
     * @param source
     * @throws IOException
     */
    public Script(URL source) throws IOException {
        rootElement = ScriptXMLDocument.load(source.openStream());
    }
    
    /**
     * @param source
     * @throws IOException
     */
    public Script(int id) {
        rootElement = new ScriptXMLDocument();
        rootElement.id = id;
    }
    

    public void perform(TestExecutor testExecutor) {
        for(Action action : getActions()){
            action.execute(testExecutor);
            if(testExecutor.result != null){
                break;
            }
        }
    }

    /**
     * @return action collection in this script
     */
    public final Collection<Action> getActions() {
        return rootElement.action;
    }
    
    /**
     * @return action collection in this script
     */
    public final void addAction(Action a) {
        rootElement.action.add(a);
    }

    /**
     * @return IR test ID
     */
    public final int getId() {
        return rootElement.id;
    }

}
