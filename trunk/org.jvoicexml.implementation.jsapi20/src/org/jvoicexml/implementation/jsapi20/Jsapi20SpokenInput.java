/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerListener;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.RuleGrammar;

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
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Audio input that uses the JSAPI 2.0 to address the recognition engine.
 *
 * <p>
 * Handle all JSAPI calls to the recognizer to make JSAPI transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Renato Cassaca
 * @author David Rodriguez
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi20SpokenInput implements SpokenInput,
        ObservableSpokenInput, RecognizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi20SpokenInput.class);

    /** The speech recognizer. */
    private Recognizer recognizer;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    /** The default recognizer mode descriptor. */
    private final RecognizerMode desc;

    /** The media locator to use. */
    private final String mediaLocator;
    private final String asrMediaLocator;

    private JVoiceXMLRecognitionListener currentResultListener;


    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     */
    public Jsapi20SpokenInput(final RecognizerMode defaultDescriptor,
                              final String locator) {
        desc = defaultDescriptor;
        mediaLocator = locator;
        String asrML = "";
        listeners = new java.util.ArrayList<SpokenInputListener>();

        if (locator != null) {
            try {
                URI uri = new URI(locator);
                if (uri.getQuery() != null) {
                    String[] parametersString = uri.getQuery().split("\\&");
                    String newParameters = "";
                    for (String part : parametersString) {
                        String[] queryElement = part.split("\\=");
                        if (queryElement[0].equals("participant")) {
                            String participantUri = uri.getScheme();
                            participantUri += "://";
                            participantUri += queryElement[1];
                            participantUri += "/audio";
                            asrML = participantUri;
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
                    if (!asrML.equals("")) {
                        asrML += newParameters;
                    }
                }
            } catch (URISyntaxException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        if (asrML.length() == 0) {
            asrMediaLocator = null;
        } else {
            asrMediaLocator = asrML;
        }
        currentResultListener = null;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        try {
            recognizer = (Recognizer) EngineManager.createEngine(desc);
            if (recognizer != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("allocating recognizer...");
                }

                try {
                    final AudioManager manager = recognizer.getAudioManager();
                    manager.setMediaLocator(asrMediaLocator);
                    recognizer.allocate();
                    final SpeechEventExecutor executor =
                        new SynchronousSpeechEventExecutor();
                    recognizer.setSpeechEventExecutor(executor);
                    recognizer.addRecognizerListener(this);
                } catch (EngineStateException ex) {
                    throw new NoresourceError(ex);
                } catch (EngineException ex) {
                    throw new NoresourceError(ex);
                } catch (AudioException ex) {
                    throw new NoresourceError(ex);
                }
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (recognizer == null) {
            LOGGER.warn("no recognizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio input...");
            LOGGER.debug("deallocating recognizer...");
        }

        try {
            recognizer.deallocate();
        } catch (EngineStateException ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error deallocating the recognizer", ex);
            }
        } catch (AudioException ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error deallocating the recognizer", ex);
            }
        } catch (EngineException ee) {
            LOGGER.error("error deallocating the recognizer", ee);
        } finally {
            recognizer = null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio input closed");
        }
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
    public GrammarImplementation<RuleGrammar> newGrammar(final GrammarType type)
        throws NoresourceError, UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new empty grammar");
        }

        final String name = UUID.randomUUID().toString();
        RuleGrammar ruleGrammar = null;
        try {
            final GrammarManager manager = recognizer.getGrammarManager();
            ruleGrammar = manager.createRuleGrammar(name, null);
        } catch (EngineException ex) {
          throw new NoresourceError(ex);
        } catch (EngineStateException ex) {
          throw new NoresourceError(ex);
        } catch (IllegalArgumentException ex) {
          throw new NoresourceError(ex);
        }

        return new RuleGrammarImplementation(ruleGrammar);
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<RuleGrammar> loadGrammar(final Reader reader,
            final GrammarType type) throws NoresourceError, BadFetchError,
            UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        RuleGrammar grammar = null;

        try {
            final GrammarManager manager = recognizer.getGrammarManager();
            grammar = (RuleGrammar) manager.loadGrammar("SOME_GRAMMAR_NAME_"
                    + reader.hashCode(), "application/srgs+xml", reader);
        } catch (EngineException ex) {
            throw new NoresourceError(ex);
        } catch (EngineStateException ex) {
            throw new NoresourceError(ex);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedFormatError(ex);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        } catch (javax.speech.recognition.GrammarException ge) {
            throw new UnsupportedFormatError(ge);
        }

        return new RuleGrammarImplementation(grammar);
    }

    /**
     * Activates the given grammar.
     * @param name
     *        Name of the grammar.
     * @param activate
     *        <code>true</code> if the grammar should be activated.
     *
     * @return <code>true</code> if the grammar is active.
     * @exception BadFetchError
     *        Error creating the grammar.
     */
    private boolean activateGrammar(final String name, final boolean activate)
        throws BadFetchError {
        RuleGrammar grammar = null;
        try {
            final GrammarManager manager =
                recognizer.getGrammarManager();
            grammar = (RuleGrammar) manager.getGrammar(name);
        } catch (EngineStateException ex) {
            throw new BadFetchError(ex.getMessage(), ex);
        }
        if (grammar == null) {
            throw new BadFetchError("unable to activate unregistered grammar '"
                                    + name + "'!");
        }

        grammar.setActivatable(activate);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("grammar '" + name + "' activated: "
                         + grammar.isActive());
        }

        return grammar.isActive() == activate;
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
        throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                        (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("activating grammar '" + name + "'...");
                }

                activateGrammar(name, true);
            }
        }

        try {
            recognizer.processGrammars();
        } catch (EngineStateException ex) {
            throw new BadFetchError(ex);
        }

        try {
            recognizer.requestFocus();
        } catch (EngineStateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
        throws BadFetchError {
        if (recognizer == null) {
            return;
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                        (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("deactivating grammar '" + name + "'...");
                }

                activateGrammar(name, false);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @todo Implement this record() method.
     */
    public void record(final OutputStream out) throws NoresourceError {
        throw new NoresourceError("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }

        recognizer.requestFocus();
        try {
            recognizer.resume();
        } catch (EngineStateException esx) {
            throw new NoresourceError(esx);
        }

        currentResultListener = new JVoiceXMLRecognitionListener(this);

        recognizer.addResultListener(currentResultListener);
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        if (!recognizer.testEngineState(Recognizer.RESUMED)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("recognition not started. No need to stop.");
            }

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition...");
        }

        // If a result listener exists: Remove it.
        if (currentResultListener != null) {
            recognizer.removeResultListener(currentResultListener);
            currentResultListener = null;
        }

        try {
            recognizer.pause();
        } catch (EngineStateException ex) {
            ex.printStackTrace();
        }

        try {
            recognizer.releaseFocus();
        } catch (EngineStateException ex) {
            ex.printStackTrace();
        }

        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STOPPED);
        fireInputEvent(event);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...recognition stopped");
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
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi20";
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {
        final Collection<GrammarType> types =
                new java.util.ArrayList<GrammarType>();

        types.add(GrammarType.SRGS_XML);

        return types;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("No recognizer");
        }
        if (mediaLocator == null) {
            return null;
        }
        try {
            return new URI(mediaLocator);
        } catch (URISyntaxException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return recognizer.testEngineState(Recognizer.RESUMED);
    }

    /**
     * {@inheritDoc}
     */
    public void recognizerUpdate(final RecognizerEvent recognizerEvent) {
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
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
}
