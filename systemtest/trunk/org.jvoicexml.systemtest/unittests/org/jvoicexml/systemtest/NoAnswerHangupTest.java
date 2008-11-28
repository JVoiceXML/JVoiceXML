package org.jvoicexml.systemtest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;

/**
 * session hangup() test.
 * 
 * @author lancer
 * 
 */
public class NoAnswerHangupTest {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(NoAnswerHangupTest.class);

    /** MAX_TIMEOUT. */
    private static final long MAX_TIMEOUT = 5000;

    /** TextServer. */
    private TextServer textServer = new TextServer(5900);

    @Before
    public void setUp() {
        textServer.start();
    }

    @After
    public void tearDown() {
        textServer.stopServer();
    }

    @Test
    public void test() throws IOException {
        JVoiceXml interpreter = findInterpreter();
        Assert.assertNotNull(interpreter);

        URL testFile = NoAnswerHangupTest.class.getResource("ir7.xml");

        CallThread callThread = null;
        try {
            callThread = new CallThread(interpreter, testFile.toURI(),
                    textServer.getRemoteClient());
        } catch (Exception e) {
            Assert.fail();
        }
        callThread.start();

        waitForMoment(MAX_TIMEOUT);

        /* focus here */
        // {
        // textServer.sendInput("chicago");
        // waitForMoment(MAX_TIMEOUT);
        // }
        LOGGER.error("callThread.status : " + callThread.getState());
        callThread.session.hangup();
        waitForMoment(MAX_TIMEOUT);

        LOGGER.error("callThread.status : " + callThread.getState());

        if (callThread.started) {
            callThread.interrupt();
            waitForMoment(MAX_TIMEOUT);
        }

        LOGGER.error("callThread.status : " + callThread.getState());
        Assert.assertEquals(Thread.State.TERMINATED, callThread.getState());
        // Assert.assertFalse(callThread.started);
    }

    private void waitForMoment(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    private static JVoiceXml findInterpreter() {
        JVoiceXml jvxml = null;
        Context context;
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);
            context = null;
        }

        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);
        }
        return jvxml;
    }

    /**
     * Monitor of session end.
     * 
     * @author lancer
     * 
     */
    private class CallThread extends Thread {

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
            } finally {

                started = false;
            }

        }
    }
}
