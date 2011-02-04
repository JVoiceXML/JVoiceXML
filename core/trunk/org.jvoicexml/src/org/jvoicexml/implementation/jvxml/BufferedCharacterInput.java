/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *K
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

package org.jvoicexml.implementation.jvxml;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.InputDevice;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;

/**
 * Buffered DTMF input.
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 *
 *
 * @since 0.5
 */
public final class BufferedCharacterInput
        implements CharacterInput, InputDevice, ObservableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(BufferedCharacterInput.class);

    /** All queued characters. */
    private final BlockingQueue<Character> buffer;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listener;

    /** Active grammars. */
    private final Collection<GrammarImplementation<?>> activeGrammars;

    /** The thread reading the dtmf sequences. */
    private Thread inputThread;

    /** Thread to monitor the inter digit timeout. */
    private InterdigitTimeoutThread interDigitTimeout;

    /** Reference to the current DTMF recognition properties. */
    private DtmfRecognizerProperties props;

    /**
     * Constructs a new object.
     */
    public BufferedCharacterInput() {
        buffer = new java.util.concurrent.LinkedBlockingQueue<Character>();
        listener = new java.util.ArrayList<SpokenInputListener>();
        activeGrammars = new java.util.ArrayList<GrammarImplementation<?>>();
    }

    /**
     * Activates the given grammars. It is guaranteed that all grammars types
     * are supported by this implementation.
     *
     * @param grammars
     *        Grammars to activate.
     * @exception BadFetchError
     *            Grammar is not know by the recognizer.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     * @since 0.7
     */
    void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        activeGrammars.addAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                LOGGER.debug("activated DTMF grammar " + grammar.getGrammar());
            }
        }
    }

    /**
     * Deactivates the given grammar. Do nothing if the input resource is not
     * available. It is guaranteed that all grammars types are supported by this
     * implementation.
     *
     * @param grammars
     *        Grammars to deactivate.
     *
     * @exception BadFetchError
     *            Grammar is not known by the recognizer.
     * @exception NoresourceError
     *            The input resource is not available.
     * @since 0.7
     */
    void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        activeGrammars.removeAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                LOGGER.debug("deactivated DTMF grammar "
                        + grammar.getGrammar());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addCharacter(final char dtmf)
        throws IllegalArgumentException {
        if ("01234567890#*".indexOf(Character.toString(dtmf)) < 0) {
            throw new IllegalArgumentException("'" + dtmf
                    + "' is not one of 0123456789#* ");
        }
        buffer.add(dtmf);
        final char termchar = props.getTermchar();
        if (dtmf == termchar) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added char '" + dtmf + "' buffer is now '"
                    + buffer.toString() + "'");
        }
        if (interDigitTimeout == null) {
            final long interdigittimeout = props.getInterdigittimeoutAsMsec();
            interDigitTimeout = new InterdigitTimeoutThread(this,
                    interdigittimeout, termchar);
            interDigitTimeout.start();
        } else {
            interDigitTimeout.enteredDigit();
        }
    }

    /**
     * Reads the next character. If no character is available this methods waits
     * for the next character.
     * @return next character.
     * @throws InterruptedException
     *         waiting interrupted.
     * @since 0.7
     */
    char getNextCharacter() throws InterruptedException {
        return buffer.take();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void startRecognition(
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
            throws NoresourceError, BadFetchError {
        props = dtmf;
        inputThread = new CharacterInputThread(this, props);
        inputThread.start();
        LOGGER.info("started DTMF recognition");
    }

    /**
     * Checks if one of the active grammars accepts the current recognition
     * result.
     * @param result the recognized DTMF result
     * @return <code>true</code> if the result is accepted.
     * @since 0.7
     */
    boolean isAccepted(final RecognitionResult result) {
        for (GrammarImplementation<?> grammar : activeGrammars) {
            if (grammar.accepts(result)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        if (inputThread != null) {
            inputThread.interrupt();
            inputThread = null;
        }
        if (interDigitTimeout != null) {
            interDigitTimeout.interrupt();
            interDigitTimeout = null;
        }
        props = null;
        LOGGER.info("stopped DTMF recognition");
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.remove(inputListener);
        }
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) {
        final Collection<SpokenInputListener> copy =
            new java.util.ArrayList<SpokenInputListener>();
        synchronized (listener) {
            copy.addAll(listener);
        }
        for (SpokenInputListener current : copy) {
            current.inputStatusChanged(event);
        }
    }
}
