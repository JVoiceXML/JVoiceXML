package org.jvoicexml.systemtest.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.TestExecutor;


@XmlRootElement(name="wait")
public class WaitAction extends Action {
    //* default wait 1 second */
    private static long DEFAULT_WAIT_TIME = 5000L;
    
    @XmlAttribute
    long timeout = DEFAULT_WAIT_TIME;
    
    @Override
    public void execute(TestExecutor te){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
