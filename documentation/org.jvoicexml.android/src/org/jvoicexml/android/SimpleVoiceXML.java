package org.jvoicexml.android;

//import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
//import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.JVoiceXMLEvent;

public final class SimpleVoiceXML implements JVoiceXmlMainListener {

	private JVoiceXmlMain jvxml;
	
	 /** Logger for this class. */
   // public static final Logger LOGGER = Logger.getLogger(SimpleVoiceXML.class);

	/**
	 * Do not create from outside.
	 */
	SimpleVoiceXML() {
        System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
		// Only needed for JSAPI 2 implementation platform
        System.setProperty("javax.speech.supports.audio.management",
                Boolean.TRUE.toString());
        System.setProperty("javax.speech.supports.audio.capture",
                Boolean.TRUE.toString());
	}

	/**
	 * Calls the VoiceXML interpreter context to process the given XML document.
	 * 
	 * @param uri
	 *            URI of the first document to load
	 * @exception JVoiceXMLEvent
	 *                Error processing the call.
	 * @throws InterruptedException 
	 */
	synchronized void interpretDocument(final URI uri) throws JVoiceXMLEvent, InterruptedException {
		DummyConfiguration config = new DummyConfiguration();
		jvxml = new JVoiceXmlMain(config);
		jvxml.addListener(this);
		jvxml.start();
		
		this.wait();
		
		final ConnectionInformation client = new BasicConnectionInformation("dummy", "jsapi20", "jsapi20");
		final Session session = jvxml.createSession(client);

		session.call(uri);
		session.waitSessionEnd();
		session.hangup();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Command line arguments. None expected.
	 */
//	public static void main(final String[] args) {
//		final SimpleVoiceXML demo = new SimpleVoiceXML();
//
//		URI uri= null;
//		try {
//			File dialog = new File("hello.vxml");
//			uri = dialog.toURI();
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//			return;
//		}
//		try {
//			demo.interpretDocument(uri);
//		} catch (org.jvoicexml.event.JVoiceXMLEvent e) {
//			LOGGER.error("error processing the document", e);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public synchronized void jvxmlStarted() {
		this.notifyAll();
	}

	@Override
	public void jvxmlTerminated() {
	}
}
