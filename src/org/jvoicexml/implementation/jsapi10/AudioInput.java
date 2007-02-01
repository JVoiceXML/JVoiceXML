/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.jsapi10;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.UserInputListener;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Audio input that uses the JSAPI 1.0 to address the recognition engine.
 *
 * <p>
 * Handle all JSAPI calls to the recognizer to make JSAPI transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class AudioInput
        implements SpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AudioInput.class);

    /** The speech recognizer. */
    private Recognizer recognizer;

    /** Listener for user input events. */
    private UserInputListener listener;

    /** The default recognizer mode descriptor. */
    private final RecognizerModeDesc desc;

    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     */
    public AudioInput(final RecognizerModeDesc defaultDescriptor) {
        desc = defaultDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            recognizer = Central.createRecognizer(desc);
            if (recognizer != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("allocating recognizer...");
                }

                recognizer.allocate();
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
    public void setUserInputListener(final UserInputListener inputListener) {
        listener = inputListener;
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
    public RuleGrammar newGrammar(final String name)
            throws NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new empty grammar");
        }

        return recognizer.newRuleGrammar(name);
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar loadGrammar(final Reader reader)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        final RuleGrammar grammar;

        try {
            grammar = recognizer.loadJSGF(reader);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        } catch (javax.speech.recognition.GrammarException ge) {
            throw new UnsupportedFormatError(ge);
        }

        return grammar;
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
        final RuleGrammar grammar = recognizer.getRuleGrammar(name);
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
    public void activateGrammars(final Collection<RuleGrammar> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        for (RuleGrammar grammar : grammars) {
            final String name = grammar.getName();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("activating grammar '" + name + "'...");
            }

            activateGrammar(name, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(final Collection<RuleGrammar> grammars)
            throws BadFetchError {
        if (recognizer == null) {
            return;
        }

        for (RuleGrammar grammar : grammars) {
            final String name = grammar.getName();
            activateGrammar(name, false);
        }
    }

    /**
     * {@inheritDoc}
     * @todo Implement this record() method.
     */
    public void record(final OutputStream out)
            throws NoresourceError {
        throw new NoresourceError("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition()
            throws NoresourceError, BadFetchError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }

        try {
            recognizer.commitChanges();
        } catch (GrammarException ge) {
            throw new BadFetchError(ge);
        }

        recognizer.requestFocus();
        try {
            recognizer.resume();
        } catch (AudioException ae) {
            throw new NoresourceError(ae);
        }

        final JVoiceXMLRecognitionListener recognitionListener =
                new JVoiceXMLRecognitionListener(listener);

        recognitionListener.start();
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

        recognizer.pause();
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
     *
     * @todo implement this method.
     */
    public void connect(final RemoteClient remoteclient)
        throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10";
    }
}
