package org.jvoicexml.voicexmlunit;


import java.io.File;

import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.jvoicexml.JVoiceXml;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.voicexmlunit.io.Call;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Statement;

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
	Call call;
	Conversation conversation;
	boolean connected;
	boolean started;
	Statement statement;
	
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
	 * @return Conversation to be used and initialized by the caller
	 */
	public Conversation init(TextServer server, JVoiceXml jvxml) {
		if (server != null) {
			server.addTextListener(this);
			//server.start();
		}
		this.call = new Call(server,jvxml);
		conversation = new Conversation();
		return conversation;
	}
	
	/**
	 * Process a VoiceXML file and generate test log
	 * @param file File to use
	 */
	public void process(File file) {
		/* wait for the server */
		synchronized (conversation) {
	        try {
				/* wait for the server to be ready */
				conversation.wait(SERVER_WAIT);
				call.dial(file.toURI());
				//new Thread(call).start();
				call.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Assert.fail("Call: "+file.getPath());
			}
		}
	}
	
	/**
	 * Process a VoiceXML path and generate test log
	 * @param path Path to the VoiceXML document
	 */
	public void process(String path) {
		process(new File(path));
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
		Assert.assertNotNull("Statement",statement);
		Assert.assertTrue("Connected",connected);
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
		outputText(document.toString());
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