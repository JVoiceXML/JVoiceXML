package org.jvoicexml.voicexmlunit;


import java.io.File;
import java.net.URI;
import java.net.UnknownHostException;

import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;


public class Simple {
	@Test
	public void testSimple() {
		final String path = "rc/helloworld.vxml";
		
		final TextServer server = new TextServer(4242);
		server.start();
		
		URI dialog = new File(path).toURI();
		try {
			final Session session = jvxml.createSession(server.getConnectionInformation());
			session.call(dialog);
			session.waitSessionEnd();
			session.hangup();			
		} catch (UnknownHostException | ErrorEvent e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			server.stopServer();
		}
	}
}