/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.callmanager.mmi/src/org/jvoicexml/callmanager/mmi/ConversionException.java $
 * Version: $LastChangedRevision: 4286 $
 * Date:    $Date: 2014-10-06 11:48:00 +0200 (Mon, 06 Oct 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Container that holds call meta data that can be used in
 * {@link org.jvoicexml.ConnectionInformatio}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public final class CallMetadata {
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
     */
    public CallMetadata() {
    }

    /**
     * Retrieves the called device.
     * 
     * @return the called device.
     */
    public URI getCalledDevice() {
        return calledDevice;
    }

    /**
     * Sets the called device.
     * 
     * @param value
     *            the called device
     */
    public void setCalledDevice(final URI value) {
        calledDevice = value;
    }

    /**
     * Sets the called device.
     * 
     * @param value
     *            the called device
     * @throws URISyntaxException
     *             error converting the value into a URI
     */
    public void setCalledDevice(final String value) throws URISyntaxException {
        final URI uri = new URI(value);
        calledDevice = uri;
    }

    /**
     * Retrieves the calling device.
     * 
     * @return the calling device.
     */
    public URI getCallingDevice() {
        return callingDevice;
    }

    /**
     * Sets the calling device.
     * 
     * @param value
     *            the calling device
     */
    public void setCallingDevice(final URI value) {
        callingDevice = value;
    }

    /**
     * Sets the calling device.
     * 
     * @param value
     *            the calling device
     * @throws URISyntaxException
     *             error converting the value into a URI
     */
    public void setCallingDevice(final String value) throws URISyntaxException {
        final URI uri = new URI(value);
        callingDevice = uri;
    }

    /**
     * Retrieves the protocol name.
     * 
     * @return the protocol name
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Sets the protocol name.
     * 
     * @param value
     *            the protocol name
     */
    public void setProtocolName(final String value) {
        protocolName = value;
    }

    /**
     * Retrieves the protocol version.
     * 
     * @return the protocol version
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the protocol version.
     * 
     * @param value
     *            the protocol version
     */
    public void setProtocolVersion(final String value) {
        protocolVersion = value;
    }
}
