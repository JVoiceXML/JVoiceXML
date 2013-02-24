/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * 
 * @author thesis
 * @version $Revision: $
 * @since 0.7.6
 */
public class TestTextServer implements TextListener {

    private static final int PORT = 4711;
    private static final long MAX_WAIT = 1000;
    
    private TextServer server;
    private TextConnectionInformation info;
    private Socket socket;
    private boolean connected;
    
    /**
     * @throws java.lang.Exception
     * @since 0.7.6
     */
    @Before
    public void setUp() throws Exception {
        server = new TextServer(PORT);
        info = (TextConnectionInformation) server.getConnectionInformation();
        socket = new Socket();
        connected = false;
        
        server.addTextListener(this);
        server.start();
        synchronized (info) {
            info.wait(MAX_WAIT);
        }
        info.connectClient(socket);
        server.waitConnected();
    }

    /**
     * @throws java.lang.Exception
     * @since 0.7.6
     */
    @After
    public void tearDown() throws Exception {
        server.stopServer();
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#getConnectionInformation()}.
     * @throws UnknownHostException 
     */
    @Test
    public void testGetConnectionInformation() {
        Assert.assertEquals(PORT, info.getPort());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#isStarted()}.
     */
    @Test
    public void testIsStarted() {
        Assert.assertTrue(server.isStarted());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#waitConnected()} and
     * {@link org.jvoicexml.client.text.TextServer#connectClient(java.net.Socket)}.
     * @throws IOException 
     */
    @Test
    public void testConnection() throws IOException {
        Assert.assertTrue(connected);
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#sendInput(java.lang.String)}.
     * @throws IOException 
     */
    @Test
    public void testSendInput() throws IOException {
        Assert.assertTrue(server.isStarted());
        String input = "Test123";
        server.sendInput(input);
        // TODO verify that input has been received somewhere
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextServer#stopServer()}.
     */
    @Test
    public void testStopServer() {
        connected(null);
        server.stopServer();
        Assert.assertFalse(server.isStarted());
        //Assert.assertFalse(server.isAlive());
        Assert.assertFalse(connected);
    }

    @Override
    public void started() {
        synchronized (info) {
            info.notifyAll();
        }
    }

    @Override
    public void connected(InetSocketAddress remote) {
        connected = true;        
    }

    @Override
    public void outputSsml(SsmlDocument document) {

    }

    @Override
    public void expectingInput() {

    }

    @Override
    public void inputClosed() {

    }

    @Override
    public void disconnected() {
        connected = false;
    }

}
