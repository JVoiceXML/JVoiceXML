package org.jvoicexml.systemtest.script;

import java.util.concurrent.TimeoutException;

import org.jvoicexml.systemtest.ActionContext;

public abstract class Action {

    long DEFAULT_TIMEOUT = 1000L;

    public abstract void execute(ActionContext context) throws TimeoutException;

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
