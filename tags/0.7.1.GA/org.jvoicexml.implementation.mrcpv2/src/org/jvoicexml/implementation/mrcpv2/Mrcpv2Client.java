/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml.implementation.mrcpv2;

import java.io.IOException;

import javax.sdp.SdpException;
import javax.sip.SipException;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.BasicRemoteClient;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.sip.SipSession;

/**
 * {@link RemoteClient} implementation for mrcpv2 clients.
 *
 * <br><br>
 * This implementation is used to setup the rmcp channels and then to pass the channels to the systemOutput and userInput objects.
 * At some point druing or after a call comes in you must set the client side address and rtp port with the setter methods
 * <pre>
 *    setClientPort(thePort)
 *    setClientAddress(theAddress)
 *</pre>   
 * At some point before you use the SystemOutput object you must create the tts channel.  Be sure to have set the client ports and address before making this call.
 * You could do it at call setup time or just prior to using the systemoutput object.
 *<pre>
 *    createTtsChannel()
 * </pre>  
 * You should terminate your session at some point with the terminate method either at call completion time or when you are down using the systemOutput object.
 * <pre>
 *    terminateTtsChannel()
 * </pre>  
 * At some point before you use the UserInput object you must create the recog channel.  You could do it at call setup time or just prior to using the systemoutput object.
 * <pre> 
 *    createRecogChannel()
 * </pre> 
 * After making this call but before actually doing any recognition requests.  You will need to get the server address and rtp port and forwar that info to the client so it knows were to stream the audio.
 * <pre>
 *    getServerPort(thePort)
 *    getServerAddress(theAddress)
 * </pre>
 * Before using this object you must configure the SessionManager.  This could be done with Spring or programtically with the no arg constructor and setter methods
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
 * @version $Revision: $
 * @since 0.7
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
@SuppressWarnings("serial")
public final class Mrcpv2Client {
    
    SessionManager sm;

    /** IP address of the client. */
    private String clientAddress;

    /** IP address of the MRCP server. */
    private String serverAddress;
    
    /** Port for RTP output (The Client Port/Synthesizers Port).*/
    private int clientPort;
    
    /** Port for RTP input (The Server port/recognizers port).*/
    private int serverPort;
    
    private SpeechClient ttsClient;
    
    private SpeechClient recogClient;
    
    //private static SessionManager sm;

    /**
     * Constructs a new object.
     */
    public Mrcpv2Client(SessionManager sm) {
        this.sm = sm;
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
     * @param clientAddress the clientAddress to set
     */
    public void setClientAddress(String clientAddress) {
    	this.clientAddress = clientAddress;
    }

	/**
     * @param clientPort the clientPort to set
     */
    public void setClientPort(int clientPort) {
    	this.clientPort = clientPort;
    }

	/**
     * @return the recogClient
	 * @throws NoMediaControlChannelException 
     */
    public SpeechClient getRecogClient() throws NoMediaControlChannelException {
    	if (recogClient == null)
    		throw new NoMediaControlChannelException();
    	return recogClient;
    }

	/**
     * @return the ttsClient
	 * @throws NoMediaControlChannelException 
     */
    public SpeechClient getTtsClient() throws NoMediaControlChannelException {
    	if (ttsClient == null)
    		throw new NoMediaControlChannelException();
    	return ttsClient;
    }

    /**
     * Creates the tts channel.
     * 
     * @throws SdpException the sdp exception
     * @throws SipException the sip exception
     */
    public void createTtsChannel() throws SdpException, SipException {
    	
    	//create a session
        SipSession session = sm.newSynthChannel(clientPort, clientAddress, "Session Name");
        
        //construct the speech client with this session
         ttsClient = new SpeechClientImpl(session.getTtsChannel(), null);
    }
    
    /**
     * Terminate tts channel.
     * 
     * @throws MrcpInvocationException the mrcp invocation exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    public void terminateTtsChannel() throws MrcpInvocationException, IOException, InterruptedException {
    	ttsClient.shutdown();
    	ttsClient = null;
    }
    
    
    /**
     * Creates the recog channel.
     * 
     * @throws SdpException the sdp exception
     * @throws SipException the sip exception
     */
    public void createRecogChannel() throws SdpException, SipException {
    	
        //set up the mrcp channels
        SipSession session = sm.newRecogChannel(clientPort,clientAddress, "Session Name");
        
        //construct the speech client with this session
         recogClient = new SpeechClientImpl(null, session.getRecogChannel());
        
        serverPort = session.getRemoteRtpPort();
        //TODO:  BUG!  Need to get the rtp address from the sdp messag.  Still using the sip host everywhere!  Not just here!
        //serverAddress = session.getRemoteAddress();

    }
    
    /**
     * Terminate recog channel.
     * 
     * @throws MrcpInvocationException the mrcp invocation exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    public void terminateRecogChannel() throws MrcpInvocationException, IOException, InterruptedException {
    	recogClient.shutdown();
    	recogClient = null;
    }
    
    /**
     * Start session manager.  Call this method once before using any remoteClients.
     * Or perhaps use something like spring to configure the session manager and then 
     * inject a reference to the session manager singleton.
     * 
     * @param cairoSipAddress the cairo sip address
     * @param peerAddress the peer address
     * @param peerPort the peer port
     * @param myPort the my port
     * @param mySipAddress the my sip address
     * @param stackName the stack name
     * @param transport the transport
     * 
     * @throws SipException the sip exception
     */
    /*public static void startSessionManager(String cairoSipAddress,
    		String peerAddress,
    		int peerPort,
    		int myPort,
    		String mySipAddress,
    		String stackName,
    		String transport) throws SipException {        
	
    	sm = new SessionManager();
    	sm.setCairoSipAddress(cairoSipAddress);
    	sm.setCairoSipHostName(peerAddress);
    	sm.setCairoSipPort(peerPort);

    	sm.setPort(myPort);
    	sm.setMySipAddress(mySipAddress);
    	sm.setStackName(stackName);
    	sm.setTransport(transport);
    	sm.startup();
    }*/

    /* Note you could replace the above static method with something like this using Spring.
     * 
     * <bean id="sipService" class="org.speechforge.cairo.client.SessionManager"
    	 		init-method="startup" destroy-method="shutdown">
    	 		<property name="dialogService"><ref bean="dialogService"/></property>
    	 		<property name="mySipAddress">
    	 			<value>sip:cairogate@speechforge.org</value>
    	 		</property>
    	 		<property name="stackName">
    	 			<value>A Sip Stack</value>
    	 		</property>
    	 		<property name="port">
    	 			<value>5090</value>
    	 		</property>
    	 		<property name="transport">
    	 			<value>UDP</value>
    	 		</property>
    	 		<property name="cairoSipAddress">
    	 			<value>sip:cairo@speechforge.org</value>
    	 		</property>
    	 		<property name="cairoSipHostName">
    	 			<value>localhost</value>
    	 		</property>
    	 		<property name="cairoSipPort">
    	 			<value>5050</value>
    	 		</property>
    	 	</bean> */
}
