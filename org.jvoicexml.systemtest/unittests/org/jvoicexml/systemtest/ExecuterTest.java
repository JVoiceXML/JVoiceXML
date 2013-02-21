package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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
    public void testPass() {
        executer.started();
        executer.connected(inet);
        executer.outputText("pass");
        executer.disconnected();
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.PASS, result.getAssert());
    }

    @Test
    public void testFail() {
        executer.started();
        executer.connected(inet);
        executer.outputText("fail");
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
    public void testTimeout22() {
        executer.started();
        executer.connected(inet);
        executer.outputText("some output");
        executer.timeout(22);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert
                .assertEquals(Result.TIMEOUT_WHEN_WAIT_OUTPUT, result
                        .getReason());
    }

    /**
     * pass but no disconnect.
     */
    @Test
    public void testTimeout31() {
        executer.started();
        executer.connected(inet);
        executer.outputText("pass");
        executer.timeout(31);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.PASS, result.getAssert());
        Assert.assertEquals(Result.TIMEOUT_WHEN_DISCONNECT, result.getReason());
    }

    /**
     * fail but no disconnect.
     */
    @Test
    public void testTimeout32() {
        executer.started();
        executer.connected(inet);
        executer.outputText("fail");
        executer.timeout(32);
        Result result = executer.getResult();
        Assert.assertEquals(TestResult.FAIL, result.getAssert());
        Assert.assertEquals(Result.TIMEOUT_WHEN_WAIT_OUTPUT, result.getReason());
    }

    @Test
    public void testStatusListener() {
        int count = 0;
        MyListener listener = new MyListener();
        Assert.assertEquals(count, listener.updateCount);

        executer.addStatusListener(listener);
        Assert.assertEquals(count, listener.updateCount);

        executer.started();
        Assert.assertEquals(++count, listener.updateCount);

        executer.connected(inet);
        Assert.assertEquals(++count, listener.updateCount);

        executer.outputText("some message1");
        Assert.assertEquals(++count, listener.updateCount);

        executer.outputText("some message2");
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
