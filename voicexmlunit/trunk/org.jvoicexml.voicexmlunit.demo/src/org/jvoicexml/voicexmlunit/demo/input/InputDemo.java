package org.jvoicexml.voicexmlunit.demo.input;


import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;

public class InputDemo {
	private Call call;
	private Supervisor supervisor;

	@Before
	public void setUp() throws Exception {
		call = new Call("rc/input.vxml");
		
		Voice voice = call.getVoice();
		voice.setPolicy("etc/jvoicexml.policy");		
		//voice.loadConfiguration("etc/jndi.properties");

		supervisor = new Supervisor();
	}

	@Test
	public void testInputYes() {
		createConversation("yes");
		supervisor.process();
	}

	@Test
	public void testInputNoFail() {
		createConversation("no");
		boolean failed = false;
		try {
			supervisor.process();
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertEquals(true,failed);
	}

	private void createConversation(String answer) {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Do you like this example?");
		conversation.addInput(answer);
		conversation.addOutput("You like this example.");
	}
}
