/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/Jsapi10SpokenInput.java $
 * Version: $LastChangedRevision: 483 $
 * Date:    $Date: 2007-10-11 08:35:53 +0100 (Qui, 11 Out 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;
import java.util.UUID;

import javax.speech.AudioException;
import javax.speech.EngineManager;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableUserInput;
import org.jvoicexml.implementation.UserInputListener;
import org.apache.log4j.Logger;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;
import java.net.URI;
import java.net.URISyntaxException;
import javax.speech.recognition.RecognizerListener;
import javax.speech.recognition.RecognizerEvent;

/**
 * Audio input that uses the JSAPI 2.0 to address the recognition engine.
 *
 * <p>
 * Handle all JSAPI calls to the recognizer to make JSAPI transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 483 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Jsapi20SpokenInput implements SpokenInput,
        ObservableUserInput, RecognizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi20SpokenInput.class);

    /** The speech recognizer. */
    private Recognizer recognizer;

    /** Listener for user input events. */
    private final Collection<UserInputListener> listeners;

    /** The default recognizer mode descriptor. */
    private final RecognizerMode desc;

    private final String mediaLocator;
    private final String asrMediaLocator;

    private JVoiceXMLRecognitionListener currentResultListener;


    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     */
    public Jsapi20SpokenInput(final RecognizerMode defaultDescriptor,
                              final String mediaLocator) {
        desc = defaultDescriptor;
        this.mediaLocator = mediaLocator;
        String asrML = "";
        listeners = new java.util.ArrayList<UserInputListener>();

        if (mediaLocator != null) {
            try {
                URI uri = new URI(mediaLocator);
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
                        }
                        else {
                            if (newParameters.equals("")) {
                                newParameters += "?";
                            }
                            else {
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
                ex.printStackTrace();
            }
        }

        asrMediaLocator = asrML;
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
                    recognizer.getAudioManager().setMediaLocator(
                            asrMediaLocator);
                    recognizer.allocate();
                    recognizer.setSpeechEventExecutor(new SynchronousSpeechEventExecutor());
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
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio input...");
            LOGGER.debug("deallocating recognizer...");
        }

        try {
            recognizer.deallocate();
        } catch (EngineStateException ex) {
        } catch (AudioException ex) {
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
    public void addUserInputListener(final UserInputListener inputListener) {
        synchronized (listeners) {
            listeners.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeUserInputListener(final UserInputListener inputListener) {
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
    public GrammarImplementation<RuleGrammar> newGrammar(final GrammarType type) throws
            NoresourceError, UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new empty grammar");
        }

        final String name = UUID.randomUUID().toString();
        RuleGrammar ruleGrammar = null;
        try {
            ruleGrammar = recognizer.createRuleGrammar(name, null);
        } catch (EngineException ex) {
        } catch (EngineStateException ex) {
        } catch (IllegalArgumentException ex) {
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
            grammar = recognizer.loadRuleGrammar("SOME_GRAMMAR_NAME_" +
                                                 reader.hashCode(), reader);
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
    private boolean activateGrammar(final String name, final boolean activate) throws
            BadFetchError {
        RuleGrammar grammar = null;
        try {
            grammar = recognizer.getRuleGrammar(name);
        } catch (EngineStateException ex) {
        }
        if (grammar == null) {
            throw new BadFetchError("unable to activate unregistered grammar '"
                                    + name + "'!");
        }

        grammar.setEnabled(activate);

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
            final Collection<GrammarImplementation<? extends Object>> grammars) throws
            BadFetchError, UnsupportedLanguageError, NoresourceError {
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
        } catch (EngineStateException ex1) {
            ex1.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars) throws
            BadFetchError {
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

        try {
            recognizer.resume();
        } catch (EngineStateException esx) {
            throw new NoresourceError(esx);
        }

        final JVoiceXMLRecognitionListener recognitionListener =
                new JVoiceXMLRecognitionListener(listeners);

        recognizer.addResultListener(recognitionListener);

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

        synchronized (listeners) {
            final Collection<UserInputListener> copy =
            new java.util.ArrayList<UserInputListener>();
            copy.addAll(listeners);
            for (UserInputListener current : copy) {
                current.recognitionStopped();
            }
        }

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

    public URI getUriForNextSpokenInput() throws NoresourceError {
        if (recognizer != null) {
            try {
                URI uri = new URI(mediaLocator);
                return uri;
            } catch (URISyntaxException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return recognizer.testEngineState(Recognizer.FOCUSED);
    }

    public void recognizerUpdate(RecognizerEvent recognizerEvent) {
    }

}
