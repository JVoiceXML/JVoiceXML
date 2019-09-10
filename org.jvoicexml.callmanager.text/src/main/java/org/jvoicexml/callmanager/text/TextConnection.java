/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.text;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

/**
 * A connection to a text client.
 * @author Dirk Schnelle-Walka
 * @since 0.7.3
 */
final class TextConnection extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
        LogManager.getLogger(TextServerThread.class);

    /** The socket connection. */
    private Socket socket;

    /** URI that has to be called. */
    private final URI uri;

    /** Reference to JVoiceXML. */
    private final JVoiceXml jvxml;

    /**
     * Constructs a new object.
     * @param client connection to the client
     * @param applicationUri URI that has to be called
     * @param jvoicexml reference to JVoiceXML
     */
    public TextConnection(final Socket client, final URI applicationUri,
            final JVoiceXml jvoicexml) {
        socket = client;
        uri = applicationUri;
        jvxml = jvoicexml;
        setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        Throwable error = null;
        try {
            final ConnectionInformation client =
                new ConnectedTextConnectionInformation(socket);
            final Session session = jvxml.createSession(client, null);
            session.call(uri);
            session.waitSessionEnd();
            socket.close();
            socket = null;
        } catch (ErrorEvent e) {
            error = e;
        } catch (URISyntaxException e) {
            error = e;
        } catch (IOException e) {
            error = e;
        }

        // Close the connection in case of an error.
        if (error != null) {
            LOGGER.error(error.getMessage(), error);
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
