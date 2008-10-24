package org.jvoicexml.systemtest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.systemtest.testcase.IRTestCase;

public class TestExecutor {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(TestExecutor.class);

    // AutoTestThread autoTestThread;
    // TestRecorder report;

    AnswerGenerator answerGenerator;

    String reason = "";
    boolean result = false;

    List<Throwable> noExpectEvents = new ArrayList<Throwable>();

    public TestExecutor(AnswerGenerator ag) {
        answerGenerator = ag;
    }

    public void execute(JVoiceXml jvxml, IRTestCase testcase, RemoteClient remoteClient) {

        LOGGER.info("\n\n");
        LOGGER.info("###########################################");
        LOGGER.info("start testcase : " + testcase.toString());
        URI testURI = testcase.getStartURI();
        LOGGER.info("start uri : " + testURI.toString());
        CallThread callThread = new CallThread(jvxml, testURI, remoteClient);
        callThread.start();
        try {

            result = answerGenerator.audioResponse();
            LOGGER.debug("result = " + result);

            if (callThread.started) {
                try {
                    callThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // no exception will be throw.
                }
            }

        } catch (InterruptedException e) {
            LOGGER.debug("responseInterpreterPrompt be interperted ");
            reason = "wait over " + AnswerGenerator.MAX_WAIT_TIME + "ms, interruputed.";
            callThread.session.hangup();
            callThread.interrupt();
            LOGGER.debug("callThread.interrupt(); ");
            noExpectEvents.add(e);
        } catch (Exception e) {
            e.printStackTrace();
            noExpectEvents.add(e);
        }

        LOGGER.info("The test result is : " + result);
        LOGGER.info("testcase " + testcase.getId() + " finished");
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
                LOGGER.debug("session.waitSessionEnd() returned.");
            } catch (Throwable t) {
                LOGGER.error("Throwable catched.", t);
                noExpectEvents.add(t);
            }
            started = false;
        }
    }
}
