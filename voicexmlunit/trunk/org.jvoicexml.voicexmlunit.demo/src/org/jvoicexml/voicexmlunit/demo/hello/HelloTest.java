package org.jvoicexml.voicexmlunit.demo.hello;


import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;


public class HelloTest extends TestCase {

	private Supervisor supervisor;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("java.security.policy","config/jvoicexml.policy");
				
		supervisor = new Supervisor();
		supervisor.lookupVoice(new File("config/jndi.properties"));
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		final TextServer server = new TextServer(4242);
		final Conversation conversation = supervisor.init(server);

		server.start();
		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		supervisor.process(new File("rc/helloworld.vxml"));
	}

}
