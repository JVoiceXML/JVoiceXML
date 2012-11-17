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


public final class Supervisor implements TextListener {
	JVoiceXml jvxml;
	TextServer server;
	Conversation conversation;
	boolean connected;
	boolean started;
	Statement statement;
	
	public static long SERVER_WAIT = 5000;

	public Supervisor() {
		jvxml = null;
		server = null;
		conversation = null;
		connected = false;
		started = false;
		statement = null;
	}
	
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
	
	public Conversation init(TextServer server) {
		this.server = server;
		server.addTextListener(this);
		conversation = new Conversation();
		//server.start();
		return conversation;
	}

	
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

	/* helper method for process, does the real call via a temporary session */
	private void doCall(File file) throws ErrorEvent, UnknownHostException {
		final URI dialog = file.toURI();
		final Session session = jvxml.createSession(server.getConnectionInformation());
		session.call(dialog);
		session.waitSessionEnd();
		session.hangup();
	}
	
	public void assertActivity() {
		if (statement == null) {
			statement = conversation.begin();
		}
		Assert.assertTrue("Connected",connected);
	}

	public void assertOutput(String message) {
		Assert.assertTrue("Output: "+message,statement instanceof Output);
		statement.receive(message);
		conversation.next();
	}
	
	public void assertInput() {
		Assert.assertTrue("Input",statement instanceof Input);
		statement.send(server);
		conversation.next();
	}
	
	public void started() {
		started = true;
		
		synchronized (conversation) {
			conversation.notifyAll();
		}
	}
	
	public void connected(final InetSocketAddress remote) {
		connected = true;
	}
	
	public void outputText(final String text) {
		assertActivity();
		assertOutput(text);
	}
	
	public void outputSsml(final SsmlDocument document) {
		outputText(document.toString());
	}
	
	public void expectingInput() {
		assertActivity();
		assertInput();
	}
	
	public void inputClosed() {
		
	}
	
	public void disconnected() {
		started = false;
		connected = false;
		Assert.assertNull("Disconnected", statement);
	}
}