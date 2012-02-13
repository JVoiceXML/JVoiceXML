/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.client.TcpUriFactory;

/**
 * Server that waits for incoming connections over a socket.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
final class TextServerThread extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(TextServerThread.class);

    /** The port number of the server socket. */
    private final int port;

    /** URI that has to be called. */
    private final URI uri;

    /** Reference to JVoiceXML. */
    private final JVoiceXml jvxml;

    /** <code>true</code> if the server is stopped. */
    private boolean stopped;

    /** Shutdown semaphore. */
    private final Object lock;

    /**
     * Constructs a new object.
     * @param portNumber server port number.
     * @param applicationUri URI that has to be called
     * @param jvoicexml reference to JVoiceXML
     */
    public TextServerThread(final int portNumber, final URI applicationUri,
            final JVoiceXml jvoicexml) {
        port = portNumber;
        uri = applicationUri;
        jvxml = jvoicexml;
        lock = new Object();
        setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket();
            server.setReuseAddress(true);
            final InetAddress localhost = InetAddress.getLocalHost();
            final InetSocketAddress address =
                new InetSocketAddress(localhost, port);
            server.bind(address);
            while ((server != null) && !interrupted() && !stopped) {
                final Socket client = server.accept();
                if (!stopped) {
                    final InetSocketAddress remote =
                            (InetSocketAddress) client.getRemoteSocketAddress();
                    final URI remoteUri = TcpUriFactory.createUri(remote);
                    LOGGER.info("connection from '" + remoteUri + "'");
                    final TextConnection connection = new TextConnection(client,
                            uri, jvxml);
                    connection.start();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Stops the server.
     * @throws IOException
     *         error stopping the server
     * @since 0.7.6
     */
    public void stopServer() throws IOException {
        stopped = true;
        final InetAddress localhost = InetAddress.getLocalHost();
        final InetSocketAddress address =
                new InetSocketAddress(localhost, port);
        final Socket socket = new Socket();
        socket.connect(address);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
