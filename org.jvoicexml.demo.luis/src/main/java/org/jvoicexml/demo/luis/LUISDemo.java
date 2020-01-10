/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2017-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.demo.luis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextMessageEvent;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Demo implementation to demonstrate interaction with the LUIS NLU.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 *
 * <p>
 * This demo uses the text platform. Take care to update the subscription key
 * in <code>config/text-implementation.xml"</code> before running this demo.
 * </p>
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public final class LUISDemo implements TextListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(LUISDemo.class);

    /** The JNDI context. */
    private Context context;

    /** The text server. */
    private TextServer server;

    private final Object lock;
    
    /**
     * Do not create from outside.
     */
    private LUISDemo() {
        lock = new Object();
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);
            context = null;
        }
    }

    /**
     * Starts the text server.
     * @throws InterruptedException
     *         error waiting for the text server to start
     */
    private void startTextServer() throws InterruptedException {
        server = new TextServer(4242);
        server.addTextListener(this);
        server.start();
        server.waitStarted();
    }
    
    /**
     * Call the voicexml interpreter context to process the given xml document.
     *
     * @param uri
     *            uri of the first document to load
     * @exception JVoiceXMLEvent
     *                Error processing the call.
     * @throws InterruptedException 
     * @throws IOException 
     */
    private void interpretDocument(final URI uri) throws JVoiceXMLEvent, InterruptedException, IOException {
        final JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup(JVoiceXml.class.getSimpleName());
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);
            return;
        }
        if (jvxml == null) {
            LOGGER.error("unable to obtain a referece to JVoiceXML");
            return;
        }

        final ConnectionInformation client = server.getConnectionInformation();
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

        session.call(uri);

        synchronized (lock) {
            lock.wait();
        }
        server.sendInput("I want a large pizza with salami");
        synchronized (lock) {
            lock.wait();
        }
        server.sendInput("yes");
        session.waitSessionEnd();
        session.hangup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
    }

    @Override
    public void connected(InetSocketAddress remote) {
        LOGGER.info("disconnected");
    }

    @Override
    public void outputSsml(final TextMessageEvent event, 
            final SsmlDocument document) {
        final Speak speak = document.getSpeak();
        LOGGER.info("System: " + speak.getTextContent());
    }

    @Override
    public void expectingInput(TextMessageEvent event) {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void inputClosed(TextMessageEvent event) {
    }

    @Override
    public void disconnected(TextMessageEvent event) {
        LOGGER.info("disconnected");
    }

    /**
     * The main method.
     *
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting LUIS demo for JVoiceXML...");
        LOGGER.info("(c) 2017-2019 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        try {
            final LUISDemo demo = new LUISDemo();
            demo.startTextServer();
            final URI uri = LUISDemo.class.getResource("/pizza.vxml").toURI();
            LOGGER.info("interpreting document '" + uri + "'...");
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
