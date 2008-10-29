package org.jvoicexml.systemtest.response;

import org.jvoicexml.systemtest.Action;
import org.jvoicexml.systemtest.ActionContext;

public class IgnoreAction extends Action{
    
    String message = null;

    public IgnoreAction(String string) {
        message = string;
    }

    @Override
    public void execute(ActionContext executor) {
        // TODO Auto-generated method stub
        
    }

}
