package org.jvoicexml.systemtest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.event.ErrorEvent;



public abstract class Action {
    
    long DEFAULT_TIMEOUT = 1000L;
    
    public abstract void execute(ActionContext context) 
        throws ErrorEvent, TimeoutException, IOException;
    
    protected void waitMemont(long timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
    
    protected void waitMemont(){
        waitMemont(DEFAULT_TIMEOUT);
    }
}
