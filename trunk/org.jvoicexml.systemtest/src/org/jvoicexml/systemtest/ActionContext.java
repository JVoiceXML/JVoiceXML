package org.jvoicexml.systemtest;

import java.util.concurrent.TimeoutException;

public interface ActionContext {

    String nextEvent() throws  TimeoutException;

    String removeCurrentEvent();

    void answer(String speak);

    void setResult(TestResult result);
}
