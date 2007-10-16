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
import java.net.UnknownHostException;

import org.jvoicexml.RemoteClient;

/**
 * {@link RemoteClient} implementation for text based clients.
 *
 * <p>
 * This implementation is based on TCP/IP communication between the
 * VoiceXML interpreter and the client.
 * </p>
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
@SuppressWarnings("serial")
public final class TextRemoteClient implements RemoteClient {
    /** Identifier for resources that are retrieved by JVoiceXml. */
    public static final String RESOURCE_IDENTIFIER = "text";

    /** IP address of the client. */
    private final InetAddress address;

    /** Port for RTP output. */
    private final int port;

    /**
     * Constructs a new object.
     * @param clientPort the port number to use for the output.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    TextRemoteClient(final int clientPort) throws UnknownHostException {
        port = clientPort;
        address = InetAddress.getLocalHost();
    }

    /**
     * {@inheritDoc}
     */
    public String getCallControl() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemOutput() {
        return RESOURCE_IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    public String getUserInput() {
        return RESOURCE_IDENTIFIER;
    }

    /**
     * {@inheritDoc}
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return port;
    }
}
