package org.jvoicexml.voicexmlunit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.processor.Assert;

public class TestConversation {

	private Supervisor supervisor;


	@Before
	public void setUp() throws Exception {
		supervisor = new Supervisor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConversationAdd() {
		// 1. one Output
		Conversation conversation = createConversationForSimpleTest();
		conversation.addOutput("Test1");
		Assert.assertStatements(1, conversation);
		
		// 2. one Output again
		conversation = createConversationForSimpleTest();
		conversation.addOutput("Test2");
		Assert.assertStatements(1, conversation);
		
		// 3. both Output and Input
		conversation.addInput("Test3");
		Assert.assertStatements(2, conversation);

		// 4. conversation gets empty again
		conversation = createConversationForSimpleTest();
		//Assert.assertStatements(0, conversation);
	}
	
	@Test
	public void testConversationNext() {
		Conversation conversation = createConversationForSimpleTest();
		conversation.addOutput("begin");		
		conversation.addOutput("next");
		
		Assert.assertEquals(conversation.next().toString(),"begin"); // tricky, eh?
		Assert.assertEquals(conversation.begin().toString(),"begin");
		Assert.assertEquals(conversation.next().toString(),"next");
		Assert.assertNull(conversation.next());
	}

	
	private Conversation createConversationForSimpleTest() {
		Conversation conversation = supervisor.init(null);
		Assert.assertStatements(0, conversation);
		return conversation;
	}
}
