package org.jvoicexml.voicexmlunit.io;

import junit.framework.Assert;

import org.jvoicexml.client.text.TextServer;

public class Output extends Statement {
	public Output(String message) {
		super(message);
	}
	
	public void receive(String actual) {
		String expect = toString();
		String assert_message = "Output received: "+actual+", expected: "+expect;
		Assert.assertEquals(assert_message,expect,actual);
	}
	
	public void send(TextServer server) {
		Assert.fail("Tried to send Output: "+toString());
	}
}
