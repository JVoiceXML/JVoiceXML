package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class ExecuterTest {
    Executor executer;

    InetSocketAddress inet;

    @Before
    public void setUp() {
        executer = new Executor(null, null, null);
        inet = new InetSocketAddress("localhost", 10000);
    }

    @Test
    public void testInitial() {

        Result result = executer.getResult();
        Assert.assertEquals(TestResult.NEUTRAL, result.getAssert());
    }

    @Test
    public void testConnect() {
        executer.started();
        executer.connected(inet);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.NEUTRAL, result.getAssert());
        Assert.assertTrue(TestResult.NEUTRAL == result.getAssert());
    }

    @Test
    public void testPass() throws ParserConfigurationException {
        executer.started();
        executer.connected(inet);
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("pass");
        executer.outputSsml(ssml);
        executer.disconnected();
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.PASS, result.getAssert());
    }

    @Test
    public void testFail() throws ParserConfigurationException {
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("faile");
        executer.outputSsml(ssml);
        executer.disconnected();
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert.assertEquals(Result.DISCONNECT_BEFORE_ASSERT, result.getReason());
    }

    @Test
    public void testTimeout1() {
        executer.started();
        executer.timeout(1);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert.assertEquals(Result.TIMEOUT_WHEN_CONNECT, result.getReason());
    }

    @Test
    public void testTimeout21() {
        executer.started();
        executer.connected(inet);
        executer.timeout(21);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert
                .assertEquals(Result.TIMEOUT_WHEN_WAIT_OUTPUT, result
                        .getReason());
    }

    @Test
    public void testTimeout22() throws Exception {
        executer.started();
        executer.connected(inet);
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("some output");
        executer.outputSsml(ssml);
        executer.timeout(22);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert
                .assertEquals(Result.TIMEOUT_WHEN_WAIT_OUTPUT, result
                        .getReason());
    }

    /**
     * pass but no disconnect.
     * @throws Exception 
     */
    @Test
    public void testTimeout31() throws Exception {
        executer.started();
        executer.connected(inet);
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("pass");
        executer.outputSsml(ssml);
        executer.timeout(31);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.PASS, result.getAssert());
        Assert.assertEquals(Result.TIMEOUT_WHEN_DISCONNECT, result.getReason());
    }

    /**
     * fail but no disconnect.
     * @throws Exception 
     */
    @Test
    public void testTimeout32() throws Exception {
        executer.started();
        executer.connected(inet);
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("faile");
        executer.outputSsml(ssml);
        executer.timeout(32);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert.assertEquals(Result.TIMEOUT_WHEN_WAIT_OUTPUT, result.getReason());
    }

    @Test
    public void testStatusListener() throws Exception {
        int count = 0;
        MyListener listener = new MyListener();
        Assert.assertEquals(count, listener.updateCount);

        executer.addStatusListener(listener);
        Assert.assertEquals(count, listener.updateCount);

        executer.started();
        Assert.assertEquals(++count, listener.updateCount);

        executer.connected(inet);
        Assert.assertEquals(++count, listener.updateCount);

        final SsmlDocument ssml1 = new SsmlDocument();
        final Speak speak1 = ssml1.getSpeak();
        speak1.addText("some message1");
        executer.outputSsml(ssml1);
        Assert.assertEquals(++count, listener.updateCount);

        final SsmlDocument ssml2 = new SsmlDocument();
        final Speak speak2 = ssml2.getSpeak();
        speak2.addText("some message2");
        executer.outputSsml(ssml2);
        Assert.assertEquals(++count, listener.updateCount);

        executer.disconnected();
        Assert.assertEquals(++count, listener.updateCount);

        executer.timeout(10);
        Assert.assertEquals(++count, listener.updateCount);
    }

    class MyListener implements StatusListener {
        int updateCount = 0;

        @Override
        public void update(final ClientConnectionStatus oldStatus,
                final ClientConnectionStatus newStatus) {
            updateCount++;
        }

    }
}
