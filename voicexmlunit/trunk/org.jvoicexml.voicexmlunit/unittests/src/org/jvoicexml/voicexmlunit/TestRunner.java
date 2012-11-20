package org.jvoicexml.voicexmlunit;


import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class TestRunner implements TextListener {

	private Runner runner;
	private boolean started;
	private boolean disconnected;

	@Before
	public void setUp() throws Exception {
		// create a faked setting, this will not work in production
		runner = new Runner(null,null);
		setStop();
	}

	private void setStop() {
		started = false;
		disconnected = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServer() {
		assertNotNull(runner);
		assertTrue(runner.getServerPort() > 1024);
		assertNotNull(runner.getServer());
	}
	
	@Test
	public void testStartStop() {
		setStop();
		runner.getServer().addTextListener(this);
		runner.run();
		synchronized (runner) {
			try {
				runner.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}
		assertTrue("started",started);
		assertTrue("disconnected",disconnected);
	}

	@Override
	public void started() {
		started = true;
		
		synchronized (runner) {
			runner.notifyAll();
		}
	}

	@Override
	public void connected(InetSocketAddress remote) {
		
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
