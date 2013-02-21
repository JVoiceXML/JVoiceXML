/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;

/**
 * The server thread that is listening for clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SocketETLServer extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(SocketETLServer.class);

    /** The port number to listen on. */
    private final int port;

    /** The socket protocol adapter. */
    private final SocketETLProtocolAdapter adapter;

    /** the server socket listening for events. */
    private ServerSocket server;

    /** <code>true</code> if the server should stop. */
    private boolean stopRequest;

    /** The URI of this server. */
    private URI uri;

    /**
     * Constructs a new object.
     * @param protocolAdapter the protocol adapter
     * @param portNumber the port number to listen on
     */
    public SocketETLServer(final SocketETLProtocolAdapter protocolAdapter,
            final int portNumber) {
        adapter = protocolAdapter;
        port = portNumber;
        setDaemon(true);
    }

    /**
     * Retrieves the URI of the socket server.
     * @return the URI of the socket server.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            server = new ServerSocket(port, -1, localHost);
            server.setReuseAddress(true);
            uri = TcpUriFactory.createUri(
                    (InetSocketAddress) server.getLocalSocketAddress());
            LOGGER.info("listening on '" + uri + "' for MMI events");
            while (!stopRequest) {
                final Socket socket = server.accept();
                final InetSocketAddress address =
                        (InetSocketAddress) socket.getRemoteSocketAddress();
                final URI remoteUri = TcpUriFactory.createUri(address);
                LOGGER.info("connection from " + remoteUri);
                final SocketETLClient client =
                        new SocketETLClient(adapter, socket);
                client.start();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
