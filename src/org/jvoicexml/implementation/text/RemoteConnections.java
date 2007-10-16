/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Storage for available text based connections.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class RemoteConnections {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RemoteConnections.class);

    /** The singleton. */
    private static final RemoteConnections REMOTE_CONNECTIONS;

    /** Stored connections. */
    private final Map<TextRemoteClient, Socket> connections;

    static {
        REMOTE_CONNECTIONS = new RemoteConnections();
    }

    /**
     * Do not create.
     */
    private RemoteConnections() {
        connections = new java.util.HashMap<TextRemoteClient, Socket>();
    }


    /**
     * Retrieve the singleton.
     * @return instance.
     */
    public static RemoteConnections getInstance() {
        return REMOTE_CONNECTIONS;
    }

    /**
     * Retrieves a socket connection to the given remote client.
     * @param client the client to connect to.
     * @return created socket connection.
     * @throws IOException
     *         Error connecting to the client.
     */
    public synchronized Socket getSocket(final TextRemoteClient client)
        throws IOException {
        if (client == null) {
            return null;
        }

        Socket socket = connections.get(client);
        if (socket != null) {
            return socket;
        }

        final InetAddress address = client.getAddress();
        final int port = client.getPort();
        socket = new Socket(address, port);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + address + ":" + port + "...");
        }

        connections.put(client, socket);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }
        return socket;
    }

    /**
     * Disconnects from the given remote client. Does nothing if no
     * connection exists.
     * @param client the client to disconnect from
     */
    public synchronized void disconnect(final TextRemoteClient client) {
        final Socket socket = connections.remove(client);
        if (socket == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            final InetAddress address = client.getAddress();
            final int port = client.getPort();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("disconnecting from '" + address + ":" + port
                        + "...");
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.debug("error disconnecting from remote client", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnected");
        }
    }
}
