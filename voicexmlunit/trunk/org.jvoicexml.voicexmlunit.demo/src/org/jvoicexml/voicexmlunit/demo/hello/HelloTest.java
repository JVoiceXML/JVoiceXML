package org.jvoicexml.voicexmlunit.demo.hello;


import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.JVoiceXml;

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

	@Test
	public void test() {
		final Runner runner = new Runner("rc/helloworld.vxml",supervisor);
		final Conversation conversation = supervisor.init(runner.getServer(),jvxml);

		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		supervisor.SERVER_WAIT = 10000;
		runner.run();
	}
}
