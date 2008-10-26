package org.jvoicexml.systemtest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class TestExecutor implements TextListener {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(TestExecutor.class);
    public final static long MAX_WAIT_TIME = 30000L;
    public final static long ANSWER_WAIT_TIME = 5000L;
    public final static long DELAY_ANSWER_TIME = 1000L;

    private final AnswerGenerator answerGenerator;

    private TextServer textServer;

    private CallThread callThread;

    private TestResult result = null;

    private BlockingQueue<Object> jvxmlEvents = new LinkedBlockingQueue<Object>();

    public TestExecutor(AnswerGenerator ag, TextServer server) {
        answerGenerator = ag;
        textServer = server;
    }

    public TestResult execute(JVoiceXml jvxml, IRTestCase testcase, RemoteClient remoteClient) {

        LOGGER.info("\n\n");
        LOGGER.info("###########################################");
        LOGGER.info("start testcase : " + testcase.toString());
        URI testURI = testcase.getStartURI();
        LOGGER.info("start uri : " + testURI.toString());

        callThread = new CallThread(jvxml, testURI, remoteClient);
        callThread.start();
        try {
            processEvent();
        } catch (Throwable e) {
            callThread.session.hangup();
            result = new TestResult(e);
            e.printStackTrace();
        }

        if (callThread.started) {
            LOGGER.debug("callThread status " + callThread.getState());
            try {
                callThread.join(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // no exception will be throw.
            }
        }

        LOGGER.info("The test result is : " + result.toString());
        LOGGER.info("testcase " + testcase.getId() + " finished");
        
        return result;
    }

    private void processEvent() {

        Object event = null;
        while (true) {
            // TestResult result = null;

            Timer timer = new Timer(Thread.currentThread(), ANSWER_WAIT_TIME);
            timer.start();
            LOGGER.error("starting timer.");
            try {
                event = jvxmlEvents.take();
            } catch (InterruptedException e) {

            }
            LOGGER.error("stopping timer.");
            timer.stopTimer();

            if (event instanceof String) {
                String output = (String)event;
                if (isTestFinished(output)) {
                    result = new TestResult(output);
                } else {
                    result = processOutput(output);
                }
            } else if (event instanceof Throwable) {
                result = new TestResult((Throwable) event);
            } else {
                result = new TestResult(false, "this event is not handle : " + event.toString());
            }
            if (result != null) {
                LOGGER.error("result " + result);
                break;
            }
        }
        if (event instanceof InterruptedException && callThread.started) {
            LOGGER.debug("callThread status " + callThread.getState());
            // callThread.session.hangup();
            // waitForMoment();
            // callThread.interrupt();
        }
    }

    private TestResult processOutput(final String output) {
        LOGGER.debug("output : " + output);

        String answer = answerGenerator.getAnswer(output);
        LOGGER.debug("answer : '" + answer + "'");
        if (answer != null) {
            waitForMoment();
            try {
                // callThread.session.getCharacterInput().addCharacter('1');
                LOGGER.error("send : '" + answer + "'");
                textServer.sendInput(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return new TestResult(false, "no suitable answer generate");
        }
    }

    private boolean isTestFinished(final String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            return true;
        }
        if (lowercase.indexOf("fail") >= 0) {
            return true;
        }
        return false;
    }

    void waitForMoment() {
        try {
            Thread.sleep(DELAY_ANSWER_TIME);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void outputSsml(SsmlDocument arg0) {
        LOGGER.debug("Received SsmlDocument : " + arg0.toString());
        try {
            jvxmlEvents.put(arg0.toString());
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void outputText(String arg0) {
        LOGGER.debug("Received Text : " + arg0);
        try {
            jvxmlEvents.put(arg0);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void connected(InetSocketAddress remote) {
        if (remote != null) {
            LOGGER.debug("connected to " + remote.toString());
        }
    }

    @Override
    public void disconnected() {
        LOGGER.debug("disconnected");
    }

    class CallThread extends Thread {

        URI _uri = null;
        JVoiceXml _jvxml = null;
        RemoteClient _client = null;

        Session session = null;

        boolean started = false;

        CallThread(JVoiceXml jvxml, URI uri, RemoteClient client) {
            _client = client;
            _jvxml = jvxml;
            _uri = uri;
        }

        @Override
        public void run() {
            started = true;
            try {
                session = _jvxml.createSession(_client);
            } catch (ErrorEvent e) {
                LOGGER.error("create session error", e);
            }

            try {
                session.call(_uri);
                LOGGER.debug("session.call() returned.");
                session.waitSessionEnd();
                LOGGER.debug("session.waitSessionEnd() returnsessioned.");

            } catch (Throwable t) {
                LOGGER.error("Throwable catched.", t);
                jvxmlEvents.add(t);
            } finally {

                started = false;
            }

        }
    }

    class Timer extends Thread {

        boolean stop = false;

        Thread needToInterrupt;

        long timeout;

        Timer(Thread t, long time) {
            needToInterrupt = t;
            timeout = time;
        }

        @Override
        public void run() {
            LOGGER.debug("timer started.");
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
            }
            if (!stop) {
                jvxmlEvents.add(new InterruptedException("max wait time was reach. cancel current test case"));
            }
        }

        public void stopTimer() {
            LOGGER.debug("timer stopped.");
            stop = true;
        }
    }
}

/**
 * result od ir test
 * 
 * @author lancer
 */
class TestResult {
    private String reason = null;

    private boolean success = false;

    public TestResult(boolean result, String reason) {
        success = result;
        this.reason = reason;
    }

    public TestResult(String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            success = true;
        }
        if (lowercase.indexOf("fail") >= 0) {
            success = false;
        }
        this.reason = output;
    }

    public TestResult(Throwable t) {
        success = false;
        reason = t.getClass().getName() + ":" + t.getMessage();
    }

    public String toString() {
        return "" + success + " : " + reason;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }
}