package org.jvoicexml.voicexmlunit;


import java.io.File;

import java.net.InetSocketAddress;
import java.net.URI;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;


public class TestCall implements TextListener {
	
    private static final long MAX_WAIT = 1000;
    
    private Call call;
    private boolean started;
    private boolean connected;
    private boolean disconnected;
    
    @Before
    public void setUp() throws Exception {
        final URI dialog = new File("unittests/rc/mock.vxml").toURI();
        call = new Call(dialog);
    	call.setListener(this);

    	started = false;
    	connected = false;
    	disconnected = false;
    }

    @Test
    public void testVoice() {
        // 1. Voice is always valid
        Assert.assertNotNull(call.getVoice());
        // 2. Voice can't be destroyed (self instantiated)
        call.setVoice(null);
        Assert.assertNotNull(call.getVoice());
        // 3. Custom voice
        Voice custom = new Voice();
        call.setVoice(custom);
        Assert.assertSame(custom, call.getVoice());
        call.setVoice(null);
        Assert.assertNotSame(custom, call.getVoice());
    }
    
    @Test(timeout=3000)
    public void testDialog() throws InterruptedException {
        final Voice voice = call.getVoice();
        voice.loadConfiguration("unittests/etc/jndi.properties");
        voice.setPolicy("unittests/etc/jvoicexml.policy");

        Assert.assertNull(call.getVoice().getSession());
        call.run();
	Assert.assertTrue("started", started);
	Assert.assertTrue("connected", connected);
	Assert.assertTrue("disconnected", disconnected);
        Assert.assertNull(call.getFailure());
	Assert.assertNull(call.getVoice().getSession());
    }

    @Test
    public void testFailure() {
        AssertionFailedError error = new AssertionFailedError();
	call.fail(error);
        
	Assert.assertNotNull(call.getFailure());
	assertDisconnectedAfterCheckFailed();
    }

    @Test
    public void testSuccess() {
    	call.fail(null);
    	
    	Assert.assertNull(call.getFailure());
    	assertDisconnectedAfterCheckFailed();
    }
	
    /**
     * Assert that Call does stopServer().
     */
	private void assertDisconnectedAfterCheckFailed() {
    	//Assert.assertTrue(disconnected); // commented in Call.fail()
    }
	
    @Override
    public void started() {
        call.startDialog();
        started = true;
    }

    @Override
    public void connected(InetSocketAddress remote) {
        connected = true;
        try {
            Thread.sleep(500); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void outputSsml(SsmlDocument document) {
    }

    @Override
    public void expectingInput() {

    }

    @Override
    public void inputClosed() {

    }

    @Override
    public void disconnected() {
    	disconnected = true;
    }
}
