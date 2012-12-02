/**
 * 
 */
package org.jvoicexml.voicexmlunit.io;


import java.io.IOException;

import org.jvoicexml.client.text.TextServer;


/**
 * Recording transacts and abstracts the model for the Supervisor
 * and the individual Statement it wants to send.
 * 
 * @author thesis
 *
 */
public class Recording {
	private TextServer server;

	/**
	 * Constructs a Recording
	 * @param server the server to send something
	 */
	public Recording(TextServer server) {
		super();
		this.server = server;
	}
	
	/**
	 * Input a text to the underlying server
	 * @param text
	 * @throws IOException
	 */
	public void input(String text) throws IOException {
		if (server != null) {
			server.sendInput(text);
		}
	}
}
