package org.jvoicexml.systemtest.script;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Answer;
import org.jvoicexml.systemtest.Script;

/**
 * For each ir test, there are a script to control test application.
 * 
 * @author lancer
 */
public class InputScript implements Script {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(InputScript.class.getName());

    final List<Action> actions = new LinkedList<Action>();

    private String id;

    /**
     * @param source
     * @throws IOException
     */
    public InputScript(String id) {
        this.id = id;
    }

    /**
     * @param source
     * @throws IOException
     */
    public InputScript() {
    }

    /**
     * @return action collection in this script
     */
    public Collection<Action> getActions() {
        return actions;
    }

    /**
     * @return action collection in this script
     */
    public final void append(Action a) {
        actions.add(a);
    }

    /**
     * @return IR test ID
     */
    public final String getId() {
        return id;
    }

    public boolean isFinished() {
        return actions.isEmpty();
    }

    public Answer perform(String event) {
        if (!actions.isEmpty()) {
            Action action = actions.get(0);
            Answer a = action.execute(event);
            if (action.finished()) {
                actions.remove(0);
            }
            return a;
        } else {
            return null;
        }
    }

}
