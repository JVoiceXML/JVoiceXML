package org.jvoicexml.voicexmlunit.demo.input;


import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;

public class DtmfDemo {
	private Call call;
	private Supervisor supervisor;

	@Before
	public void setUp() throws Exception {
		call = new Call("rc/dtmf.vxml");
		
		Voice voice = call.getVoice();
		voice.setPolicy("etc/jvoicexml.policy");		
		//voice.loadConfiguration("etc/jndi.properties");

		supervisor = new Supervisor();
	}

	@Test
	public void testInputYes() {
		createConversation('1');
		supervisor.process();
	}

	@Test
	public void testInputNoFail() {
		createConversation('2');
		boolean failed = false;
		try {
			supervisor.process();
		} catch (AssertionFailedError e) {
			failed = true;
		}
		Assert.assertEquals(true,failed);
	}

	private void createConversation(char answer) {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Do you like this example? Please enter 1 for yes or 2 for no");
		conversation.addDtmf(answer);
		conversation.addOutput("You like this example.");
	}
}
