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
	
	private Call call;
	private boolean started;
	private boolean connected;
	private boolean disconnected;
	
	@Before
	public void setUp() throws Exception {
		URI dialog = new File("unittests/rc/mock.vxml").toURI();
		call = new Call(dialog);
		call.setListener(this);

		started = false;
		connected = false;
		disconnected = false;
		
		final Voice voice = call.getVoice();
		voice.loadConfiguration("unittests/etc/jndi.properties");
		voice.setPolicy("unittests/etc/jvoicexml.policy");
	}

	@Test
	public void testDialog() {
		call.run();

		Assert.assertEquals("started",true,started);
		Assert.assertEquals("connected",true,connected);
		Assert.assertEquals("disconnected",true,disconnected);
	}

	@Test
	public void testFailure() {
		AssertionFailedError error = new AssertionFailedError();
		call.fail(error);
		
		Assert.assertNotNull(call.getFailure());
		Assert.assertTrue(disconnected);
	}

	@Test
	public void testSuccess() {
		call.fail(null);
		
		Assert.assertNull(call.getFailure());
		Assert.assertTrue(disconnected);
	}
	
	@Override
	public void started() {
		call.startDialog();
		started = true;
	}

	@Override
	public void connected(InetSocketAddress remote) {
		connected = true;		
	}

	@Override
	public void outputText(String text) {
		
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
