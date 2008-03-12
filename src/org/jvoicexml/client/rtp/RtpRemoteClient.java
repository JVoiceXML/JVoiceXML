/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/client/rtp/RtpRemoteClient.java $
 * Version: $LastChangedRevision: 409 $
 * Date:    $Date: 2007-08-16 18:07:44 +0200 (Do, 16 Aug 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.client.rtp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.BasicRemoteClient;

/**
 * Remote client configuration for RTP streaming.
 *
 * @author Dirk Schnelle
 * @version $Revision: 409 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
@SuppressWarnings("serial")
public final class RtpRemoteClient
    extends BasicRemoteClient
    implements RtpConfiguration {

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
    public RtpRemoteClient(final String call, final String output,
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
    public RtpRemoteClient(final String call, final String output,
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
