/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Executer for one test case.
 *
 * @author lancer
 *
 */
public final class Executor implements TextListener {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(Executor.class);

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
    private final Memo result = new Memo();

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
     * Construct a new object.
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

        final TimeoutMonitor timeoutMonitor = new TimeoutMonitor();
        listeners.add(timeoutMonitor);
        timeoutMonitor.start();
    }


    /**
     * execute the test.
     * @param jvxml the interpreter.
     */
    public void execute(final JVoiceXml jvxml) {

        Session session = null;
        final URI testURI = testcase.getStartURI();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create session and call '" + testURI + "'");
        }
        try {
            final RemoteClient client = textServer.getRemoteClient();
            session = jvxml.createSession(client);
            session.call(testURI);
            session.waitSessionEnd();
        } catch (Throwable t) {
            LOGGER.error("Error calling the interpreter", t);
            result.setFail("call session '" + t.getMessage() + "'");
            return;
        } finally {
            if (session != null) {
                session.hangup();
                session = null;
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
        result.appendCommMsg(text);

        if (result.getAssert() == TestResult.NEUTRAL) {
            if (script != null && !script.isFinished()) {
                feedback(script.perform(text));
            }
        } else {
            status = ClientConnectionStatus.WAIT_CLIENT_DISCONNECT;
        }
        fireStatusUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void started() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text server started.");
        }
        result.appendCommMsg("text server started.");
        status = ClientConnectionStatus.WAIT_CLIENT_CONNECT;
        fireStatusUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void connected(final InetSocketAddress remote) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connected to " + remote);
        }
        status = ClientConnectionStatus.WAIT_CLIENT_OUTPUT;
        result.appendCommMsg("connected to " + remote);
        fireStatusUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void disconnected() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected");
        }
        status = ClientConnectionStatus.DONE;
        result.appendCommMsg("disconnected.");
        if (result.getAssert() == TestResult.NEUTRAL) {
            result.setFail(Result.DISCONNECT_BEFORE_ASSERT);
        }
        fireStatusUpdate();
    }

    /**
     * timeout notify method.
     */
    synchronized void timeout() {
        result.appendCommMsg("timeout");
        switch (status) {
        case WAIT_CLIENT_CONNECT:
            result.setFail(Result.TIMEOUT_WHEN_CONNECT);
            break;
        case WAIT_CLIENT_OUTPUT:
            result.setFail(Result.TIMEOUT_WHEN_WAIT_OUTPUT);
            break;
        case WAIT_CLIENT_DISCONNECT:
            result.setFail(Result.TIMEOUT_WHEN_DISCONNECT);
            break;
        case DONE:
        case INITIAL:
        default:
        }
        fireStatusUpdate();
    }

    /**
     * fire the status updated.
     */
    private void fireStatusUpdate() {
        for (StatusListener listener : listeners) {
            listener.update();
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
            String speak = answer.getAnswer();
            LOGGER.debug("guess answer = " + "'" + speak + "'");
            result.appendCommMsg("ANSWER:'" + speak + "'");
            try {
                Thread.sleep(DELAY_ANSWER_TIME);
            } catch (InterruptedException e1) {
                LOGGER.debug("InterruptedException when sleeping", e1);
            }
            try {
                // callThread.session.getCharacterInput().addCharacter('1');
                LOGGER.info("send : '" + speak + "'");
                textServer.sendInput(speak);
            } catch (IOException e) {
                LOGGER.error("error sending output", e);
            }
        } else {
            LOGGER.debug("not guess suiteable answer.");
        }
    }

    /**
     * get result of test case.
     * @return the result of test case.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Test executer timeout monitor. when timeout occur, call back executer.
     * @author lancer
     *
     */
    private class TimeoutMonitor extends Thread implements StatusListener {
        /**
         * Constructs a new object.
         */
        public TimeoutMonitor() {
            setDaemon(true);
            setName("TimeoutMonitor");
        }

        /**
         * wait lock.
         */
        private final Integer myWaitLock = new Integer(0);

        /**
         * {@inheritDoc}
         */
        @Override
        public void update() {
            synchronized (myWaitLock) {
                myWaitLock.notifyAll();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            while ((status != ClientConnectionStatus.DONE)
                    && (result.getAssert() == TestResult.NEUTRAL)) {
                long markSleepTime = System.currentTimeMillis();
                synchronized (myWaitLock) {
                    LOGGER.debug("wait()");
                    try {
                        myWaitLock.wait(MAX_WAIT_TIME);
                    } catch (InterruptedException e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("wait interupted", e);
                        }
                        return;
                    }
                }
                long wakeupTime = System.currentTimeMillis();
                if (wakeupTime - markSleepTime >= MAX_WAIT_TIME) {
                    timeout();
                }
            }
        }
    }
}

