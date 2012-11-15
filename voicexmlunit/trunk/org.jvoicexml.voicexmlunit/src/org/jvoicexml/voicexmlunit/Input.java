package org.jvoicexml.voicexmlunit;

import java.io.IOException;

import org.jvoicexml.client.text.TextServer;

import junit.framework.Assert;

public class Input extends Statement {
	public Input(String message) {
		super(message);
	}
	
	public void receive(String actual) {
		Assert.fail("Tried to receive Input: "+actual);
	}
	
	public void send(TextServer server) {
		String expect = toString();
		try {
			server.sendInput(expect);
		} catch (IOException e) {
			Assert.fail("Tried to send Input: "+expect);
		}
	}
}
