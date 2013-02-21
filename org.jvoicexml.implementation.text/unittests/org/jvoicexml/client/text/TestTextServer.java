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

package org.jvoicexml.client.text;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.TcpUriFactory;

/**
 * Test cases for the {@link TextServer}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestTextServer {
    /** Number of msec to wait before the server state changes. */
    private static final int DELAY = 500;

    /** The port number to use. */
    private static final int SERVER_PORT = 4242;

    /** The text server to test. */
    private TextServer server;

    /**
     * Set up the test environment.
     * @exception Exception
     *            set up failed.
     */
    @Before
    public void setUp() throws Exception {
        server = new TextServer(SERVER_PORT);
    }

    /**
     * Tear down the test environment.
     */
    @After
    public void tearDown() {
        if (server.isAlive()) {
            server.stopServer();
        }
    }


    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#getConnectionInformation()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetConnectionInformation() throws Exception {
        final ConnectionInformation client = server.getConnectionInformation();
        Assert.assertNotNull(client);
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#getConnectionInformation()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetConnectionInformationConnected() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        Assert.assertTrue("server expected to be alive", server.isAlive());
        Socket socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
        server.waitConnected();
        final ConnectionInformation client = server.getConnectionInformation();
        Assert.assertNotNull(client);
        final InetAddress localhost = InetAddress.getLocalHost();
        final InetSocketAddress address =
            new InetSocketAddress(localhost, SERVER_PORT);
        Assert.assertEquals(TcpUriFactory.createUri(address),
                client.getCallingDevice());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testStopServer() throws Exception {
        server.stopServer();
        Assert.assertFalse(server.isAlive());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testStopServerStarted() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        server.stopServer();
        Thread.sleep(DELAY);
        Assert.assertFalse(server.isAlive());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testStopServerConnected() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        Assert.assertTrue("server expected to be alive", server.isAlive());
        Socket client = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
        server.waitConnected();
        server.stopServer();
        Thread.sleep(DELAY);
        Assert.assertFalse("server expected not to be alive", server.isAlive());
        client.close();
    }

    /**
     * Test method for {@link java.lang.Thread#start()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testStart() throws Exception {
        server.start();
        Thread.sleep(DELAY);
        server.stopServer();
        Thread.sleep(DELAY);
        Assert.assertFalse(server.isAlive());
    }
}
