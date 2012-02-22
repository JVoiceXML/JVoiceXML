/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/BasicConnectionInformation.java $
 * Version: $LastChangedRevision: 2553 $
 * Date:    $Date: 2011-01-28 08:33:43 +0100 (Fr, 28 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client;

import java.net.URI;

import org.jvoicexml.ConnectionInformation;

/**
 * Basic connection configuration.
 *
 * <p>
 * This implementation is designed to transfer the minimum of the needed
 * information from the client to the JVoiceXml server. It may be
 * extended by custom implementations to transfer other client settings
 * that is needed by custom implementation platforms.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2553 $
 * @since 0.6
 */
public class BasicConnectionInformation implements ConnectionInformation {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -382549466379933472L;

    /** Unique identifier for the call control. */
    private final String callControl;

    /** Unique identifier for the system output. */
    private final String systemOutput;

    /** Unique identifier for the user input. */
    private final String userInput;

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
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     */
    public BasicConnectionInformation(final String call, final String output,
            final String input) {
        callControl = call;
        systemOutput = output;
        userInput = input;
    }

    /**
     * {@inheritDoc}
     */
    public final String getCallControl() {
        return callControl;
    }

    /**
     * {@inheritDoc}
     */
    public final String getSystemOutput() {
        return systemOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getUserInput() {
        return userInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final URI getCalledDevice() {
        return calledDevice;
    }

    /**
     * Sets the called device.
     * @param device the called device to set
     */
    public final void setCalledDevice(final URI device) {
        calledDevice = device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final URI getCallingDevice() {
        return callingDevice;
    }

    /**
     * Sets the calling device.
     * @param device the calling device to set
     */
    public final void setCallingDevice(final URI device) {
        callingDevice = device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getProtocolName() {
        return protocolName;
    }

    /**
     * Sets the protocol name.
     * @param name the protocol name to set
     */
    public final void setProtocolName(final String name) {
        protocolName = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the protocol version.
     * @param version the protocol version to set
     */
    public final void setProtocolVersion(final String version) {
        protocolVersion = version;
    }

    /**
     * Subclass specific additions to the
     * {@link BasicConnectionInformation#toString()} method. Subclasses are
     * requested to add their member variables in the following form
     * <code>,&lt;value&gt;</code>.
     * @param str {@link StringBuilder} to add to
     * @since 0.7.5
     */
    protected void addToString(final StringBuilder str) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(callControl);
        str.append(',');
        str.append(systemOutput);
        str.append(',');
        str.append(userInput);
        str.append(',');
        str.append(callingDevice);
        str.append(',');
        str.append(calledDevice);
        str.append(',');
        str.append(protocolName);
        str.append(',');
        str.append(protocolVersion);
        addToString(str);
        str.append(']');
        return str.toString();
    }
}
