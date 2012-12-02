package org.jvoicexml.voicexmlunit.io;


import java.io.IOException;

import junit.framework.Assert;


public class Input extends Statement {
	public Input(String message) {
		super(message);
	}
	
	public void receive(String actual) {
		Assert.fail("Receive "+getClass().getSimpleName()+": "+actual);
	}
	
	public void send(Recording record) {
		if (record == null) {
			return;
		}
		
		String expect = toString();
		try {
			record.input(expect);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Send "+getClass().getSimpleName()+": "+expect);
		}
	}
}
