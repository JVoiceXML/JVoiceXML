package org.jvoicexml.voicexmlunit.io;


import junit.framework.Assert;


public class Output extends Statement {
	public Output(String message) {
		super(message);
	}
	
	public void receive(String actual) {
		String expect = toString();
		Assert.assertEquals(getClass().getSimpleName(),expect,actual);
	}
	
	public void send(Recording record) {
		Assert.fail("Send "+getClass().getSimpleName()+": "+toString());
	}
}
