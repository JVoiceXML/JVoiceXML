package org.jvoicexml.voicexmlunit;


import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.event.ErrorEvent;

import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Statement;

import org.jvoicexml.xml.ssml.SsmlDocument;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.spi.NamingManager;

import junit.framework.Assert;


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
	JVoiceXml jvxml;
	TextServer server;
	Conversation conversation;
	boolean connected;
	boolean started;
	Statement statement;
	
	public static long SERVER_WAIT = 5000;

	/**
	 * Constructor
	 */
	public Supervisor() {
		jvxml = null;
		server = null;
		conversation = null;
		connected = false;
		started = false;
		statement = null;
	}
	
	/**
	 * Lookup the JVoiceXML engine
	 * @param configuration Configuration file with settings for JNDI
	 */
	public void lookupVoice(File configuration) {
		try {
			final Properties environment = new Properties();
			environment.load(new FileReader(configuration));
			final Context context = NamingManager.getInitialContext(environment);
			jvxml = (JVoiceXml)context.lookup("JVoiceXml");
		} catch (javax.naming.NamingException | IOException ne) {
			ne.printStackTrace();
		}
	}
	
	/**
	 * Initialize a new server conversation
	 * @param server Server to use
	 * @return Conversation to be used and initialized by the caller
	 */
	public Conversation init(TextServer server) {
		this.server = server;
		server.addTextListener(this);
		conversation = new Conversation();
		//server.start();
		return conversation;
	}

	
	/**
	 * Process a VoiceXML file and generate test log
	 * @param file File to use
	 */
	public void process(File file) {
		Assert.assertNotNull("JVoiceXML",jvxml);
		Assert.assertNotNull("Server",server);
		/* wait for the server */
		synchronized (conversation) {
	        try {
	        	conversation.wait(SERVER_WAIT);
	    		Assert.assertTrue("Started",started);
	    		doCall(file);
			} catch (ErrorEvent | UnknownHostException | InterruptedException e) {
				e.printStackTrace();
				Assert.fail("Session");
			}
		}
	}

	/**
	 * Helper method for process, does the real call via a temporary session
	 * Don't call this directly! Better use the process() methods instead.
	 * @param file
	 * @throws ErrorEvent
	 * @throws UnknownHostException
	 */
	private void doCall(File file) throws ErrorEvent, UnknownHostException {
		final URI dialog = file.toURI();
		final Session session = jvxml.createSession(server.getConnectionInformation());
		session.call(dialog);
		session.waitSessionEnd();
		session.hangup();
	}
	
	/**
	 * Assert that a working conversation and the server connection is established
	 */
	public void assertActivity() {
		if (statement == null) {
			statement = conversation.begin();
		}
		Assert.assertTrue("Connected",connected);
	}

	/**
	 * Assert that the current statement is an Output instance with the given message
	 * @param message Message to expect in the call
	 */
	public void assertOutput(String message) {
		Assert.assertTrue("Output: "+message,statement instanceof Output);
		statement.receive(message);
		conversation.next();
	}
	
	/**
	 * Assert that the current statement is an Input instance and the actual message can be send.´
	 */
	public void assertInput() {
		Assert.assertTrue("Input",statement instanceof Input);
		statement.send(server);
		conversation.next();
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