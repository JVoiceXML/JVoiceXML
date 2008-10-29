package org.jvoicexml.systemtest;

import java.util.concurrent.TimeoutException;

import org.jvoicexml.event.ErrorEvent;

public interface ActionContext {

    String nextEvent() throws ErrorEvent, TimeoutException;
    
    String removeCurrentEvent();
    
    void answer(String speak);
    
    void setResult(TestResult result);
}
