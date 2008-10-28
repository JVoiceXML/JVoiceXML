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
import org.jvoicexml.systemtest.response.Script;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class TestExecutor implements TextListener {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(TestExecutor.class);
    public final static long MAX_WAIT_TIME = 10000L;
//    public final static long ANSWER_WAIT_TIME = 5000L;
//    public final static long DELAY_ANSWER_TIME = 1000L;

    private final Script script;

    private TextServer textServer;

    public TestResult result = null;

    private Queue<Object> jvxmlEvents = new ConcurrentLinkedQueue<Object>();

    private Boolean isConnected = false;

    private Session session = null;

    public TestExecutor(Script s, TextServer server) {
        script = s;
        textServer = server;
    }

    public TestResult execute(JVoiceXml jvxml, IRTestCase testcase,
            RemoteClient remoteClient) {

        LOGGER.info("\n\n");
        LOGGER.info("###########################################");
        LOGGER.info("start testcase : " + testcase.toString());
        URI testURI = testcase.getStartURI();
        LOGGER.info("start uri : " + testURI.toString());

        try {
            session = jvxml.createSession(remoteClient);
            session.call(testURI);
            LOGGER.debug("session.call() returned.");

        } catch (Throwable t) {
            LOGGER.error("Throwable catched.", t);
            result = new TestResult(t);
            return result;
        }

        try {
            waitConnected();

            script.perform(this);

            if (result == null) {
                result = new TestResult(
                        "fail : all action be executed, but still not received jvxml assert.");
                session.hangup();
            }

            waitDisconnected();
        } catch (Throwable e) {
            result = new TestResult(e);
        }

        return result;
    }

    void waitConnected() throws TimeoutException, ErrorEvent {
        synchronized (jvxmlEvents) {
            LOGGER.info("jvxmlEvents.isEmpty()" + jvxmlEvents.isEmpty());
            if (jvxmlEvents.isEmpty()) {
                try {
                    jvxmlEvents.wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                }
            }
            LOGGER.info("jvxmlEvents.isEmpty()" + jvxmlEvents.isEmpty());
            if (jvxmlEvents.isEmpty()) {
                ErrorEvent t = session.getLastError();
                if (t != null) {
                    throw t;
                } else {
                    throw new TimeoutException("Never connect in "
                            + MAX_WAIT_TIME);
                }
            }
            isConnected = true;
        }
        Object event = jvxmlEvents.peek();
        if (event instanceof InetSocketAddress) {
            jvxmlEvents.poll();
        }

    }

    void waitDisconnected() throws TimeoutException {
        synchronized (isConnected) {
            LOGGER.info("isConnected" + isConnected);
            if (isConnected) {
                try {
                    isConnected.wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                }
            }
            LOGGER.info("isConnected" + isConnected);
            if (isConnected) {
                throw new TimeoutException("Never disconnect in "
                        + MAX_WAIT_TIME);
            }
        }
    }

//    void waitForMoment() {
//        try {
//            Thread.sleep(DELAY_ANSWER_TIME);
//        } catch (InterruptedException e) {
//        }
//    }

    public void answer(String speak) {
        try {
            // callThread.session.getCharacterInput().addCharacter('1');
            LOGGER.error("send : '" + speak + "'");
            textServer.sendInput(speak);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNewEvent() {
        return !jvxmlEvents.isEmpty();
    }

    public Object getNextEvent() {
        return jvxmlEvents.poll();
    }

    @Override
    public void outputSsml(SsmlDocument arg0) {
        LOGGER.debug("Received SsmlDocument : " + arg0.toString());

        jvxmlEvents.offer(arg0.toString());

    }

    @Override
    public void outputText(String arg0) {
        LOGGER.debug("Received Text : " + arg0);

        jvxmlEvents.offer(arg0);
    }

    @Override
    public void connected(InetSocketAddress remote) {
        if (remote != null) {
            LOGGER.debug("connected to " + remote.toString());
        }
        synchronized (jvxmlEvents) {
            jvxmlEvents.offer(remote);
            jvxmlEvents.notifyAll();
        }
    }

    @Override
    public void disconnected() {
        LOGGER.debug("disconnected");
        synchronized (jvxmlEvents) {
            isConnected = false;
            jvxmlEvents.notifyAll();
        }
    }

}
