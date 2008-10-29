package org.jvoicexml.systemtest.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.ActionContext;


@XmlRootElement(name="wait")
public class WaitAction extends Action {
    //* default wait 1 second */
    public static long DEFAULT_WAIT_TIME = 1000L;
    
    @XmlAttribute
    long timeout = DEFAULT_WAIT_TIME;
    
    @Override
    public void execute(ActionContext te){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
