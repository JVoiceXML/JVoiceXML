/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link TcpUriFactory}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TcpUriFactoryTest {

    /**
     * Test method for {@link org.jvoicexml.client.TcpUriFactory#createUri(java.net.InetSocketAddress)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateUri() throws Exception {
        final InetAddress localhost = InetAddress.getByName("127.0.0.1");
        final InetSocketAddress address =
            new InetSocketAddress(localhost, 4242);
        final URI uri = TcpUriFactory.createUri(address);
        Assert.assertEquals("tcp://localhost:4242", uri.toString());
    }

}
