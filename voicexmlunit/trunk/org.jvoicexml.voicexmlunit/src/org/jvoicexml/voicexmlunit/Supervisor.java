package org.jvoicexml.voicexmlunit;


import java.io.File;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;

import org.jvoicexml.voicexmlunit.io.Call;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Statement;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;


/**
 * Supervisor can help you to write unit tests for VoiceXML documents.
 * Use case scenario:
 * 1. Lookup the JVoiceXML engine.
 * 2. Initialize a new conversation with a TextServer instance.
 * 3. Process a given VoiceXML file. 
 * 
 * @author thesis
 *
 */
public final class Supervisor implements TextListener {
	private Call call;
	private Conversation conversation;
	private boolean connected;
	private boolean started;
	private Statement statement;
	
	public long SERVER_WAIT = 5000;
	
	/**
	 * Constructor
	 */
	public Supervisor() {
		call = null;
		conversation = null;
		connected = false;
		started = false;
		statement = null;
	}

	/**
	 * Initialize a new server conversation
	 * @param server Server to use
	 * @param jvxml Engine to use
	 * @return Conversation to be used and initialized by the caller
	 */
	public Conversation init(TextServer server, JVoiceXml jvxml) {
		if (server != null) {
			server.addTextListener(this);
		}
		
		call = new Call(server,jvxml);
		
		conversation = new Conversation();
		return conversation;
	}

	/**
	 * Process a VoiceXML file and generate test log
	 * @param file File to use
	 */
	public void process(String path) {
		Assert.assertNotNull("Call",call);
		
		/* wait for the server */
		synchronized (conversation) {
	        try {
				conversation.wait(SERVER_WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Assert.fail("Started");
			}
		}

		call.dial(path);
		call.run();
	}

	/**
	 * Assert the expected count of conversation statements
	 * @param expectedCount How many statements should we have?
	 */
	public void assertStatements(int expectedCount) {
		Assert.assertEquals(expectedCount,conversation.countStatements());
	}
	
	/**
	 * Assert that a working conversation and the server connection is established
	 */
	public void assertActivity() {
		Assert.assertTrue("Started",started);
		Assert.assertTrue("Connected",connected);
		Assert.assertNotNull("Statement",statement);
	}

	/**
	 * Assert that the current statement is an Output instance with the given message
	 * @param message Message to expect in the call
	 */
	public void assertOutput(String message) {
		Assert.assertTrue("Output: "+message,statement instanceof Output);
		statement.receive(message);
		statement = conversation.next();
	}
	
	/**
	 * Assert that the current statement is an Input instance and the actual message can be send.´
	 */
	public void assertInput() {
		Assert.assertTrue("Input",statement instanceof Input);
		final TextServer server = call.getServer(); 
		if (server != null) {
			statement.send(server);
		}
		statement = conversation.next();
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#started()
	 */
	public void started() {
		started = true;
		
		synchronized (conversation) {
			conversation.notifyAll();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#connected(java.net.InetSocketAddress)
	 */
	public void connected(final InetSocketAddress remote) {
		connected = true;
		started = true; // just in case...
		
		if (statement == null) {
			statement = conversation.begin();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#outputText(java.lang.String)
	 */
	public void outputText(final String text) {
		assertActivity();
		assertOutput(text);
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#outputSsml(org.jvoicexml.xml.ssml.SsmlDocument)
	 */
	public void outputSsml(final SsmlDocument document) {
		String text = null;
		if (document != null) {
			Speak speak = document.getSpeak();
			if (speak != null) {
				text = speak.getTextContent();
			}
		}
		outputText(text);
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#expectingInput()
	 */
	public void expectingInput() {
		assertActivity();
		assertInput();
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#inputClosed()
	 */
	public void inputClosed() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#disconnected()
	 */
	public void disconnected() {
		started = false;
		connected = false;
		Assert.assertNull("Disconnected", statement);
	}
}