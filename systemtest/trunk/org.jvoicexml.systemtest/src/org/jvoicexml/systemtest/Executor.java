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
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
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
     * initial status.
     */
    public static final int INITIAL = 0;
    /**
     * wait client connect status.
     */
    public static final int WAIT_CLIENT_CONNECT = INITIAL + 1;
    /**
     * wait client output status.
     */
    public static final int WAIT_CLIENT_OUTPUT = WAIT_CLIENT_CONNECT + 1;
    /**
     * wait client disconnect status.
     */
    public static final int WAIT_CLIENT_DISCONNECT = WAIT_CLIENT_OUTPUT + 1;
    /**
     * all step down status.
     */
    public static final int DONE = WAIT_CLIENT_DISCONNECT + 1;

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
    private final List<StatusListener> listeners = new Vector<StatusListener>();

    /**
     * current status.
     */
    private int status = INITIAL;

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

        Session session;

        final URI testURI = testcase.getStartURI();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create session and call it.");
        }
        try {
            final RemoteClient client = textServer.getRemoteClient();
            session = jvxml.createSession(client);
            session.call(testURI);
        } catch (Throwable t) {
            LOGGER.error("Error calling the interpreter", t);
            result.setFail("call session");
            return;
        }

        int maxCount = 20;
        while (maxCount-- > 0) {
            if (result.getAssert() != Result.NEUTRAL) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(result.getAssert());
                }
                break;
            }
            synchronized (waitLock) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting for a connection");
                }
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("wake up");
                    }
                }
            }
        }

        if (maxCount == 0) {
            LOGGER.error("max count !!!!");
        }

        if (session != null) {
            session.hangup();
            session = null;
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
        String xmlString = ssml.toString();
        int index = xmlString.indexOf("<speak>");
        String speak = xmlString.substring(index);
        String text = speak.replace("<speak>", "").replaceAll("</speak>", "")
                .trim();
        outputText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void outputText(final String text) {
        LOGGER.debug("Received Text : " + text);
        result.appendCommMsg(text);

        if (result.getAssert() == Result.NEUTRAL) {
            if (script != null && !script.isFinished()) {
                feedback(script.perform(text));
            }
        } else {
            status = WAIT_CLIENT_DISCONNECT;
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
        status = WAIT_CLIENT_CONNECT;
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
        status = WAIT_CLIENT_OUTPUT;
        result.appendCommMsg("connected to" + remote);
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
        status = DONE;
        result.appendCommMsg("disconnected.");
        if (result.getAssert() == Result.NEUTRAL) {
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
            while (status < DONE && result.getAssert() == Result.NEUTRAL) {
                long markSleepTime = new Date().getTime();
                synchronized (myWaitLock) {
                    LOGGER.debug("wait()");
                    try {
                        myWaitLock.wait(MAX_WAIT_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.debug("wake up");
                    }
                }
                long wakeupTime = new Date().getTime();
                if (wakeupTime - markSleepTime >= MAX_WAIT_TIME) {
                    timeout();
                }
            }
        }
    }
}

