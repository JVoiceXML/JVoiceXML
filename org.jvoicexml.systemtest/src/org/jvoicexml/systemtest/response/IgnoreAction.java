package org.jvoicexml.systemtest.response;

import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.TestExecutor;

public class IgnoreAction extends Action{
    
    String message = null;

    public IgnoreAction(String string) {
        message = string;
    }

    @Override
    public void execute(TestExecutor executor) {
        // TODO Auto-generated method stub
        
    }

}
