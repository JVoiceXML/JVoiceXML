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
	Conversation log;
	boolean connected;
	boolean started;
	Statement focus;
	
	public static long SERVER_WAIT = 5000;

	public Supervisor() {
		jvxml = null;
		server = null;
		log = null;
		connected = false;
		started = false;
		focus = null;
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
		log = new Conversation();
		server.start();
		
		/* wait for the server */
		synchronized (log) {
			try {
				log.wait(SERVER_WAIT);
			}
			catch (InterruptedException e) {

			}
		}

		return log;
	}
	
	public void process(File file) {
		Assert.assertNotNull("JVoiceXML",jvxml);
		Assert.assertNotNull("Server",server);
        try {
			final URI dialog = file.toURI();
			final Session session = jvxml.createSession(server.getConnectionInformation());
			session.call(dialog);
			session.waitSessionEnd();
			session.hangup();
		} catch (ErrorEvent | UnknownHostException e) {
			e.printStackTrace();
			Assert.fail("Session");
		}		
	}
	
	public void assertActivity() {
		Assert.assertTrue("Started",started);
		Assert.assertTrue("Connected",connected);
		if (focus == null) {
			focus = log.begin();
		}
	}

	public void assertOutput(String message) {
		Assert.assertTrue("Output: "+message,focus instanceof Output);
		focus.receive(message);
		log.next();
	}
	
	public void assertInput() {
		Assert.assertTrue("Input",focus instanceof Input);
		focus.send(server);
		log.next();
	}
	
	public void started() {
		started = true;
		
		synchronized (log) {
			log.notifyAll();
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
		Assert.assertNull("Disconnected", focus);
	}
}