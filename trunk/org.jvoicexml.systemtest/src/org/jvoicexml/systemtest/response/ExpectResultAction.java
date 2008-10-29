package org.jvoicexml.systemtest.response;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.ActionContext;
import org.jvoicexml.systemtest.TestResult;

@XmlRootElement(name = "expect")
public class ExpectResultAction extends Action {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(ExpectResultAction.class);

    @Override
    public void execute(ActionContext te) 
            throws ErrorEvent, TimeoutException, IOException {
        LOGGER.debug("execute() ");
        while (true) {
            String output = te.nextEvent();
            if(output.equals("disconnected")){
                break;
            }
            if (isTestFinished(output)) {
                te.setResult(new TestResult(output));
                break;
            } else {
                te.removeCurrentEvent();
                waitMemont();
                continue;
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
