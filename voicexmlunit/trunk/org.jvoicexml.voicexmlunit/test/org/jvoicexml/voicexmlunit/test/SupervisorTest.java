package org.jvoicexml.voicexmlunit.test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;


public class SupervisorTest {

	private Supervisor supervisor;

	@Before
	public void setUp() throws Exception {
		supervisor = new Supervisor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStatements() {
		Conversation conversation = supervisor.init(null);

		supervisor.assertStatements(0);
		
		conversation.addOutput("ping"); // must have an Output before
		conversation.addInput("pong");
		
		supervisor.assertStatements(2);
	}
	
	@Test
	public void testActivity() {
		Conversation conversation = supervisor.init(null);
		
		conversation.addOutput("bla"); // at least, one element required
		
		simulateCall();

		supervisor.assertActivity();
	}

	@Test
	public void testOuput(){
		Conversation conversation = supervisor.init(null);
		
		final String message = "bla";
		conversation.addOutput(message);

		Assert.assertEquals(message,conversation.begin().toString());
		
		simulateCall();
		
		//supervisor.assertActivity();
		supervisor.assertOutput(message);
	}
	
	@Test
	public void testInput(){
		Conversation conversation = supervisor.init(null);
		
		final String message = "blub";
		conversation.addOutput(message); // must have an Output before
		conversation.addInput(message);
		
		Assert.assertEquals(message,conversation.begin().toString());
		Assert.assertEquals(message,conversation.next().toString());
		
		simulateCall();
		
		supervisor.assertOutput(message);
		supervisor.assertInput();
	}

	private void simulateCall() {
		// no session data, but begin conversation
		supervisor.connected(null);
	}
}
