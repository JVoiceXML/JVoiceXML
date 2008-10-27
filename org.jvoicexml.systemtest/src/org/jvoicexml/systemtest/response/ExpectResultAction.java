package org.jvoicexml.systemtest.response;

import javax.xml.bind.annotation.XmlRootElement;

import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.TestExecutor;
import org.jvoicexml.systemtest.TestResult;

@XmlRootElement(name = "expect")
public class ExpectResultAction extends Action {
    /** default wait 1 second */
    private static long DEFAULT_WAIT_TIME = 2000L;

    @Override
    public void execute(TestExecutor te) {
        while(te.hasNewEvent()){
            Object o = te.getNextEvent();
            if(o instanceof String){ 
                String output = (String)o;
                if (isTestFinished(output)) {
                    te.result = new TestResult(output);
                } else {
                    waitMoment();
                    continue;
                }
            } else if (o instanceof Throwable){
                te.result = new TestResult((Throwable)o);
                break;
            } else {
                te.result = new TestResult("fail : " + o.toString());
                break;
            }
        }
    }

    private void waitMoment() {
        try {
            Thread.sleep(DEFAULT_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
