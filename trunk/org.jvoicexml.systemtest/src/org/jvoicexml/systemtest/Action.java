package org.jvoicexml.systemtest;



public abstract class Action {
    
    public abstract void execute(TestExecutor executor);

    
    public void checkOutput(TestExecutor te){
        while(te.hasNewEvent()){
            Object o = te.getNextEvent();
            if(o instanceof String){ 
                String output = (String)o;
                if (isTestFinished(output)) {
                    te.result = new Result(output);
                } 
            } else if (o instanceof Throwable){
                te.result = new Result((Throwable)o);
                break;
            } else {
                te.result = new Result("fail : " + o.toString());
                break;
            }
        }
    }

    private boolean isTestFinished(final String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            return true;
        }
        if (lowercase.indexOf("fail") >= 0) {
            return true;
        }
        return false;
    }
}
