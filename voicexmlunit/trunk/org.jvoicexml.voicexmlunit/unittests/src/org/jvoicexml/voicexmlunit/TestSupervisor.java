package org.jvoicexml.voicexmlunit;


import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;


public class TestSupervisor {

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
		Conversation conversation = initMock();

		supervisor.assertStatements(0);
		
		conversation.addOutput("ping"); // must have an Output before
		conversation.addInput("pong");
		
		supervisor.assertStatements(2);
	}

	@Test
	public void testOuput(){
		Conversation conversation = initMock();
		
		final String message = "bla";
		conversation.addOutput(message);

		Assert.assertEquals(message,conversation.begin().toString());
		
		simulateCall();

		supervisor.assertOutput(message);
	}
	
	@Test
	public void testInput(){
		Conversation conversation = initMock();
		
		final String message = "blub";
		conversation.addOutput(message); // must have an Output before
		conversation.addInput(message);
		
		Assert.assertEquals(message,conversation.begin().toString());
		Assert.assertEquals(message,conversation.next().toString());
		
		simulateCall();
		
		supervisor.assertOutput(message);
		supervisor.assertInput();
	}
	
	@Test
	public void testDisconnect() {
		Conversation conversation = supervisor.init(null);

		conversation.addOutput("hello");
		
		simulateCall();
		
		boolean failed = false;
		try {
			supervisor.disconnected();
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}
	
	@Test
	public void testInputIsOutput() {
		Conversation conversation = supervisor.init(null);

		conversation.addOutput("input");
		
		simulateCall();
		
		boolean failed = false;
		try {
			supervisor.assertInput();
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}
	
	@Test
	public void testOutputIsinput() {
		Conversation conversation = supervisor.init(null);

		String message = "output";
		conversation.addInput(message);
		
		simulateCall();
		
		boolean failed = false;
		try {
			supervisor.assertOutput(message);
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}
	
	private Conversation initMock() {
		return supervisor.init(null);
	}

	private void simulateCall() {
		// no session data, but begin conversation
		supervisor.connected(null);
	}
}
