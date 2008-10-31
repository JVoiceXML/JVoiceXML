package org.jvoicexml.systemtest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.event.ErrorEvent;

public interface ActionContext {

    String nextEvent() throws ErrorEvent, TimeoutException, IOException;

    String removeCurrentEvent();

    void answer(String speak);

    void setResult(TestResult result);
}
