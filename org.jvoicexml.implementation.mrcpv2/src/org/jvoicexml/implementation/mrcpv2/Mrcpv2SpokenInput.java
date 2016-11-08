/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.documentserver.ExternalGrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.DocumentGrammarImplementation;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
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
 * @author Patrick L. Lange
 * @version $Revision$
 * @since 0.7
 */
public final class Mrcpv2SpokenInput
        implements SpokenInput, SpeechEventListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SpokenInput.class);

    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** The port that will receive the stream from mrcp server. **/
    private int rtpReceiverPort;

    // TODO Workaround for JMF. Even though only sending audio,
    // JMF rtp setup needs a local rtp port too.
    // Really should not be needed.

    /** The local host address. */
    private String hostAddress;

    private String remoteRtpHost;
    private int remoteRtpPort;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    /** The grammar parser to use. */
    private final Map<String, GrammarParser<?>> parsers;
    // private JSGFGrammar _grammar = new JSGFGrammar();

    // TODO Handle load and activate grammars properly on the server. At
    // present the mrcpv2 server does not support it. So just saving the grammar
    // to be passed to the server with the recognize request. Should work OK for
    // now for recognize request with a single grammar.

    // TODO Handle multiple grammars, now just the first one activated is active.
    private Collection<GrammarImplementation<?>> activeGrammars;

    /** The session manager. */
    private SessionManager sessionManager;

    /** The ASR client. */
    private SpeechClient speechClient;

    /**
     * Constructs a new object.
     */
    public Mrcpv2SpokenInput() {
	activeGrammars = new java.util.ArrayList<GrammarImplementation<?>>();
        listeners = new java.util.ArrayList<SpokenInputListener>();
	parsers = new java.util.HashMap<String, GrammarParser<?>>();
    }

    /**
     * Set the grammar parsers to use.
     * @param grammarParsers the grammar parsers to use
     * @since 0.7.8
    */
    public void setGrammarParsers(final List<GrammarParser<?>> grammarParsers) {
        for (GrammarParser<?> parser : grammarParsers) {
	    final GrammarType type = parser.getType();
	    parsers.put(type.getType(), parser);
  	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("added parser '" + parser + "' for grammar type '"
			+ type + "'");
	    }
	}
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        LOGGER.info("Opening mrcpv2 spoken input.");
        // get the local host address (used for rtp audio stream)
        // TODO Maybe the receiver (call control) could be remote -- then this
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
    @Override
    public void close() {
        LOGGER.info("Closing mrcpv2 spoken input.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.remove(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        final Collection<BargeInType> types = new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(final URI uri,
            final GrammarType type) throws NoresourceError, IOException,
            UnsupportedFormatError {
	for (GrammarParser<?> parser : parsers.values()) {
		LOGGER.info(parser.getType().getType() == type.getType());
	}
	LOGGER.info(parsers.get(type));
        final GrammarParser<?> parser = parsers.get(type.getType());
	if (parser == null) {
	    throw new UnsupportedFormatError("'" + type + "' is not supported");
	}
	return parser.load(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
	activeGrammars.addAll(grammars);
	if (LOGGER.isDebugEnabled()) {
		for (GrammarImplementation<?> grammar : grammars) {
		     LOGGER.debug("activated grammar "
			+ grammar.getGrammarDocument());
		}
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError {
	if (grammars == null) {
	    return;
	}
	activeGrammars.removeAll(grammars);
	if (LOGGER.isDebugEnabled()) {
	    for (GrammarImplementation<?> grammar : grammars) {
	         LOGGER.debug("deactivated grammar "
		    + grammar.getGrammarDocument());
	    }
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition(final DataModel model,
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf) throws NoresourceError,
            BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }
        if (activeGrammars.size() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("No active grammars");
            }
            throw new NoresourceError("No Active Grammars");
        }
        try {

            long noInputTimeout = 0;
            boolean hotword = false;
            boolean attachGrammar = true;
	   
	    GrammarImplementation<?> firstGrammar = activeGrammars.iterator().next(); 
	    GrammarDocument firstGrammarDocument = (GrammarDocument) firstGrammar.getGrammarDocument();
	    // TODO use the URI here instead of putting the URI inside the document in 
	    // org.jvoicexml.interpreter.grammar.halef.HalefGrammarParser.java
	    speechClient.setContentType(firstGrammar.getMediaType().getType());
            speechClient.recognize(
                    firstGrammarDocument.getDocument(), hotword,
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

        final SpokenInputEvent event = new RecognitionStartedEvent(this, null);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
        final Mrcpv2ConnectionInformation mrcpv2Client = (Mrcpv2ConnectionInformation) client;
        LOGGER.info("connecting to '" + mrcpv2Client + "'");

        if (mrcpv2Client.getAsrClient() != null) {
            speechClient = mrcpv2Client.getAsrClient();
            speechClient.addListener(this);
            return;
        } else {
            throw new IOException("No ASR client");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
        // If the connection is already established, do not touch this
        // connection.
        if (client instanceof Mrcpv2ConnectionInformation) {
            speechClient.removeListener(this);
            speechClient = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Disconnected the spoken input mrcpv2 client form the server");
            }
            return;
        }

        // TODO not sure we should shut it down... can it be used later by
        // another object? commented it out for now.
        /*
         * try { speechClient.shutdown(); } catch (MrcpInvocationException e) {
         * LOGGER.info(e, e); } catch (IOException e) { LOGGER.info(e, e); }
         * catch (InterruptedException e) { LOGGER.info(e, e); } finally {
         * speechClient = null; }
         */

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
	Collection<GrammarType> supportedTypes = new java.util.HashSet<GrammarType>();
	for (GrammarParser<?> parser: parsers.values()) {
	    supportedTypes.add(parser.getType());
	}
	return supportedTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSpokenInput() throws NoresourceError {
        final String url = "rtp://" + remoteRtpHost + ":" + remoteRtpPort;
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            final Collection<SpokenInputListener> copy = new java.util.ArrayList<SpokenInputListener>();
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

            final SpokenInputEvent spokenInputEvent = new InputStartedEvent(
                    this, null, ModeType.VOICE);
            fireInputEvent(spokenInputEvent);

        } else if (event == SpeechEventType.RECOGNITION_COMPLETE) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Recognition results are: " + result.getText());
            }
            final org.jvoicexml.RecognitionResult recognitionResult = new Mrcpv2RecognitionResult(
                    result);

            final SpokenInputEvent spokenInputEvent = new RecognitionEvent(
                    this, null, recognitionResult);
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
     * 
     * @param port
     *            the rtpReceiverPort to set
     */
    public void setRtpReceiverPort(final int port) {
        rtpReceiverPort = port;
    }

    /**
     * Retrieves the session manager.
     * 
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Sets the session manager.
     * 
     * @param manager
     *            the sessionManager to set
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }
}
