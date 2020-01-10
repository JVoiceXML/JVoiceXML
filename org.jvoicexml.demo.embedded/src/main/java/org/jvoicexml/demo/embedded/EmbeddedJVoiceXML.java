/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.demo.embedded;

import java.io.File;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Demo to show how JVoiceXML can be launched by other applications.
 * <p>
 * This demo expects to have the JSAPI 1.0 implementation platform configured.
 * </p>
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class EmbeddedJVoiceXML implements JVoiceXmlMainListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(EmbeddedJVoiceXML.class);

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
     * @throws UnknownHostException 
     */
    private synchronized void interpretDocument(final URI uri)
            throws JVoiceXMLEvent, InterruptedException, UnknownHostException {
        final EmbeddedTextConfiguration config =
                new EmbeddedTextConfiguration();
        jvxml = new JVoiceXmlMain(config);
        jvxml.addListener(this);
        jvxml.start();

        wait();

        final TextServer server = new TextServer(4242);
        server.start();
        server.waitStarted();
        final ConnectionInformation client = server.getConnectionInformation();
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

        session.call(uri);
        session.waitSessionEnd();
        session.hangup();
        server.stopServer();
        jvxml.shutdown();
    }

    /**
     * The main method.
     * 
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure(); // log to console
        
        final EmbeddedJVoiceXML demo = new EmbeddedJVoiceXML();

        try {
            File dialog = new File("hello.vxml");
            final URI uri = dialog.toURI();
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent | InterruptedException
                | UnknownHostException e) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlStartupError(final Throwable exception) {
        LOGGER.error("error starting JVoiceML", exception);
        jvxmlStarted(); // cancel
    }
}
