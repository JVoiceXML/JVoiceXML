package org.jvoicexml.systemtest;



public abstract class Action {
    
    long DEFAULT_TIMEOUT = 1000L;
    
    public abstract void execute(TestExecutor executor);

    
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
