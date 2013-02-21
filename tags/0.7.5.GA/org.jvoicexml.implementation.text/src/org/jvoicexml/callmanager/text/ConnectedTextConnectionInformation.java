/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.client.TcpUriFactory;

/**
 * Connection information of a connected text based client.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class ConnectedTextConnectionInformation
    extends BasicConnectionInformation {
    /** The serial version UID. */
    private static final long serialVersionUID = -3748226645427599142L;

    /** Identifier for resources that are retrieved by JVoiceXml. */
    public static final String RESOURCE_IDENTIFIER = "text";

    /** The socket of an established connection to a client. */
    private final transient Socket socket;

    /**
     * Constructs a new object.
     * @param endpoint connection to a text client.
     * @throws URISyntaxException
     *         if the socket address could not be transformed into a URI.
     */
    ConnectedTextConnectionInformation(final Socket endpoint)
        throws URISyntaxException {
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
