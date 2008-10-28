package org.jvoicexml.systemtest;

/**
 * Log snoop. collect interest message from log. Implements dependence on Log
 * library
 *
 * @author lancer
 * @since 2008/10/28
 */
public interface LogSnoop {

    /**
     * start collect.
     *
     * @param name
     *            collector's name
     */
    void start(String name);

    /**
     * stop collect.
     */
    void stop();

    /**
     *
     * @return collections.
     */
    Object getTrove();
}
