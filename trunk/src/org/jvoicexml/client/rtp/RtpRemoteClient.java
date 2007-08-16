/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
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

package org.jvoicexml.client.rtp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jvoicexml.RemoteClient;

/**
 * @author DS01191
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
@SuppressWarnings("serial")
public final class RtpRemoteClient implements RemoteClient {
    /** Unique identifier for the call control. */
    private final String callControl;

    /** Unique identifier for the system output. */
    private final String systemOutput;

    /** Unique identifier for the user input. */
    private final String userInput;

    /** IP address of the client. */
    private final InetAddress address;

    /** Port for RTP output. */
    private final int port;

    /**
     * Constructs a new object.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @param rtpPort the port number to use for RTP streaming.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public RtpRemoteClient(final String call, final String output,
            final String input, final int rtpPort) throws UnknownHostException {
        callControl = call;
        systemOutput = output;
        userInput = input;
        port = rtpPort;
        address = InetAddress.getLocalHost();
    }

    /**
     * {@inheritDoc}
     */
    public String getCallControl() {
        return callControl;
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemOutput() {
        return systemOutput;
    }

    /**
     * {@inheritDoc}
     */
    public String getUserInput() {
        return userInput;
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
