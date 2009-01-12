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
import java.net.URI;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SynthesisResult;

import org.jvoicexml.SpeakableText;

import org.jvoicexml.client.mrcpv2.Mrcpv2RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

import org.jvoicexml.xml.ssml.SsmlDocument;
import org.mrcp4j.MrcpEventName;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.MrcpEvent;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Audio output that uses the JSAPI 2.0 to address the TTS engine.
 * 
 * <p>
 * Handle all MRCPv2 calls to the TTS engine.
 * </p>
 * 
 * @author Spencer Lord
 * @version $Revision: $
 */
public final class Mrcpv2SynthesizedOutput
        implements SynthesizedOutput, ObservableSynthesizedOutput,
        SpeechEventListener {
        //SpeakableListener, SynthesizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SynthesizedOutput.class);
    
    /** The system output listener. */
    private Collection<SynthesizedOutputListener> listeners;

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

    
	private Mrcpv2RemoteClient mrcpv2Client;  
    
    

    /**
     * Constructs a new audio output.
     * 
     * @param defaultDescriptor
     *                the default synthesizer mode descriptor.
     * @param locator the media locator to use.
     */
    public Mrcpv2SynthesizedOutput() {

        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        
        //TODO: SHould there be a queue here on the client side too?  There is one on the server.
        //queuedSpeakables = new java.util.ArrayList<SpeakableText>();

    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Open not implemented");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Close not implemented");
        }
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
            final boolean bargein, final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
    	
    	
    	SpeechClient speechClient = null;
    	//make sure that the resoure (recognition mrcp channel) is setup
    	try {
    		speechClient = mrcpv2Client.getTtsClient();
        } catch (NoMediaControlChannelException e) {
        	throw new NoresourceError("recognizer not available");
        }
    	
        String speakText=null;
        try {
            //TODO: Pass on the entire SSML doc (and remove the code that extracts the text)
            //The following code extract the text from the SSML since 
            // the mrcp server (cairo) does not support SSML yet (really teh tts engine needs to support it i.e freetts)
            if (speakable instanceof SpeakableSsmlText) {
               InputStream is = null; 
               String temp = speakable.getSpeakableText(); 
               byte[] b = temp.getBytes();
               is = new ByteArrayInputStream(b);
               InputSource src = new InputSource( is);
               SsmlDocument ssml = new SsmlDocument(src);
               speakText = ssml.getSpeak().getTextContent();
            } else if (speakable instanceof SpeakablePlainText) {
                speakText = speakable.getSpeakableText();
            }
            //play the text
            speechClient.queuePrompt(false, speakText);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MrcpInvocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoMediaControlChannelException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new SynthesizedOutputEvent(this,
                SynthesizedOutputEvent.OUTPUT_STARTED, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
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
        final SynthesizedOutputEvent event = new SynthesizedOutputEvent(this,
                SynthesizedOutputEvent.MARKER_REACHED, mark);

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
        final SynthesizedOutputEvent event = new SynthesizedOutputEvent(this,
                SynthesizedOutputEvent.OUTPUT_ENDED, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
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
        final SynthesizedOutputEvent event = new SynthesizedOutputEvent(this,
                SynthesizedOutputEvent.QUEUE_EMPTY);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    private void fireOutputUpdate(final SynthesisResult synthesisResult) {
        final SynthesizedOutputEvent event = new SynthesizedOutputEvent(this,
                SynthesizedOutputEvent.OUTPUT_UPDATE, synthesisResult);

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
    	
    	SpeechClient speechClient = null;
    	//make sure that the resoure (recognition mrcp channel) is setup
    	try {
    		speechClient = mrcpv2Client.getTtsClient();
        } catch (NoMediaControlChannelException e) {
        	throw new NoresourceError("recognizer not available");
        }
    	
        try {
            speechClient.playBlocking(false,text);

        } catch (MrcpInvocationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mrcpv2 invocation exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("IO exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Interrupted exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (NoMediaControlChannelException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No Media Control Channel exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        }
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
     * Convenient method to wait until all output is being played.
     */
    public void waitQueueEmpty() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("WaitQueueEmpty not implemented");
        }
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
    public void connect(final RemoteClient remoteClient) throws IOException {

        mrcpv2Client = (Mrcpv2RemoteClient) remoteClient;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connect not implemented");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Disconnect not implemented");
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
        //TODO: Determine what this is used for
       /* if (synthesizer != null) {
            try {
                URI uri = new URI(synthesizer.getAudioManager()
                        .getMediaLocator());
                if (uri.getQuery() != null) {
                    String[] parametersString = uri.getQuery().split("\\&");
                    String newParameters = "";
                    String participantUri = "";
                    for (String part : parametersString) {
                        String[] queryElement = part.split("\\=");
                        if (queryElement[0].equals("participant")) {
                            participantUri = uri.getScheme();
                            participantUri += "://";
                            participantUri += queryElement[1];
                            participantUri += "/audio";
                        } else {
                            if (newParameters.equals("")) {
                                newParameters += "?";
                            } else {
                                newParameters += "&";
                            }
                            newParameters += queryElement[0];
                            newParameters += "=";
                            newParameters += queryElement[1];
                        }
                    }
                    if (!participantUri.equals("")) {
                        participantUri += newParameters;
                    }

                    return new URI(participantUri);
                }
                return uri;
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return null;
            }
        }*/

        return null;
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
        //TODO: Should there be a queue here in the client or over on teh server or both?
        //fireQueueEmpty();
        //TODO: Handle  speech markers    
        //} else if (MrcpEventName.SPEECH_MARKER.equals(event.getEventName())) {
        //    fireMarkerReached(mark);
        } else {
                LOGGER.warn("Unhandled mrcp speech synth event "+event.getEventName());          
        }    
    }

    public void recognitionEventReceived(MrcpEvent event, RecognitionResult r) {
        LOGGER.warn("mrcpv2synthesized output received a recog event.  Discarding it.");
    }
    
    public void characterEventReceived(String c, EventType status) {
        LOGGER.debug("characterEventReceived not implemented");
    }

}
