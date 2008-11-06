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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * executer of one test case.
 * @author lancer
 *
 */
public class TestExecutor implements TextListener {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(TestExecutor.class);
    public final static long MAX_WAIT_TIME = 5000L;
    // public final static long ANSWER_WAIT_TIME = 5000L;
    public final static long DELAY_ANSWER_TIME = 1000L;

    private static String CONNECTED = "connected";
    private static String DISCONNECTED = "disconnected";

    private final Script script;

    private TextServer textServer;

    private int textServerPort;

    private TestResult result = null;

    private Queue<String> jvxmlEvents = new ConcurrentLinkedQueue<String>();

    private Session session = null;

    public TestExecutor(Script s, int port) {
        script = s;
        textServerPort = port;
    }

    public TestResult execute(JVoiceXml jvxml, TestCase testcase) {

        LOGGER.info("\n\n");
        LOGGER.info("###########################################");
        LOGGER.info("start testcase : " + testcase.toString());
        URI testURI = testcase.getStartURI();
        LOGGER.info("start uri : " + testURI.toString());

        LOGGER.info("start TextServer at port " + textServerPort);
        textServer = new TextServer(textServerPort);
        textServer.addTextListener(this);
        textServer.start();
        waitForMoment(); // wait TextServer start.

        try {
            LOGGER.debug("create session and call it.");
            try {
                RemoteClient remoteClient = textServer.getRemoteClient();
                session = jvxml.createSession(remoteClient);
                session.call(testURI);
                LOGGER.debug("session.call() returned.");
            } catch (Throwable t) {
                LOGGER.error("Throwable catched.", t);
                result = new TestResult(t, "call session");
                return result;
            }

            try {
                waitClientConnected();
            } catch (IOException te) {
                Exception e = new TimeoutException("no response in " + MAX_WAIT_TIME + "ms");
                result = new TestResult(e, "connect.");
                return result;
            }
            boolean hasDisconnected = false;
            try {
                while (true) {
                    String event = nextEvent();
                    removeCurrentEvent();
                    if (DISCONNECTED.equals(event)) {
                        hasDisconnected = true;
                        if (result == null) {
                            result = new TestResult(TestResult.FAIL, "catch disconnect which not expected");
                        }
                        return result;
                    }
                    if (isAssertEvent(event)) {
                        result = new TestResult(event);
                        return result;
                    }
                    
                    LOGGER.debug("output = " + event);
                    if (!script.isFinished()) {
                        Answer a = script.perform(event);
                        if (a != null) {
                            waitForMoment();
                            LOGGER.debug("guess answer = " + a.getAnswer());
                            answer(a.getAnswer());
                        } else {
                            synchronized (jvxmlEvents) {
                                if(jvxmlEvents.isEmpty()){
                                    LOGGER.debug("not guess suitable answer, exit.");
                                    result = new TestResult("fail", "not guess suitable answer");
                                    return result;
                                } else {
                                    continue;
                                }
                            }

                        }
                    }
                }

            } catch (TimeoutException e) {

                LOGGER.debug("Throwable catched.");

                ErrorEvent t = null;
                try {
                    t = session.getLastError();
                } catch (ErrorEvent e1) {
                }
                if (t != null) {
                    result = new TestResult(t, "Error in session.");
                } else {
                    result = new TestResult(e, "script perform.");
                }

            } finally {
                LOGGER.debug("finally");
                if (session != null) {
                    session.hangup();
                    session = null;
                }
                if (!hasDisconnected) {
                    try {
                        waitDisconnected();
                    } catch (TimeoutException e) {
                        LOGGER.error("finally catch");
                        result = new TestResult(e, "disconnect");
                    }
                }
            }
        } finally {
            textServer.stopServer();
        }

        return result;
    }

    private boolean isAssertEvent(final String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            return true;
        }
        if (lowercase.indexOf("fail") >= 0) {
            return true;
        }
        return false;
    }

    private void waitClientConnected() throws IOException {
        LOGGER.debug("wait Clien tConnected");

        Timer timer = new Timer(Thread.currentThread());
        timer.start();

        textServer.waitConnected();
        timer.stopTimer();

    }

    private void waitDisconnected() throws TimeoutException {
        LOGGER.debug("waitDisconnected() ");
        while (true) {
            Object event = nextEvent();
            if (DISCONNECTED.equals(event)) {
                break;
            } else {
                jvxmlEvents.poll();
            }
        }

        /*
         * Must wait some time, because TextServer only accept one client. If
         * not, When TextServer Thread not back call ServerSocket.accept(), any
         * client can not connect to server, then can not create TextTelephony
         * resource, throw noresource.error.
         */
        waitForMoment();
    }

    private void waitForMoment() {
        try {
            Thread.sleep(DELAY_ANSWER_TIME);
        } catch (InterruptedException e) {
        }
    }

    private String nextEvent() throws TimeoutException {
        String event;
        synchronized (jvxmlEvents) {
            event = jvxmlEvents.peek();
            if (event == null) {
                try {
                    jvxmlEvents.wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                }
                event = jvxmlEvents.peek();
                if (event == null) {

                    throw new TimeoutException("no response in " + MAX_WAIT_TIME + "ms");
                }
            }
        }

        return event;
    }

    private void answer(String speak) {
        try {
            // callThread.session.getCharacterInput().addCharacter('1');
            LOGGER.error("send : '" + speak + "'");
            textServer.sendInput(speak);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeCurrentEvent() {
        String event = null;
        synchronized (jvxmlEvents) {
            event = jvxmlEvents.poll();
        }
        LOGGER.debug("removeCurrentEvent : " + event);
        return event;
    }

    // implements TextListener method.

    @Override
    public void outputSsml(SsmlDocument arg0) {
        LOGGER.debug("Received SsmlDocument : " + arg0.toString());
        String s = arg0.toString();
        int index = s.indexOf("<speak>");
        offer(s.substring(index));
    }

    @Override
    public void outputText(String arg0) {
        LOGGER.debug("Received Text : " + arg0);
        offer(arg0);
    }

    @Override
    public void connected(InetSocketAddress remote) {
        LOGGER.debug("connected to " + remote.toString());
        // offer(CONNECTED);
    }

    @Override
    public void disconnected() {
        LOGGER.debug("disconnected");
        offer(DISCONNECTED);
    }

    private void offer(String arg0) {
        synchronized (jvxmlEvents) {
            jvxmlEvents.offer(arg0);
            jvxmlEvents.notifyAll();
        }
    }

    /**
     * For timeout check.
     * 
     * @author lancer
     */
    private class Timer extends Thread {
        Thread shouldBeInterrupt = null;
        boolean stop = true;

        public Timer(Thread sbi) {
            shouldBeInterrupt = sbi;
        }

        @Override
        public void run() {
            stop = false;
            try {
                Thread.sleep(MAX_WAIT_TIME);
            } catch (InterruptedException e) {
            }
            if (!stop) {
                LOGGER.debug("interrupt the thread");
                shouldBeInterrupt.interrupt();
            }
        }

        public void stopTimer() {
            stop = true;
        }

    }
}
