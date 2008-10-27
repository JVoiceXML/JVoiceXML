package org.jvoicexml.systemtest;

public interface LogCollector {

    public abstract void start(String name);

    public abstract void stop();

}