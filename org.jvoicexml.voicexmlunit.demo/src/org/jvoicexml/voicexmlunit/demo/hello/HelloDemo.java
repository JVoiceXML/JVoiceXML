package org.jvoicexml.voicexmlunit.demo.hello;


import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Voice;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Conversation;


public class HelloDemo {

	private Call call;
	private Supervisor supervisor;
	
	@Before
	public void setUp() throws Exception {
		call = new Call("rc/helloworld.vxml");
		
		Voice voice = call.getVoice();
		voice.setPolicy("etc/jvoicexml.policy");		
		//voice.loadConfiguration("etc/jndi.properties");

		supervisor = new Supervisor();
	}
	
	@Test
	public void testSuccess() {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		supervisor.assertStatements(2);
		supervisor.process();
	}
	
	@Test	
	public void testMissingHello() {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Goodbye!");
		
		supervisor.assertStatements(1);
		assertFailure();
	}

	@Test	
	public void testMissingGoodbye() {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Hello World!");

		supervisor.assertStatements(1);
		assertFailure();
	}

	@Test	
	public void testEmpty() {
		supervisor.init(call);
		
		supervisor.connected(null); // enforce processing of an empty list
		
		supervisor.assertStatements(0);
		assertFailure();
	}


	private void assertFailure() {
		boolean failed = false;
		try {
			supervisor.process();
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertEquals(true,failed);
	}
}
