/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.error.jvxml.ExceptionWrapper;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
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
 * @since 0.7
 */
public final class Mrcpv2SpokenInput
        implements SpokenInput, SpeechEventListener {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(Mrcpv2SpokenInput.class);

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    /** The grammar parser to use. */
    private final Map<String, GrammarParser<?>> parsers;

    // TODO Handle load and activate grammars properly on the server. At
    // present the mrcpv2 server does not support it. So just saving the grammar
    // to be passed to the server with the recognize request. Should work OK for
    // now for recognize request with a single grammar.

    // TODO Handle multiple grammars, now just the first one activated is
    // active.
    /** Current active grammars. */
    private final Collection<GrammarImplementation<?>> activeGrammars;

    /** The session manager. */
    private SessionManager sessionManager;

    /** The ASR client. */
    private SpeechClient speechClient;

    /** The timeout value that was used to start a recognition. */
    private long lastUsedTimeout;
    
    /**
     * Constructs a new object.
     */
    public Mrcpv2SpokenInput() {
        activeGrammars = new java.util.ArrayList<GrammarImplementation<?>>();
        listeners = new java.util.ArrayList<SpokenInputListener>();
        parsers = new java.util.HashMap<String, GrammarParser<?>>();
        lastUsedTimeout = SpeechRecognizerProperties.DEFAULT_NO_INPUT_TIMEOUT;
    }

    /**
     * {@inheritDoc}
     * 
     * The no input timeout is handled by the MRCPv2 provider
     */
    @Override
    public long getNoInputTimeout() {
        return -1;
    }

    /**
     * Set the grammar parsers to use.
     * 
     * @param grammarParsers
     *            the grammar parsers to use
     * @since 0.7.8
     */
    public void setGrammarParsers(final List<GrammarParser<?>> grammarParsers) {
        for (GrammarParser<?> parser : grammarParsers) {
            final GrammarType type = parser.getType();
            parsers.put(type.toString(), parser);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
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
        final Collection<BargeInType> types =
                new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(final URI uri,
            final GrammarType type)
            throws NoresourceError, IOException, UnsupportedFormatError {
        final GrammarParser<?> parser = parsers.get(type.toString());
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
                LOGGER.debug(
                        "activated grammar " + grammar.getGrammarDocument());
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
                LOGGER.debug(
                        "deactivated grammar " + grammar.getGrammarDocument());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition(final DataModel model,
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
            throws NoresourceError, BadFetchError {
        if (activeGrammars.size() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("No active grammars");
            }
            throw new NoresourceError("No Active Grammars");
        }
        try {

            lastUsedTimeout = speech.getNoInputTimeoutAsMsec();
            boolean hotword = false;
            boolean attachGrammar = true;
            GrammarImplementation<?> firstGrammar = activeGrammars.iterator().next(); 
            GrammarDocument firstGrammarDocument = (GrammarDocument) firstGrammar.getGrammarDocument();
            // TODO use the URI here instead of putting the URI inside the document in 
            // org.jvoicexml.interpreter.grammar.halef.HalefGrammarParser.java
            // TODO load the application type from the grammar
            LOGGER.info(String.format("Starting recognition with url: %s", firstGrammarDocument.getDocument()));
            speechClient.setContentType("application/wfst");
            speechClient.recognize(
                    firstGrammarDocument.getDocument(), hotword,
                    attachGrammar, lastUsedTimeout);
        } catch (MrcpInvocationException e) {
            LOGGER.error("MRCPv2 invocation exception while initiating a "
                    + "recognition request", e);
            throw new NoresourceError(
                    "MRCPv2 invocation exception while initiating a "
                    + "recognition request", e);
        } catch (IllegalValueException e) {
            LOGGER.error("Illegal Value exception while initiating a "
                    + "recognition request", e);
            throw new NoresourceError(
                    "Illegal Value exception while initiating a "
                    + "recognition request", e);
        } catch (IOException e) {
            LOGGER.error("IO exception while initiating a recognition request",
                    e);
            throw new NoresourceError(
                    "IO exception while initiating a recognition request", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interruped exception while initiating a "
                    + "recognition request", e);
            throw new NoresourceError("Interruped exception while initiating a "
                    + "recognition request", e);
        } catch (NoMediaControlChannelException e) {
            LOGGER.error("No Media Control Channel exception while "
                    + "initiating a recognition request", e);
            throw new NoresourceError(
                    "No Media Control Channel exception while "
                    + "initiating a recognition request", e);
        }

        final SpokenInputEvent event = new RecognitionStartedEvent(this, null);
        fireInputEvent(event);
        LOGGER.debug("recognition started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        try {
            lastUsedTimeout =
                    SpeechRecognizerProperties.DEFAULT_NO_INPUT_TIMEOUT;
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
        LOGGER.debug("recognition stopped");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
        final Mrcpv2ConnectionInformation mrcpv2Client =
                (Mrcpv2ConnectionInformation) client;
        LOGGER.info("connecting to '" + mrcpv2Client + "'");

        speechClient = mrcpv2Client.getAsrClient();
        if (speechClient == null) {
            throw new IOException("No ASR client");
        }
        speechClient.addListener(this);
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
                LOGGER.debug(
                        "Disconnected the spoken input mrcpv2 client form the server");
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
        for (GrammarParser<?> parser : parsers.values()) {
            supportedTypes.add(parser.getType());
        }
        return supportedTypes;
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
            final Collection<SpokenInputListener> copy =
                    new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the given error.
     * 
     * @param error
     *            the error vent.
     * @since 0.7.8
     */
    void fireErrorEvent(final ErrorEvent error) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy =
                    new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputError(error);
            }
        }
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param event
     *            the event.
     * @since 0.6
     */
    void fireTimeoutEvent() {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy =
                    new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.timeout(lastUsedTimeout);
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
            } catch (MrcpInvocationException | IOException 
                    | InterruptedException e) {
                LOGGER.warn(e.getMessage(), e);
                final ErrorEvent error =
                        new ExceptionWrapper(e.getMessage(), e);
                fireErrorEvent(error);
                return;
            }

            final SpokenInputEvent spokenInputEvent = new InputStartedEvent(
                    this, null, ModeType.VOICE);
            fireInputEvent(spokenInputEvent);

        } else if (event == SpeechEventType.RECOGNITION_COMPLETE) {
            // Some implementations may return an empty recognition result.
            // Handle these as timeouts as the input cannot be used at all.
            if (result == null) {
                fireTimeoutEvent();
            } else {
                LOGGER.info("Recognition results are: " + result.getText());
                final org.jvoicexml.RecognitionResult recognitionResult =
                        new Mrcpv2RecognitionResult(result);
    
                final SpokenInputEvent spokenInputEvent =
                        new RecognitionEvent(this, null, recognitionResult);
                fireInputEvent(spokenInputEvent);
            }
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
