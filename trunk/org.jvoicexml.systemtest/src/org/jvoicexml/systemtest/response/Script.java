package org.jvoicexml.systemtest.response;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Action;

/**
 * For each ir test, there are a script to control test application.
 * 
 * @author lancer
 */
public class Script {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Script.class.getName());


    /** Root Document. */
    private final ScriptDocNode rootElement;

    /**
     * @param source
     * @throws IOException
     */
    public Script(URL source) throws IOException {
        rootElement = ScriptDocNode.load(source.openStream());
    }
    
    /**
     * @param source
     * @throws IOException
     */
    public Script(ScriptDocNode scriptDocNode) {
        rootElement = scriptDocNode;
    }
    
    /**
     * @param source
     * @throws IOException
     */
    public Script(String id) {
        rootElement = new ScriptDocNode();
        rootElement.id = id;
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
    public final void append(Action a) {
        rootElement.action.add(a);
    }

    /**
     * @return IR test ID
     */
    public final String getId() {
        return rootElement.id;
    }

    public void insertAt(int i, Action action) {
        rootElement.action.add(i, action);
    }
    
    public boolean isIgnored(){
        return rootElement.ignore == null ? false : true;
    }
    public String getIgnoredReason(){
        return rootElement.ignore;
    }
}
