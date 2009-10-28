package org.jvoicexml.systemtest.script;

import org.jvoicexml.systemtest.Answer;

public abstract class Action {

    private final long DEFAULT_TIMEOUT = 1000L;

    public abstract Answer execute(String event);
    
    public boolean finished (){
        return true;
    }

    protected void waitMoment(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    protected void waitMoment() {
        waitMoment(DEFAULT_TIMEOUT);
    }
}
