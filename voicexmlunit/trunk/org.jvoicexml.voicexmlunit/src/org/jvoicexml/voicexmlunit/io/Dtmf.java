package org.jvoicexml.voicexmlunit.io;


import java.io.IOException;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

import junit.framework.Assert;


public class Dtmf extends Statement {
	private char dtmf;
	
	public Dtmf(char dtmf) {
		super("DTMF '"+dtmf+"'");
		this.dtmf = dtmf;
	}
	
	public void receive(String actual) {
		Assert.fail("Receive "+getClass().getSimpleName()+": "+actual);
	}
	
	public void send(Recording record) {
		if (record == null) {
			return;
		}
		
		try {
			record.input(dtmf);
		} catch (NoresourceError | ConnectionDisconnectHangupEvent e) {
			e.printStackTrace();
			Assert.fail("Send: "+toString());
		}
	}
}
