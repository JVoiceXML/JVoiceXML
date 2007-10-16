/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: schnelle $
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
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.client.text.TextServer;

/**
 *Test cases for {@link RemoteConnections}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestRemoteConnections extends TestCase {
    /** The port number to use. */
    private static final int SERVER_PORT = 4242;

    /** The server socket. */
    private ServerSocket server;

    /** The text server as a factory for the remote client. */
    private TextServer textServer;

    /** The test object. */
    private RemoteConnections connections;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        server = new ServerSocket(SERVER_PORT);
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.accept();
                } catch (IOException e) {
                    return;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();

        textServer = new TextServer(SERVER_PORT);
        connections = RemoteConnections.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        server.close();

        super.tearDown();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#getSocket(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testGetSocket() throws Exception {
        final TextRemoteClient client =
            (TextRemoteClient) textServer.getRemoteClient();

        Socket socket1 = connections.getSocket(client);
        assertNotNull(socket1);
        assertTrue(socket1.isConnected());
        assertFalse(socket1.isClosed());

        Socket socket2 = connections.getSocket(client);
        assertNotNull(socket2);
        assertTrue(socket2.isConnected());
        assertFalse(socket2.isClosed());
        assertTrue(socket1.equals(socket2));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#getSocket(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testGetSocketNullClient() throws Exception {
        Socket socket = connections.getSocket(null);
        assertNull(socket);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#getSocket(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testGetSocketUnconnectedClient() throws Exception {
        textServer = new TextServer(SERVER_PORT + 1);

        final TextRemoteClient client =
            (TextRemoteClient) textServer.getRemoteClient();

        IOException error = null;
        try {
            connections.getSocket(client);
        } catch (IOException e) {
            error = e;
        }
        assertNotNull(error);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#disconnect(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testDisconnect() throws Exception  {
        final TextRemoteClient client =
            (TextRemoteClient) textServer.getRemoteClient();

        Socket socket = connections.getSocket(client);
        assertNotNull(socket);
        assertTrue(socket.isConnected());
        assertFalse(socket.isClosed());

        connections.disconnect(client);
        assertTrue(socket.isConnected());
        assertTrue(socket.isClosed());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#disconnect(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testDisconnectNullClient() throws Exception  {
        connections.disconnect(null);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.RemoteConnections#disconnect(org.jvoicexml.client.text.TextRemoteClient)}.
     * @exception Exception
     *            Test failed.
     */
    public void testDisconnectUnconnectedClient() throws Exception  {
        textServer = new TextServer(SERVER_PORT + 1);

        final TextRemoteClient client =
            (TextRemoteClient) textServer.getRemoteClient();

        connections.disconnect(client);
    }
}
