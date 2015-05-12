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
import java.net.URISyntaxException;


/**
 * Factory to create an URI representation of a TCP/IP address.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TcpUriFactory {

    /**
     * Do not create instances.
     */
    private TcpUriFactory() {
    }

    /**
     * Retrieves a URI for the given address.
     * @param address the address
     * @return URI representation of the address.
     * @throws URISyntaxException
     *         error creating the URI.
     */
    public static URI createUri(final InetSocketAddress address)
        throws URISyntaxException {
        final String host = address.getHostName();
        final int port = address.getPort();
        return new URI("tcp", null, host, port, null, null, null);
    }

    /**
     * Retrieves a URI for the given address.
     * @param address the address
     * @return URI representation of the address.
     * @throws URISyntaxException
     *         error creating the URI.
     */
    public static URI createUri(final InetAddress address)
        throws URISyntaxException {
        final String host = address.getHostName();
        return new URI("tcp", host, null, null);
    }
}
