/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mrcpv2;

import org.jvoicexml.callmanager.CallParameters;
import org.speechforge.cairo.client.SpeechClient;

/**
 * Call paramters for a SIP client.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class SipCallParameters extends CallParameters {
    /** IP address of the client. */
    private String clientAddress;

    /** IP address of the MRCP server. */
    private String serverAddress;

    /** Port for RTP output (The Client Port/Synthesizers Port).*/
    private int clientPort;

    /** Port for RTP input (The Server port/recognizers port).*/
    private int serverPort;
    
    SpeechClient speechClient;

    /**
     * @return the speechClient
     */
    public SpeechClient getSpeechClient() {
        return speechClient;
    }

    /**
     * @param speechClient the speechClient to set
     */
    public void setSpeechClient(SpeechClient speechClient) {
        this.speechClient = speechClient;
    }

    /**
     * Retrieves the IP address.
     * @return the address
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * Retrieves the IP address.
     * @return the address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Retrieves the IP port number.
     * @return the port
     */
    public int getClientPort() {
        return clientPort;
    }

    /**
     * Retrieves the IP port number.
     * @return the port
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the client address.
     * @param address the client address to set
     */
    public void setClientAddress(final String address) {
        clientAddress = address;
    }

    /**
     * @param port the clientPort to set
     */
    public void setClientPort(final int port) {
        clientPort = port;
    }

}
