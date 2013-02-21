/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.mrcpv2;

import java.net.URI;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.BasicRemoteClient;
import org.speechforge.cairo.client.SpeechClient;

/**
 * {@link RemoteClient} implementation for mrcpv2 clients.
 *
 * <br><br>
 * This implementation is used to setup the MRCP channels and then to pass the
 * channels to the systemOutput and userInput objects.
 * At some point during or after a call comes in you must set the client side
 * address and RTP port with the setter methods
 * <pre>
 *    setClientPort(thePort)
 *    setClientAddress(theAddress)
 *</pre>   
 * At some point before you use the SystemOutput object you must create the TTS
 * channel.  Be sure to have set the client ports and address before making this
 * call.
 * You could do it at call setup time or just prior to using the systemoutput
 * object.
 * <pre>
 *    createTtsChannel()
 * </pre>  
 * You should terminate your session at some point with the terminate method
 * either at call completion time or when you are down using the systemOutput
 * object.
 * <pre>
 *    terminateTtsChannel()
 * </pre>  
 * At some point before you use the UserInput object you must create the recog
 * channel.  You could do it at call setup time or just prior to using the
 * systemoutput object.
 * <pre> 
 *    createRecogChannel()
 * </pre> 
 * After making this call but before actually doing any recognition requests
 * You will need to get the server address and rtp port and forward that info to
 * the client so it knows were to stream the audio.
 * <pre>
 *    getServerPort(thePort)
 *    getServerAddress(theAddress)
 * </pre>
 * Before using this object you must configure the SessionManager.  This could
 * be done with Spring or programatically with the no arg constructor and setter
 * methods
 * See the code in the static method Startup();
 * <pre>
 *   SessionManager sm;
 *   sm = new SessionManager();
 *   sm.setCairoSipAddress(cairoSipAddress);
 *   sm.setCairoSipHostName(peerAddress);
 *   sm.setCairoSipPort(peerPort);
 *
 *   sm.setPort(myPort);
 *   sm.setMySipAddress(mySipAddress);
 *   sm.setStackName("Test Sip Stack");
 *   sm.setTransport("UDP");
 *   sm.startup();
 * </pre>
 *
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class Mrcpv2RemoteClient extends BasicRemoteClient {
    /** The connection to the MRCPv2 TTS server. */
    private transient SpeechClient ttsClient;

    /** The connection to the MRCPv2 ASR server. */
    private transient SpeechClient asrClient;

    /** IP address of the client. */
    private String clientAddress;

    /** IP address of the MRCP server. */
    private String serverAddress;

    /** Port for RTP output (The Client Port/Synthesizers Port).*/
    private int clientPort;

    /** Port for RTP input (The Server port/recognizers port).*/
    private int serverPort;

    /**
     * Constructs a new object.
     */
    public Mrcpv2RemoteClient() {
        super("dummy", "mrcpv2", "mrcpv2");
    }

    /**
     * Constructs a new object.
     * @param callingDevice URI of the calling device
     * @param calledDevice URI of the called device
     */
    public Mrcpv2RemoteClient(final URI callingDevice, final URI calledDevice) {
        super("dummy", "mrcpv2", "mrcpv2");
        setCalledDevice(callingDevice);
        setCalledDevice(calledDevice);
    }

    /**
     * Retrieves the TTS client.
     * @return the TTS client
     * @since 0.7.3
     */
    public SpeechClient getTtsClient() {
        return ttsClient;
    }

    /**
     * Sets the TTS client.
     * @param client the TTS client to set
     * @since 0.7.3
     */
    public void setTtsClient(final SpeechClient client) {
        ttsClient = client;
    }

    /**
     * Retrieves the ASR client.
     * @return the asrClient
     * @since 0.7.3
     */
    public SpeechClient getAsrClient() {
        return asrClient;
    }

    /**
     * Sets the ASR client.
     * @param client the ASR client to set
     * @since 0.7.3
     */
    public void setAsrClient(final SpeechClient client) {
        asrClient = client;
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
