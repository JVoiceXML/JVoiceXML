package org.jvoicexml.voicexmlunit.sessiontest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.voicexmlunit.Voice;
import org.jvoicexml.xml.ssml.SsmlDocument;

public final class InputDemo implements TextListener {

	private static final int PORT = 4243;

	private static final long MAX_WAIT = 1000;

	private Voice voice;

	private TextServer server;

	private String result;

	private URI dialog;

	private String input;

	@Before
    public void setUp() {
    	voice = new Voice();
    	voice.setPolicy("etc/jvoicexml.policy");
    	
    	dialog = new File("rc/input.vxml").toURI();
    	
    	server = new TextServer(PORT);
    	server.addTextListener(this);
    	server.start();
    }
	
	@After
	public void tearDown() {
		server.stopServer();
	}

	@Test(timeout=10000)
    public void testInputYes() throws IOException {
		testInput("yes");
	}

	@Test(timeout=10000)
    public void testInputNo() throws IOException {
		boolean failed = false;
		try {
			testInput("no");
		} catch (AssertionFailedError e) {
			failed  = true;
		} finally {
			Assert.assertTrue(failed);
		}
	}

	private void testInput(final String answer) throws IOException {
		try {
			synchronized (server) {
				server.wait(MAX_WAIT);
			}
			input = answer;
	        interpretDocument(dialog);
			synchronized (server) {
				server.wait(MAX_WAIT);
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		
		final String expected = "You like this example.";
		Assert.assertEquals(expected, result);
	}


    private void interpretDocument(final URI uri) throws IOException {
    	voice.connect(server.getConnectionInformation(), uri);
    }

	@Override
	public void started() {
		synchronized (server) {
			server.notifyAll();
		}
	}

	@Override
	public void connected(InetSocketAddress remote) {
		synchronized (server) {
			server.notifyAll();
		}
	}

	@Override
	public void outputSsml(SsmlDocument document) {
		result = document.getSpeak().getTextContent();
	}

	@Override
	public void expectingInput() {
		try {
			server.sendInput(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void inputClosed() {

	}

	@Override
	public void disconnected() {

	}
}
