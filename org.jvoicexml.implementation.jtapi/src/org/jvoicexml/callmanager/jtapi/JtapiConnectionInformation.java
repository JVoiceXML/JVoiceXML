/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.net.UnknownHostException;

import org.jvoicexml.ConnectionInformation;

/**
 * Jtapi based implementation of a {@link ConnectionInformation}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
@SuppressWarnings("serial")
public final class JtapiConnectionInformation implements ConnectionInformation {
    /** A terminal for a JTapi connection. */
    private final JVoiceXmlTerminal terminal;

    /** Type of the {@link org.jvoicexml.SystemOutput} resource. */
    private final String output;

    /** Type of the {@link org.jvoicexml.UserInput} resource. */
    private final String input;

    /** URI of the local interpreter context device. */
    private URI calledDevice;

    /** URI of the remote caller device. */
    private URI callingDevice;

    /** Name of the connection protocol. */
    private String protocolName;

    /** Version of the connection protocol. */
    private String protocolVersion;

    /**
     * Constructs a new object.
     *
     * @param term
     *            the JTAPI terminal.
     * @param outputType
     *            type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType
     *            type of the {@link org.jvoicexml.UserInput} resource
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public JtapiConnectionInformation(final JVoiceXmlTerminal term,
            final String outputType, final String inputType)
        throws UnknownHostException {
        terminal = term;
        output = outputType;
        input = inputType;
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
    @Override
    public URI getCalledDevice() {
        return calledDevice;
    }

    /**
     * Sets the called device.
     * @param device the called device to set
     */
    public void setCalledDevice(final URI device) {
        calledDevice = device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getCallingDevice() {
        return callingDevice;
    }

    /**
     * Sets the calling device.
     * @param device the calling device to set
     */
    public void setCallingDevice(final URI device) {
        callingDevice = device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Sets the protocol name.
     * @param name the protocol name to set
     */
    public void setProtocolName(final String name) {
        protocolName = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the protocol version.
     * @param version the protocol version to set
     */
    public void setProtocolVersion(final String version) {
        protocolVersion = version;
    }
}
