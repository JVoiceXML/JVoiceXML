package org.jvoicexml.systemtest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    public final static long MAX_WAIT_TIME = 30000L;
    public final static long ANSWER_WAIT_TIME = 5000L;
    public final static long DELAY_ANSWER_TIME = 1000L;

    private final Script script;

    private TextServer textServer;

    private CallThread callThread;

    public Result result = null;

    private Queue<Object> jvxmlEvents = new ConcurrentLinkedQueue<Object>();

    boolean stopTest = false;
    
    public TestExecutor(Script s, TextServer server) {
        script = s;
        textServer = server;
    }

    public Result execute(JVoiceXml jvxml, IRTestCase testcase, RemoteClient remoteClient) {

        LOGGER.info("\n\n");
        LOGGER.info("###########################################");
        LOGGER.info("start testcase : " + testcase.toString());
        URI testURI = testcase.getStartURI();
        LOGGER.info("start uri : " + testURI.toString());

        callThread = new CallThread(jvxml, testURI, remoteClient);
        callThread.start();
        
        script.perform(this);
        
        if(result == null){
            result = new Result("fail : all action be executed, but still not received jvxml assert.");
            callThread.session.hangup();
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
        if(Thread.State.TERMINATED != callThread.getState()){
            LOGGER.error("callThread has not TERMINATED. stop test application.");
            stopTest = true;
        }

        LOGGER.info("The test result is : " + result.toString());
        LOGGER.info("testcase " + testcase.getId() + " finished");
        
        return result;
    }

    void waitForMoment() {
        try {
            Thread.sleep(DELAY_ANSWER_TIME);
        } catch (InterruptedException e) {
        }
    }

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
                LOGGER.debug("session.waitSessionEnd() return.");

            } catch (Throwable t) {
                LOGGER.error("Throwable catched.", t);
                jvxmlEvents.add(t);
            } finally {

                started = false;
            }

        }
    }


}

/**
 * result od ir test
 * 
 * @author lancer
 */
class Result {
    private String reason = null;

    private boolean success = false;

    public Result(boolean result, String reason) {
        success = result;
        this.reason = reason;
    }

    public Result(String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            success = true;
        }
        if (lowercase.indexOf("fail") >= 0) {
            success = false;
        }
        this.reason = output;
    }

    public Result(Throwable t) {
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