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
	JVoiceXml jvxml;
	TextServer server;
	URI dialog;
	
	/**
	 * Constructor
	 * @param jvxml Connection to JVoiceXML engine
	 * @param server Server to use for event callbacks
	 * @param dialog VoiceXML resource to process (URL)
	 */
	public Call(JVoiceXml jvxml, TextServer server, URI dialog) {
		this.jvxml = jvxml;
		this.server = server;
		this.dialog = dialog;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Session session;
		try {
			session = jvxml.createSession(server.getConnectionInformation());
			session.call(dialog);
			session.waitSessionEnd();
			session.hangup();		
		} catch (UnknownHostException | ErrorEvent e) {
			e.printStackTrace();
			Assert.fail("Session: "+dialog.getPath());
		}
	}
}
