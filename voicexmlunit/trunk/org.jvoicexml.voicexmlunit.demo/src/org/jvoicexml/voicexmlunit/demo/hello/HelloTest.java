package org.jvoicexml.voicexmlunit.demo.hello;


import java.io.File;
import java.net.URI;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;

import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;

import org.jvoicexml.voicexmlunit.Runner;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Voice;


public class HelloTest extends TestCase {

	private JVoiceXml jvxml;
	private Supervisor supervisor;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("java.security.policy","etc/jvoicexml.policy");

		final File configuration = new File("etc/jndi.properties");
		jvxml = Voice.lookup(configuration);
		supervisor = new Supervisor();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
/*
	@Test
	public void testSimple() {
		final String path = "rc/helloworld.vxml";
		
		final TextServer server = new TextServer(4242);
		server.start();
		
		URI dialog = new File(path).toURI();
		try {
			final Session session = jvxml.createSession(server.getConnectionInformation());
			session.call(dialog);
			session.waitSessionEnd();
			session.hangup();			
		} catch (UnknownHostException | ErrorEvent e) {
			e.printStackTrace();
			Assert.fail("Session");
		} finally {
			server.stopServer();
		}
	}
*/
	@Test
	public void testSupervisor() {		
		final String path = "rc/helloworld.vxml";
		
		final Runner runner = new Runner(path,supervisor);
		final TextServer server = runner.getServer();
		final Conversation conversation = supervisor.init(server,jvxml);
		
		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		runner.run();
	}
}
