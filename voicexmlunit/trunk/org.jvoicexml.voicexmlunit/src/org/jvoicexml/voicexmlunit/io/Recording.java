/**
 * 
 */
package org.jvoicexml.voicexmlunit.io;


import java.io.IOException;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;


/**
 * Recording transacts and abstracts the model for the Supervisor
 * and the individual Statement it wants to send.
 * 
 * @author thesis
 *
 */
public class Recording {
	private TextServer server;
	private Session session;

	/**
	 * Constructs a Recording
	 * @param server the server to send something
	 */
	public Recording(TextServer server, Session session) {
		super();
		this.server = server;
		this.session = session;
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
	
	public void input(char dtmf) throws NoresourceError, ConnectionDisconnectHangupEvent {
		if (session != null) {
			CharacterInput input = session.getCharacterInput();
	        input.addCharacter(dtmf);
		}
	}
}
