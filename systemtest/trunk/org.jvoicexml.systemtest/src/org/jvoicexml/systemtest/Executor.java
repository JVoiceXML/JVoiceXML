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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Executer for one test case.
 *
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
public final class Executor implements TextListener, TimeoutListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Executor.class);

    /** Timeout to poll for a session timeout. */
    private static final int WAIT_SESSION_END_TIMEOUT = 300;

    /**
     * max wait time.
     */
    public static final long MAX_WAIT_TIME = 5000L;

    /**
     * delay answer time.
     */
    public static final long DELAY_ANSWER_TIME = 500L;

    /**
     * the case be test.
     */
    private final TestCase testcase;

    /**
     * the script used with this test case.
     */
    private final Script script;

    /**
     * the test server.
     */
    private final TextServer textServer;

    /**
     * the test result.
     */
    private final Memo memo = new Memo();

    /**
     * status change listeners.
     */
    private final List<StatusListener> listeners
        = new java.util.ArrayList<StatusListener>();

    /**
     * current status.
     */
    private ClientConnectionStatus status = ClientConnectionStatus.INITIAL;

    /**
     * wait lock.
     */
    private final Object waitLock = new Object();

    /**
     * Constructs a new object.
     * @param test the test case.
     * @param answerScript the answer script of this test case.
     * @param server testServer.
     */
    public Executor(final TestCase test,
            final Script answerScript,
            final TextServer server) {
        testcase = test;
        script = answerScript;
        textServer = server;
    }

    /**
     * Executes the test.
     * @param jvxml the interpreter.
     */
    public void execute(final JVoiceXml jvxml) {
        Session session = null;
        final URI testURI = testcase.getStartURI();

        LOGGER.info("create session and call '" + testURI + "'");
        try {
            final ConnectionInformation client =
                textServer.getConnectionInformation();
            session = jvxml.createSession(client);
            session.call(testURI);
            waitSessionEnd();
        } catch (Throwable t) {
            LOGGER.error("Error calling the interpreter", t);
            memo.setFail("call session '" + t.getMessage() + "'");
            return;
        } finally {
            if (session != null) {
                session.hangup();
                session = null;
            }
        }
    }

    /**
     * Wait for the end of the session. In this case this is where a result
     * was received
     * @throws InterruptedException
     *         wait interrupted
     * @since 0.7.3
     */
    private void waitSessionEnd() throws InterruptedException {
        while (memo.getAssert() != TestResult.NEUTRAL) {
            synchronized (waitLock) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting for a end of session");
                }
                waitLock.wait(WAIT_SESSION_END_TIMEOUT);
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

    // implements TextListener method.

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void outputSsml(final SsmlDocument ssml) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received SsmlDocument : " + ssml.toString());
        }
        final Speak speak = ssml.getSpeak();
        final String text = speak.getTextContent();
        processReceivedText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void outputText(final String text) {
        processReceivedText(text);
    }

    /**
     * Processes the received text.
     * @param text the received text
     * @since 0.7.3
     */
    private void processReceivedText(final String text) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received Text: '" + text + "'");
        }
        memo.appendCommMsg(text);

        if (memo.getAssert() == TestResult.NEUTRAL) {
            if (script != null && !script.isFinished()) {
                final Answer answer = script.perform(text);
                feedback(answer);
            }
        } else {
            status = ClientConnectionStatus.WAIT_CLIENT_DISCONNECT;
        }
        updateStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void started() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text server started.");
        }
        memo.appendCommMsg("text server started.");
        final ClientConnectionStatus newStatus =
            ClientConnectionStatus.WAIT_CLIENT_CONNECT;
        updateStatus(newStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void connected(final InetSocketAddress remote) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connected to " + remote);
        }
        final ClientConnectionStatus newStatus =
            ClientConnectionStatus.WAIT_CLIENT_OUTPUT;
        memo.appendCommMsg("connected to " + remote);
        updateStatus(newStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void disconnected() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected");
        }
        final ClientConnectionStatus newStatus = ClientConnectionStatus.DONE;
        memo.appendCommMsg("disconnected.");
        if (memo.getAssert() == TestResult.NEUTRAL) {
            memo.setFail(Result.DISCONNECT_BEFORE_ASSERT);
        }
        updateStatus(newStatus);
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
     * answer the interpreter output.
     * @param answer the answer.
     */
    private void feedback(final Answer answer) {
        if (answer != null) {
            final String speak = answer.getAnswer();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("guess answer = " + "'" + speak + "'");
            }
            memo.appendCommMsg("ANSWER:'" + speak + "'");
            try {
                Thread.sleep(DELAY_ANSWER_TIME);
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("interrupted in delay before an answer", e);
                }
                textServer.stopServer();
                return;
            }
            try {
                LOGGER.info("send : '" + speak + "'");
                textServer.sendInput(speak);
            } catch (IOException e) {
                LOGGER.error("error sending output", e);
            }
        } else {
            LOGGER.warn("unable to guess a suiteable answer.");
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
