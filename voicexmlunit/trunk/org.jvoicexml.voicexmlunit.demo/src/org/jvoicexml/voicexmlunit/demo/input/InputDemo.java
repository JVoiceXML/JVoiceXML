package org.jvoicexml.voicexmlunit.demo.input;


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

		supervisor = new Supervisor();	}

	@Test
	public void testInput() {
		Conversation conversation = supervisor.init(call);
		conversation.addOutput("Hello!");
		//conversation.addInput("Goodbye!");

		supervisor.assertStatements(1);
		supervisor.process();
	}

}
