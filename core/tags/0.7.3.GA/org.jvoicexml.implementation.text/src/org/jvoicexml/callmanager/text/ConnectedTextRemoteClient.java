/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.client.BasicRemoteClient;
import org.jvoicexml.client.TcpUriFactory;

/**
 * A remote client transporting an established connection to a text based
 * client.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
@SuppressWarnings("serial")
public final class ConnectedTextRemoteClient extends BasicRemoteClient {
    /** Identifier for resources that are retrieved by JVoiceXml. */
    public static final String RESOURCE_IDENTIFIER = "text";

    /** The socket of an established connection to a client. */
    private final transient Socket socket;

    /**
     * Constructs a new object.
     * @param endpoint connection to a remote client.
     * @throws URISyntaxException
     *         if the socket address could not be transformed into a URI.
     */
    ConnectedTextRemoteClient(final Socket endpoint) throws URISyntaxException {
        super(RESOURCE_IDENTIFIER, RESOURCE_IDENTIFIER, RESOURCE_IDENTIFIER);
        socket = endpoint;
        final InetSocketAddress remote =
            (InetSocketAddress) socket.getRemoteSocketAddress();
        final URI callingId = TcpUriFactory.createUri(remote);
        setCallingDevice(callingId);
        final InetSocketAddress local =
            (InetSocketAddress) socket.getLocalSocketAddress();
        final URI calledId = TcpUriFactory.createUri(local);
        setCalledDevice(calledId);
    }

    /**
     * Retrieves the socket.
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }
}
