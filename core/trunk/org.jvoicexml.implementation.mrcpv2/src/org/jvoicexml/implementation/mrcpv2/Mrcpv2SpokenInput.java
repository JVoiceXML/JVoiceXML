/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.DocumentGrammarImplementation;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
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
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class Mrcpv2SpokenInput
        implements SpokenInput, ObservableSpokenInput, SpeechEventListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SpokenInput.class);

    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** The port that will receive the stream from mrcp server. **/
    private int rtpReceiverPort;

    // TODO Workaround for JMF.  Even though only sending audio,
    // JMF rtp setup needs a local rtp port too.
    // Really should not be needed.

    /** The local host address. */
    private String hostAddress;
    
    private String remoteRtpHost;
    private int remoteRtpPort;
    
    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    // private JSGFGrammar _grammar = new JSGFGrammar();

    // TODO Handle load and activate grammars properly on the server. At
    // present the mrcpv2 server does not support it. So just saving the grammar
    // to be passed to the server with the recognize request. Should work OK for
    // now for recognize request with a single grammar.  
    
    //TODO Handle multiple grammars, now just the last one activated is active.
    private GrammarDocument activatedGrammar;
    private int numActiveGrammars;

    /** The session manager. */
    private SessionManager sessionManager;
   
    /** The ASR client. */
    private SpeechClient speechClient;

    /**
     * Constructs a new object.
     */
    public Mrcpv2SpokenInput() {
        listeners = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        LOGGER.info("Opening mrcpv2 spoken input.");
        //get the local host address (used for rtp audio stream)
        //TODO Maybe the receiver (call control) could be remote -- then this
        // wont work.
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        LOGGER.info("Closing mrcpv2 spoken input.");
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
    public GrammarImplementation<GrammarDocument> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        final char[] buffer = new char[READ_BUFFER_SIZE];
        final StringBuilder str = new StringBuilder();
        int num;
        try {
            do {
                num = reader.read(buffer);
                if (num >= 0) {
                    str.append(buffer, 0, num);
                }
            } while(num >= 0);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
        final GrammarDocument document =
            new JVoiceXmlGrammarDocument(null, str.toString());
        document.setMediaType(type);
        return new DocumentGrammarImplementation(document);
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof SrgsXmlGrammarImplementation) {
                LOGGER.warn("SRGS not yet supported in mrcpv2 implementation");
            }
            if (current instanceof DocumentGrammarImplementation) {
                final DocumentGrammarImplementation grammar =
                    (DocumentGrammarImplementation) current;
                activatedGrammar = grammar.getGrammar();
                numActiveGrammars = 1;
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
                LOGGER.warn("SRGS not yet supported in mrcpv2 implementation");
            }
            if (current instanceof DocumentGrammarImplementation) {
                final DocumentGrammarImplementation grammar =
                    (DocumentGrammarImplementation) current;
                if (grammar.getGrammar().equals(activatedGrammar)) {
                    numActiveGrammars = 0;
                }
                
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
    @Override
    public void startRecognition(
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
        throws NoresourceError, BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }
        if ((activatedGrammar == null) || (numActiveGrammars == 0)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("No active grammars");
            }
            throw new NoresourceError("No Active Grammars");   
        }
        try {

            long noInputTimeout = 0;
            boolean hotword = false;
            boolean attachGrammar = true;
            //todo: add a method in speechclient to take a string (rather than constructing readers on the fly to match the API).
            speechClient.recognize(new StringReader(activatedGrammar.getDocument()), hotword, 
                    attachGrammar, noInputTimeout);
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
    public void connect(final ConnectionInformation client) throws IOException {

        Mrcpv2ConnectionInformation mrcpv2Client = (Mrcpv2ConnectionInformation) client;
        LOGGER.debug(mrcpv2Client.toString2());

        if (mrcpv2Client.getAsrClient() != null) {
            speechClient = mrcpv2Client.getAsrClient();
            speechClient.addListener(this);
            return;
        } else {
            //TODO:  What condition is this?  Need to digram out the sequence of events.  Its is getting confusing...
            LOGGER.warn("No ASR Client.");
        }


    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
        // If the connection is already established, do not touch this
        // connection.
        if (client instanceof Mrcpv2ConnectionInformation) {
            speechClient.removeListener(this);
            speechClient = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                "Disconnected the spoken input mrcpv2 client form the server");
            }
            return;
        }

        //TODO not sure we should shut it down... can it be used later by
        // another object?  commented it out for now.
        /*
        try {
            speechClient.shutdown();
        } catch (MrcpInvocationException e) {
            LOGGER.info(e, e);
        } catch (IOException e) {
            LOGGER.info(e, e);
        } catch (InterruptedException e) {
            LOGGER.info(e, e);
        } finally {
            speechClient = null;
        }
        */
        

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
        final Collection<GrammarType> types =
            new java.util.ArrayList<GrammarType>();
        types.add(GrammarType.JSGF);

        return types;
    }

    // TODO: Determine if this is needed in the mrcpv2 case. Hopefully
    // returning null is ok.
    public URI getUriForNextSpokenInput() throws NoresourceError {
        String url = "rtp://" + remoteRtpHost + ":" + remoteRtpPort;
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        LOGGER.warn("isBusy check is not implemented.");
        // TODO Implement this. Is it checking if there is a recognition
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
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recognitionEventReceived(final SpeechEventType event,
            final RecognitionResult result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Recognition event received: " + event);
        }
        
        if (event == SpeechEventType.START_OF_INPUT) {
            try {
                speechClient.sendBargeinRequest();
            } catch (MrcpInvocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            final SpokenInputEvent spokenInputEvent = new SpokenInputEvent(this,
                    SpokenInputEvent.INPUT_STARTED);
            fireInputEvent(spokenInputEvent);
        
        } else if (event == SpeechEventType.RECOGNITION_COMPLETE) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Recognition results are: " + result.getText());
            }
            final org.jvoicexml.RecognitionResult recognitionResult =
                new Mrcpv2RecognitionResult(result);

            final SpokenInputEvent spokenInputEvent = new SpokenInputEvent(this,
                    SpokenInputEvent.RESULT_ACCEPTED, recognitionResult);
            fireInputEvent(spokenInputEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characterEventReceived(final String c,
            final DtmfEventType status) {
        LOGGER.warn("Character received event occurred in Mrcpv2 Spoken Input "
                + "implementation.  Not implemeneted");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void speechSynthEventReceived(final SpeechEventType event) {
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
     * Sets the RTP receiver port.
     * @param port the rtpReceiverPort to set
     */
    public void setRtpReceiverPort(final int port) {
        rtpReceiverPort = port;
    }

    /**
     * Retrieves the session manager.
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Sets the session manager.
     * @param manager the sessionManager to set
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }
}
