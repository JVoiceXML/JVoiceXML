package org.jvoicexml.demo.embedded;

import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.JVoiceXMLEvent;

public final class EmbeddedJVoiceXML implements JVoiceXmlMainListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(EmbeddedJVoiceXML.class);

    /** Reference to JVoiceXML. */
    private JVoiceXmlMain jvxml;

    /**
     * Do not create from outside.
     */
    private EmbeddedJVoiceXML() {
        // Specify the location of the config folder.
        System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
    }

    /**
     * Calls the VoiceXML interpreter context to process the given XML document.
     * 
     * @param uri
     *            URI of the first document to load
     * @exception JVoiceXMLEvent
     *                error processing the call.
     * @throws InterruptedException
     *                error waiting for JVoiceXML
     */
    private synchronized void interpretDocument(final URI uri)
            throws JVoiceXMLEvent, InterruptedException {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
        jvxml = new JVoiceXmlMain(config);
        jvxml.addListener(this);
        jvxml.start();

        wait();

        final ConnectionInformation client = new BasicConnectionInformation(
                "dummy", "jsapi10", "jsapi10");
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
    public static void main(final String[] args) {
        final EmbeddedJVoiceXML demo = new EmbeddedJVoiceXML();

        try {
            File dialog = new File("hello.vxml");
            final URI uri = dialog.toURI();
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        } catch (InterruptedException e) {
            LOGGER.error("error processing the document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void jvxmlStarted() {
        notifyAll();
    }

    @Override
    public void jvxmlTerminated() {
    }
}
