/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.rtp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jvoicexml.client.BasicConnectionInformation;

/**
 * Connection information for RTP streaming.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class RtpConnectionInformation
    extends BasicConnectionInformation
    implements RtpConfiguration {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -1984940475557481155L;

    /** IP address of the client. */
    private final InetAddress address;

    /** Port for RTP output. */
    private final int rtpPort;

    /** Port for RTPC communication. */
    private final int rtpcPort;

    /**
     * Constructs a new object without control streaming.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @param port the port number to use for RTP streaming.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public RtpConnectionInformation(final String call, final String output,
            final String input, final int port) throws UnknownHostException {
        this(call, output, input, port, -1);
    }

    /**
     * Constructs a new object.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @param port the port number to use for RTP streaming.
     * @param controlPort the port number for control information.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public RtpConnectionInformation(final String call, final String output,
            final String input, final int port, final int controlPort)
        throws UnknownHostException {
        super(call, output, input);
        rtpPort = port;
        rtpcPort = controlPort;
        address = InetAddress.getLocalHost();
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
        return rtpPort;
    }

    /**
     * {@inheritDoc}
     */
    public int getControlPort() {
        return rtpcPort;
    }
}
