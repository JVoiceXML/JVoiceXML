/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml.implementation.mrcpv2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.sdp.SdpException;
import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.MrcpEvent;
import org.mrcp4j.message.header.IllegalValueException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;

/**
 * Audio input that uses a mrcpv2 client to use a recognition resource.
 *
 * <p>
 * Handle all MRCPv2 calls to the recognizer to make MRCPv2 transparent to the
 * interpreter.
 * </p>
 *
 * @author Spencer Lord
 * @version $Revision: $
 * @since 0.7
 */
public final class Mrcpv2SpokenInput
        implements SpokenInput, ObservableSpokenInput, SpeechEventListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SpokenInput.class);
    
    /** the port that will receive the stream from mrcp server **/
    private int rtpReceiverPort;
    // TODO: Workaround for JMF.  Even though only sending audio, JMF rtp setup needs a local rtp port too.  Really should not be needed.

    /** the local host address **/
    private String hostAddress;
    
    private String remoteRtpHost;
    private int remoteRtpPort;
    
    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    // private JSGFGrammar _grammar = new JSGFGrammar();

    // TODO: Handle load and activate grammars properly on the server. At
    // present the mrcpv2 server does not support it. So just saving the grammar
    // to be passed to the server with the recognize request. Should work OK for
    // now for recognize request with a single grammar.
    private Reader _loadedGrammarReader;
    private GrammarType _loadedGrammarType;
    private SrgsXmlDocument activatedGrammar;
    private boolean _activatedGrammarState;
    
    
    private SessionManager sessionManager;
   

    private Mrcpv2Client mrcpv2Client;

    public Mrcpv2SpokenInput() {
        listeners = new java.util.ArrayList<SpokenInputListener>();
        
        //get the local host address (used for rtp audio stream)
        //TODO: Maybe the receiver (call control) could be remote -- then this wont work.
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            hostAddress = "127.0.0.1";
            LOGGER.debug(e, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        LOGGER.info("Opening mrcpv2 spoken input.  (not implemented)");
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        LOGGER.info("Closing mrcpv2 spoken input.  (not implemented)");
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.remove(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        final Collection<BargeInType> types =
            new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<SrgsXmlDocument> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        try {
            mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        _loadedGrammarReader = reader;
        _loadedGrammarType = type;

        // TODO Determine why this method needs to return the
        // RuleGrammarImplementation. Hopefully it does not really need to...
        return null;
        // return new RuleGrammarImplementation(grammar);

    }

    /**
     * Activates the given grammar.
     *
     * @param document
     *            grammar
     * @param activate
     *            <code>true</code> if the grammar should be activated.
     *
     * @return <code>true</code> if the grammar is active.
     * @exception BadFetchError
     *                Error creating the grammar.
     */
    private boolean activateGrammar(final SrgsXmlDocument document,
            final boolean activate) throws BadFetchError {
        activatedGrammar = document;
        _activatedGrammarState = activate;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {

        // make sure that the resoure (mrcp channel) is setup
        try {
            mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError("recognizer not available");
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof SrgsXmlGrammarImplementation) {
                final SrgsXmlGrammarImplementation grammar =
                    (SrgsXmlGrammarImplementation) current;
                SrgsXmlDocument document = grammar.getGrammar();
                activateGrammar(document, true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError {
        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof SrgsXmlGrammarImplementation) {
                final SrgsXmlGrammarImplementation grammar =
                    (SrgsXmlGrammarImplementation) current;
                SrgsXmlDocument document = grammar.getGrammar();
                activateGrammar(document, false);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this record() method.
     */
    public void record(final OutputStream out) throws NoresourceError {
        throw new NoresourceError("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {

        SpeechClient speechClient = null;
        // make sure that the resource (recognition MRCP channel) is setup
        try {
            speechClient = mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }

        try {
            _loadedGrammarReader.reset();
            long noInputTimeout = 0;
            speechClient.recognize(_loadedGrammarReader, false, false,
                    noInputTimeout);
        } catch (MrcpInvocationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mrcpv2 invocation exception while initiating a "
                        + "recognition request", e);
            }
            throw new NoresourceError(e);
        } catch (IllegalValueException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Illegal Value exception while initiating a "
                        + "recognition request", e);
            }
            throw new NoresourceError(e);
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "IO exception while initiating a recognition request",
                        e);
            }
            throw new NoresourceError(e);
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Interruped exception while initiating a "
                        + "recognition request", e);
            }
            throw new NoresourceError(e);
        } catch (NoMediaControlChannelException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No Media Control Channel exception while "
                        + "initiating a recognition request", e);
            }
            throw new NoresourceError(e);
        }

        final SpokenInputEvent event = new SpokenInputEvent(this,
                SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {

        SpeechClient speechClient = null;
        // make sure that the resoure (recognition mrcp channel) is setup
        try {
            speechClient = mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
            LOGGER.warn("No MRCP recognition media control channel to stop "
                    + "recocognition.  Ignoring request.");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stoping recognition...");
        }
        try {
            speechClient.stopActiveRecognitionRequests();
        } catch (MrcpInvocationException e) {
            LOGGER.warn("MrcpException while stopping recognition."
                    + e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.warn("IOException while stopping recognition."
                    + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            LOGGER.warn("InteruptedException while stopping recognition."
                    + e.getLocalizedMessage());
        } catch (NoMediaControlChannelException e) {
            LOGGER.warn("No Media Control Channel Exception while stopping "
                    + "recognition." + e.getLocalizedMessage());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {

        mrcpv2Client = new Mrcpv2Client(sessionManager);
        
        
        //set the local rtp Port
        mrcpv2Client.setClientPort(rtpReceiverPort);
        

        //set the local host address
        mrcpv2Client.setClientAddress(hostAddress);

        
        //create the mrcp tts channel
        try {
            mrcpv2Client.createRecogChannel();
        } catch (SdpException e) {
            LOGGER.info(e, e);
            throw new IOException(e.getLocalizedMessage());
        } catch (SipException e) {
            LOGGER.info(e, e);
            throw new IOException(e.getLocalizedMessage());
        }
        

        remoteRtpHost = mrcpv2Client.getServerAddress();
        remoteRtpPort = mrcpv2Client.getServerPort();
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connected the  spokeninput mrcpv2 client to the server");
        }

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        
        try {
            mrcpv2Client.terminateRecogChannel();
        } catch (MrcpInvocationException e) {
            LOGGER.info(e, e);
        } catch (IOException e) {
            LOGGER.info(e, e);
        } catch (InterruptedException e) {
            LOGGER.info(e, e);
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Disconnected the spoken input mrcpv2 client form the server");
        }
        
        mrcpv2Client=null;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {

        final Collection<GrammarType> types = new java.util.ArrayList<GrammarType>();

        types.add(GrammarType.JSGF);

        return types;
    }

    // TODO: Determine if this is needed in the mrcpv2 case. Hopefully returning
    // null is ok.
    public URI getUriForNextSpokenInput() throws NoresourceError {
        String url = "rtp://"+remoteRtpHost+":"+remoteRtpPort;
        URI u = null;
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            LOGGER.info(e, e);
        }
        return u;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        LOGGER.warn("isBusy check is not implemented.");
        // TODO: Implement this. Is it checking if there is a recognition
        // request active?
        return false;
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param event
     *            the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy = new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }

    public void recognitionEventReceived(MrcpEvent event, RecognitionResult r) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Recognition event received: " + event);
        }

        final org.jvoicexml.RecognitionResult recognitionResult =
            new Mrcpv2RecognitionResult(r);

        final SpokenInputEvent spokenInputEvent = new SpokenInputEvent(this,
                SpokenInputEvent.RESULT_ACCEPTED, recognitionResult);
        fireInputEvent(spokenInputEvent);
    }

    public void characterEventReceived(String c, EventType status) {
        LOGGER.warn("Character received event occurred in Mrcpv2 Spoken Input "
                + "implementation.  Not implemeneted");
    }

    public void speechSynthEventReceived(MrcpEvent event) {
        LOGGER.warn("Speech Synth event received not implemented in "
                + "SpokenInput: " + event);
    }

    /**
     * @return the rtpReceiverPort
     */
    public int getRtpReceiverPort() {
        return rtpReceiverPort;
    }

    /**
     * @param rtpReceiverPort the rtpReceiverPort to set
     */
    public void setRtpReceiverPort(int rtpReceiverPort) {
        this.rtpReceiverPort = rtpReceiverPort;
    }

    /**
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * @param sessionManager the sessionManager to set
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
