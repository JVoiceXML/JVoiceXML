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

package org.jvoicexml.client.text;

import java.net.InetAddress;
import java.net.Socket;

import org.jvoicexml.RemoteClient;

import junit.framework.TestCase;

/**
 * Test cases for the {@link TextServer}.
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
public final class TextServerTest extends TestCase {
    /** Delay to wait until the server state changes. */
    private static final int DELAY = 500;

    /** The port number to use. */
    private static final int SERVER_PORT = 4242;

    /** The text server to test. */
    private TextServer server;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new TextServer(SERVER_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        if (server.isAlive()) {
            server.stopServer();
        }

        super.tearDown();
    }


    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#getRemoteClient()}.
     * @exception Exception
     *            Test failed.
     */
    public void testGetRemoteClient() throws Exception {
        final RemoteClient client = server.getRemoteClient();
        assertNotNull(client);
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    public void testStopServer() throws Exception {
        server.stopServer();
        assertFalse(server.isAlive());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    public void testStopServerStarted() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        assertTrue(server.isAlive());
        server.stopServer();
        Thread.sleep(DELAY);
        assertFalse(server.isAlive());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    public void testStopServerConnected() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        assertTrue(server.isAlive());
        Socket client = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
        Thread.sleep(DELAY);
        server.stopServer();
        Thread.sleep(DELAY);
        assertFalse(server.isAlive());
        client.close();
    }

    /**
     * Test method for {@link java.lang.Thread#start()}.
     * @exception Exception
     *            Test failed.
     */
    public void testStart() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        assertTrue(server.isAlive());
        server.stopServer();
        Thread.sleep(DELAY);
        assertFalse(server.isAlive());
    }
}
