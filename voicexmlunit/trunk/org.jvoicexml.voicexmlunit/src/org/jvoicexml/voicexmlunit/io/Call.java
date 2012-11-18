/**
 * 
 */
package org.jvoicexml.voicexmlunit.io;


import java.net.URI;
import java.net.UnknownHostException;

import org.junit.Assert;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;

import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.event.ErrorEvent;


/**
 * Call simulates a real telephony call.
 * This is done with creation of a new JVoiceXML session
 * and a TextServer that can be used to notice all events.
 * 
 * @author thesis
 *
 */
public final class Call implements Runnable {
	private TextServer server;
	private JVoiceXml jvxml;
	private URI dialog;
	
	/**
	 * Constructor
	 * @param server Server to use for event callbacks
	 * @param jvxml Connection to JVoiceXML engine
	 */
	public Call(TextServer server, JVoiceXml jvxml) {
		this.server = server;
		this.jvxml = jvxml;
		this.dialog = null;
	}
	
	/**
	 * Start a new dialog
	 * @param dialog the dialog resource
	 */
	public void dial(URI dialog) {
		this.dialog = dialog;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Assert.assertNotNull("Server",server);
		Assert.assertNotNull("JVoiceXML",jvxml);
		Assert.assertNotNull("Dialog",dialog);
		
		try {
			doSession();		
		} catch (UnknownHostException | ErrorEvent e) {
			e.printStackTrace();
			Assert.fail("Session: "+dialog.getPath());
		}
	}

	/**
	 * @throws ErrorEvent
	 * @throws UnknownHostException
	 */
	private void doSession() throws ErrorEvent, UnknownHostException {
		final Session session = jvxml.createSession(server.getConnectionInformation());
		session.call(dialog);
		session.waitSessionEnd();
		session.hangup();
	}

	public TextServer getServer() {
		return server;
	}
}
