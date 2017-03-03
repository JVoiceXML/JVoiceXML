/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.textdemo;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextMessageEvent;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Demo implementation of the venerable "Hello World" for the text
 * implementation platform.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and the
 * <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the text implementation
 * platform.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 */
public final class TextDemo implements TextListener {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(TextDemo.class);

    /**
     * The main method.
     * 
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'hello world' parallel text demo for "
                + "JVoiceXML...");
        LOGGER.info("(c) 2014-2017 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final int MAX = 20;
        final TextServer[] servers = new TextServer[MAX];
        for (int i = 0; i < MAX; i++) {
            final TextServer server = new TextServer(14242 + i);
            server.addTextListener(new TextDemo());
            server.start();
            servers[i] = server;
        }

        try {
            final Context context = new InitialContext();
            final JVoiceXml jvxml = (JVoiceXml) context.lookup(
                    JVoiceXml.class.getSimpleName());
            final URI dialog =
                    TextDemo.class.getResource("/helloworld.vxml").toURI();
            LOGGER.info("initiating " + MAX + " calls...");
            final Session[] sessions = new Session[MAX];
            for (int i = 0; i < MAX; i++) {
                final ConnectionInformation info = servers[i]
                        .getConnectionInformation();
                final Session session = jvxml.createSession(info);
                sessions[i] = session;
            }
            for (int i = 0; i < MAX; i++) {
                sessions[i].call(dialog);
            }
            LOGGER.info("waiting for the end of all sessions...");
            for (int i = 0; i < MAX; i++) {
                sessions[i].waitSessionEnd();
                sessions[i].hangup();
            }
            LOGGER.info("...done");
        } catch (NamingException | ErrorEvent | UnknownHostException
                | URISyntaxException e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final InetSocketAddress remote) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final TextMessageEvent event,
            final SsmlDocument document) {
        final Speak speak = document.getSpeak();
        LOGGER.info("System said: '" + speak.getTextContent() + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput(final TextMessageEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputClosed(final TextMessageEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected(final TextMessageEvent event) {
    }
}
