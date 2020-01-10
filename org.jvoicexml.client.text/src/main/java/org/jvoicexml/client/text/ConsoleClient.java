/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.URI;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Console client for JVoiceXML.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class ConsoleClient implements TextListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ConsoleClient.class);;

    /** Server start lock. */
    private final Object lock;

    /** The text server. */
    private TextServer server;

    /**
     * Constructs a new object.
     */
    private ConsoleClient() {
        lock = new Object();
    }

    /**
     * Running thread.
     * 
     * @param uri
     *            the URI to call
     * @exception Exception
     *                error communicating with JVoiceXML
     * @exception JVoiceXMLEvent
     *                error running the application
     */
    private void run(final URI uri) throws Exception, JVoiceXMLEvent {
        final Context context = new InitialContext();
        final JVoiceXml jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        server = new TextServer(4242);
        server.addTextListener(this);
        server.start();
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("server started");
        final ConnectionInformation info = server.getConnectionInformation();
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(info, id);
        LOGGER.info("calling application at '" + uri + "'...");
        session.call(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
        synchronized (lock) {
            lock.notifyAll();
        }
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
        System.out.println("System: " + speak.getTextContent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput(final TextMessageEvent event) {
        try {
            final String line = readLine();
            server.sendInput(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inputClosed(final TextMessageEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected(final TextMessageEvent event) {
        LOGGER.info("system hung up");
        System.exit(0);
    }

    /**
     * Read an input from the command line.
     * 
     * @return DTMF from the command line.
     * @exception IOException
     *                error reading the next line
     */
    public String readLine() throws IOException {
        System.out.print("User: ");
        System.out.flush();

        final Reader reader = new InputStreamReader(System.in);
        final BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

    /**
     * Start routine.
     * 
     * @param args
     *            URI of the application to call
     */
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("usage:");
            System.err.println("\tjava "
                    + ConsoleClient.class.getCanonicalName() + " <uri>");
            System.exit(-1);
        }
        try {
            final URI uri = new URI(args[0]);
            final ConsoleClient console = new ConsoleClient();
            console.run(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
