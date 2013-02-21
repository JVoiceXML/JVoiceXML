/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jtapi/src/org/jvoicexml/callmanager/jtapi/JtapiConnectionInformation.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-12-23 18:36:01 +0700 (Thu, 23 Dec 2010) $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.mobicents.callmanager;

import java.net.URI;
import java.net.UnknownHostException;

import org.jvoicexml.ConnectionInformation;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;

/**
 * Jtapi based implementation of a {@link ConnectionInformation}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 * @since 0.6
 */
public final class MobicentsConnectionInformation implements ConnectionInformation {
    /** The serial version UID. */
    private static final long serialVersionUID = -2292816741245233245L;

    /** A terminal for a SIP MGCP connection. */
    private final MgcpCallTerminal terminal;

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
     *            the vnxivr terminal.
     * @param outputType
     *            type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType
     *            type of the {@link org.jvoicexml.UserInput} resource
     * @throws UnknownHostException
     *         Error determining the local IP address.
     */
    public MobicentsConnectionInformation(final MgcpCallTerminal term,
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
        return "mobicents";
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
    public MgcpCallTerminal getTerminal() {
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
