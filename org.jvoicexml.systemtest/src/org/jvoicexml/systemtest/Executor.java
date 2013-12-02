/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.voicexmlunit.Call;

/**
 * Executer for one test case.
 *
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
public final class Executor implements TimeoutListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Executor.class);

    /** Max wait time. */
    public static final long MAX_WAIT_TIME = 5000L;

    /** Delay answer time. */
    public static final long DELAY_ANSWER_TIME = 500L;

    /** The current test case. */
    private final TestCase testcase;

    /** The script used with this test case. */
    private final Script script;
    /**
     * the test result.
     */
    private final Memo memo = new Memo();

    /** Status change listeners. */
    private final List<StatusListener> listeners
        = new java.util.ArrayList<StatusListener>();

    /** The current status. */
    private ClientConnectionStatus status = ClientConnectionStatus.INITIAL;

    /** Wait lock. */
    private final Object waitLock = new Object();

    /**
     * Constructs a new object.
     * @param test the test case.
     * @param answerScript the answer script of this test case.
     */
    public Executor(final TestCase test,
            final Script answerScript) {
        testcase = test;
        script = answerScript;
    }

    /**
     * Executes the test.
     * @param jvxml the interpreter.
     */
    public void execute(final JVoiceXml jvxml) {
        final URI testURI = testcase.getStartURI();

        LOGGER.info("create session and call '" + testURI + "'");
        Call call = null;
        try {
            call = new Call();
            call.call(testURI);
            script.perform(call);
        } catch (Throwable t) {
            LOGGER.error("Error calling the interpreter", t);
            memo.setFail("call session '" + t.getMessage() + "'");
            return;
        } finally {
            if (call != null) {
                call.hangup();
            }
        }
    }

    /**
     * add a status listener.
     * @param listener the status listener.
     */
    public void addStatusListener(final StatusListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void timeout(final long time) {
        memo.appendCommMsg("timeout: " + time);
        
        switch (status) {
        case INITIAL:
        case WAIT_CLIENT_CONNECT:
            memo.setFail(Result.TIMEOUT_WHEN_CONNECT);
            break;
        case WAIT_CLIENT_OUTPUT:
            memo.setFail(Result.TIMEOUT_WHEN_WAIT_OUTPUT);
            break;
        case WAIT_CLIENT_DISCONNECT:
            memo.setFail(Result.TIMEOUT_WHEN_DISCONNECT);
            break;
        case DONE:
        default:
        }
        updateStatus(status);
    }

    /**
     * Updates the status to the new status and notifies all registered
     * listeners about the status change.
     * @param newStatus the new status
     */
    private void updateStatus(final ClientConnectionStatus newStatus) {
        final ClientConnectionStatus oldStatus = status;
        status = newStatus;
        for (StatusListener listener : listeners) {
            listener.update(oldStatus, status);
        }
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
    }

    /**
     * Retrieves the intermediate result of the test case.
     * @return the result of test case.
     */
    public Result getResult() {
        return memo;
    }
}
