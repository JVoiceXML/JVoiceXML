/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.sdp.SdpException;
import javax.sip.SipException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesisResult;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.MarkerReachedEvent;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.OutputUpdateEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.mrcp4j.MrcpEventName;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.MrcpEvent;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.speechforge.cairo.sip.SipSession;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Audio output that uses the MRCPv2 to address the TTS engine.
 * 
 * <p>
 * Handle all MRCPv2 calls to the TTS engine.
 * </p>
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7
 */
public final class Mrcpv2SynthesizedOutput
        implements SynthesizedOutput, ObservableSynthesizedOutput,
        SpeechEventListener {
        //SpeakableListener, SynthesizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SynthesizedOutput.class);
    
    /** The system output listener. */
    private final Collection<SynthesizedOutputListener> listeners;

    /** Type of this resources. */
    private String type;

    /**
     * Flag to indicate that TTS output and audio can be canceled.
     * 
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /** Queued speakables. */
    //private final List<SpeakableText> queuedSpeakables;

    /** The session manager. */
    private SessionManager sessionManager;
   
    /** The speech client. */
    private SpeechClient speechClient;

    /** the port that will receive the stream from mrcp server **/
    private int rtpReceiverPort;
    // TODO: Perhaps this port should be managed by call manager -- it is the one that uses it. 
    
    /** the local host address **/
    String hostAddress;

    /**
     * Constructs a new audio output.
     * 
     * @param defaultDescriptor
     *                the default synthesizer mode descriptor.
     * @param locator the media locator to use.
     */
    public Mrcpv2SynthesizedOutput() {

        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        
        //TODO: SHould there be a queue here on the client side too?  There is
        // one on the server.
        //queuedSpeakables = new java.util.ArrayList<SpeakableText>();
        
        //get the local host address (used to send the audio stream)
        //TODO: Maybe the receiver (call control) could be remote?
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
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.remove(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    public void queueSpeakable(final SpeakableText speakable,
            final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        String speakText = null;
        try {
            //TODO: Pass on the entire SSML doc (and remove the code that
            // extracts the text)
            //The following code extract the text from the SSML since 
            // the mrcp server (cairo) does not support SSML yet
            // (really the tts engine needs to support it i.e freetts)
            if (speakable instanceof SpeakableSsmlText) {
               InputStream is = null; 
               String temp = speakable.getSpeakableText(); 
               byte[] b = temp.getBytes();
               is = new ByteArrayInputStream(b);
               InputSource src = new InputSource(is);
               SsmlDocument ssml = new SsmlDocument(src);
               speakText = ssml.getSpeak().getTextContent();
            } else if (speakable instanceof SpeakablePlainText) {
                speakText = speakable.getSpeakableText();
            }
            //play the text
            speechClient.queuePrompt(false, speakText);
        } catch (ParserConfigurationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (MrcpInvocationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IOException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputStartedEvent(this,
                speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    
    /**
     * Notifies all listeners that the given marker has been reached.
     * 
     * @param mark
     *                the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event = new MarkerReachedEvent(this,
                mark);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputEndedEvent(this,
                speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue us empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    private void fireOutputUpdate(final SynthesisResult synthesisResult) {
        final SynthesizedOutputEvent event = new OutputUpdateEvent(this,
                synthesisResult);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Speaks a plain text string.
     * 
     * @param text
     *                String contains plain text to be spoken.
     * @exception NoresourceError
     *                    No synthesizer allocated.
     * @exception BadFetchError
     *                    Synthesizer in wrong state.
     */
    public void queuePlaintext(final String text) throws NoresourceError,
            BadFetchError {
        try {
            speechClient.playBlocking(false, text);
        } catch (MrcpInvocationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IOException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cancelOutput not implemented");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        LOGGER.warn("waitNonBargeInPlayed not implemented");
    }

    /**
     * Convenient method to wait until all output is being played.
     */
    @Override
    public void waitQueueEmpty() {
        LOGGER.warn("WaitQueueEmpty not implemented");
    }


    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating output..." );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output..." );
        }

        listeners.clear();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...passivated output");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        //create the mrcp tts channel
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating sip session to '" + hostAddress + ":"
                    + rtpReceiverPort + "'");
        }
        try {
            final SipSession session =
                sessionManager.newSynthChannel(rtpReceiverPort, hostAddress,
                    "Session Name");

            //construct the speech client with this session
            speechClient = new SpeechClientImpl(session.getTtsChannel(), null);
        } catch (SdpException e) {
            LOGGER.error(e, e);
            throw new IOException(e.getLocalizedMessage());
        } catch (SipException e) {
            LOGGER.error(e, e);
            throw new IOException(e.getLocalizedMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connected the synthesizedoutput mrcpv2 client to the "
                    + "server");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {
        
        //disconnect the mrcp channel
        try {
            speechClient.shutdown();
        } catch (MrcpInvocationException e) {
            LOGGER.warn(e, e);
        } catch (IOException e) {
            LOGGER.warn(e, e);
        } catch (InterruptedException e) {
            LOGGER.warn(e, e);
        } finally {
            speechClient = null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
           "Disconnected the  synthesizedoutput mrcpv2 client form the server");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     * 
     * @param resourceType
     *                type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAudioFileOutput() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SetAudioFileOutput not implemented");
        }
    }


 
    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        String url = "rtp://"+hostAddress+":"+rtpReceiverPort;
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
        //TODO: query server to determine if queue is non-empty
        return false;
    }
    

   //Cairo Client Speech event methods (from SpeechEventListener i/f) 

    public void speechSynthEventReceived(MrcpEvent event) {
        if (LOGGER.isDebugEnabled()) {
           LOGGER.debug("Speech synth event "+event.getContent());
        }
        if (MrcpEventName.SPEAK_COMPLETE.equals(event.getEventName())) {
            
            // TODO: get the speakable object from the event?
            fireOutputStarted(new SpeakablePlainText());
        //TODO: Should there be a queue here in the client or over on the server
        // or both?
        //fireQueueEmpty();
        //TODO: Handle  speech markers    
        //} else if (MrcpEventName.SPEECH_MARKER.equals(event.getEventName())) {
        //    fireMarkerReached(mark);
        } else {
                LOGGER.warn("Unhandled mrcp speech synth event "
                        + event.getEventName());          
        }    
    }

    public void recognitionEventReceived(MrcpEvent event, RecognitionResult r) {
        LOGGER.warn("mrcpv2synthesized output received a recog event.  Discarding it.");
    }
    
    public void characterEventReceived(String c, EventType status) {
        LOGGER.debug("characterEventReceived not implemented");
    }

    /**
     * @return the receiverPort
     */
    public int getRtpReceiverPort() {
        return rtpReceiverPort;
    }

    /**
     * @param receiverPort the receiverPort to set
     */
    public void setRtpReceiverPort(int receiverPort) {
        this.rtpReceiverPort = receiverPort;
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
