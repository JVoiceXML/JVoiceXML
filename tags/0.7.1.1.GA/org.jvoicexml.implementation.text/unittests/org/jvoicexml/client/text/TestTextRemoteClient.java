/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link TextRemoteClient}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public final class TestTextRemoteClient {
    /** Client port number. */
    private static final int CLIENT_PORT = 4242;

    /**
     * Test method for {@link org.jvoicexml.client.text.TextRemoteClient#getCallControl()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetCallControl() throws Exception {
        final TextRemoteClient client = new TextRemoteClient(CLIENT_PORT);
        Assert.assertEquals(TextRemoteClient.RESOURCE_IDENTIFIER,
                client.getCallControl());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextRemoteClient#getSystemOutput()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetSystemOutput() throws Exception {
        final TextRemoteClient client = new TextRemoteClient(CLIENT_PORT);
        Assert.assertEquals(TextRemoteClient.RESOURCE_IDENTIFIER,
                client.getSystemOutput());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextRemoteClient#getUserInput()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetUserInput() throws Exception {
        final TextRemoteClient client = new TextRemoteClient(CLIENT_PORT);
        Assert.assertEquals(TextRemoteClient.RESOURCE_IDENTIFIER,
                client.getUserInput());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextRemoteClient#getAddress()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetAddress() throws Exception {
        final TextRemoteClient client = new TextRemoteClient(CLIENT_PORT);
        Assert.assertEquals(InetAddress.getLocalHost(),
                client.getAddress());
    }

    /**
     * Test method for {@link org.jvoicexml.client.text.TextRemoteClient#getPort()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetPort() throws Exception {
        final TextRemoteClient client = new TextRemoteClient(CLIENT_PORT);
        Assert.assertEquals(CLIENT_PORT, client.getPort());
    }
}
