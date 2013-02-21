package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Answer;

public abstract class Action {

    long DEFAULT_TIMEOUT = 1000L;

    public abstract Answer execute(String event);
    
    public boolean finished (){
        return true;
    }

    protected void waitMemont(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    protected void waitMemont() {
        waitMemont(DEFAULT_TIMEOUT);
    }
}
