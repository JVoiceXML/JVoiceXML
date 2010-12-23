/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import org.jvoicexml.client.BasicConnectionInformation;

/**
 * {@link org.jvoicexml.ConnectionInformation} implementation for text based clients.
 *
 * <p>
 * This implementation is based on TCP/IP communication between the
 * VoiceXML interpreter and the client.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TextConnectionInformation
    extends BasicConnectionInformation {
    /** The serial version UID. */
    private static final long serialVersionUID = -4019684264350156454L;

    /** Default resource type. */
    public static final String TYPE = "text";

    /** Identifier for resources that are retrieved by JVoiceXml. */
    public static final String RESOURCE_IDENTIFIER = "text";

    /** IP address of the client. */
    private final InetAddress address;

    /** The client's port number. */
    private final int port;

    /**
     * Constructs a new object.
     * @param clientPort the port number to use for the output.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    TextConnectionInformation(final int clientPort) throws UnknownHostException {
        super(RESOURCE_IDENTIFIER, RESOURCE_IDENTIFIER, RESOURCE_IDENTIFIER);
        port = clientPort;
        address = InetAddress.getLocalHost();
    }

    /**
     * Retrieves the IP address.
     * @return the address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Retrieves the IP port number.
     * @return the port
     */
    public int getPort() {
        return port;
    }
}
