package org.jvoicexml.voicexmlunit.io;

import org.jvoicexml.client.text.TextServer;

public abstract class Statement {
	String message;
	
	public Statement(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}

	public abstract void receive(String actual);
	
	public abstract void send(TextServer server);
}
