/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
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

package org.jvoicexml.callmanager.jtapi;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.rtp.RtpConfiguration;

/**
 * Jtapi based implementation of a {@link RemoteClient}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
@SuppressWarnings("serial")
public final class JtapiRemoteClient implements RemoteClient, RtpConfiguration {
    /** A terminal for a JTapi connection. */
    private final JVoiceXmlTerminal terminal;

    /** Type of the {@link org.jvoicexml.SystemOutput} resource. */
    private final String output;

    /** Type of the {@link org.jvoicexml.UserInput} resource. */
    private final String input;

    /** IP address of the client. */
    private final InetAddress address;

    /** Port for RTP output. */
    private final int rtpPort;
    
    /** Port for RTPC communication. */
    private final int rtpcPort;

    /**
     * Constructs a new object without a control commnication..
     *
     * @param term
     *            the JTAPI terminal.
     * @param outputType
     *            type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType
     *            type of the {@link org.jvoicexml.UserInput} resource
     * @param rtpPort the port number to use for RTP streaming.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public JtapiRemoteClient(final JVoiceXmlTerminal term,
            final String outputType, final String inputType,
            final int port) throws UnknownHostException {
        this(term, outputType, inputType, port, -1);
    }

    /**
     * Constructs a new object.
     *
     * @param term
     *            the JTAPI terminal.
     * @param outputType
     *            type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType
     *            type of the {@link org.jvoicexml.UserInput} resource
     * @param rtpPort the port number to use for RTP streaming.
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public JtapiRemoteClient(final JVoiceXmlTerminal term,
            final String outputType, final String inputType,
            final int port, final int controlPort) throws UnknownHostException {
        terminal = term;
        output = outputType;
        input = inputType;
        rtpPort = port;
        rtpcPort = controlPort;
        address = InetAddress.getLocalHost();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCallControl() {
        return "jtapi";
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemOutput() {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    public String getUserInput() {
        return input;
    }

    /**
     * Retrieves the terminal name.
     *
     * @return name of the terminal.
     */
    public String getTerminalName() {
        return terminal.getTerminalName();
    }

    /**
     * Retrieves the terminal.
     * @return the terminal.
     */
    public JVoiceXmlTerminal getTerminal() {
        return terminal;
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
