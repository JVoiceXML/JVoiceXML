package org.jvoicexml.voicexmlunit.demo.hello;


import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.JVoiceXml;

import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;


public class HelloTest extends TestCase {

	private JVoiceXml jvxml;
	private Supervisor supervisor;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("java.security.policy","config/jvoicexml.policy");

		final File configuration = new File("config/jndi.properties");
		jvxml = Voice.lookup(configuration);
		supervisor = new Supervisor();
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		final TextServer server = new TextServer(4242);
		final Conversation conversation = supervisor.init(server,jvxml);

		server.start();
		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		supervisor.SERVER_WAIT = 10000;
		supervisor.process("rc/helloworld.vxml");
	}
}
