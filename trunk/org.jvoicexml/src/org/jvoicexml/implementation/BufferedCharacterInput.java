/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedLanguageError;

/**
 * Buffered DTMF input.
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 *
 *
 * @since 0.5
 */
public final class BufferedCharacterInput extends Thread
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

    /**
     * Constructs a new object.
     */
    public BufferedCharacterInput() {
        setDaemon(true);
        setName("CharacterInput");

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
                LOGGER.debug("activated DTMF grammar " + grammar);
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
                LOGGER.debug("deactivated DTMF grammar " + grammar);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addCharacter(final char dtmf) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding char '" + dtmf + "'...");
        }

        buffer.add(dtmf);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void startRecognition()
            throws NoresourceError, BadFetchError {
        start();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        LOGGER.info("started DTMF recognition");
        while (!interrupted()) {
            Character dtmf;
            try {
                dtmf = buffer.take();
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("reading DTMF interrupted", e);
                }
                return;
            }
            final RecognitionResult result =
                new CharacterInputRecognitionResult(dtmf.toString());
            final SpokenInputEvent acceptedEvent =
                new SpokenInputEvent(this, SpokenInputEvent.RESULT_ACCEPTED,
                        result);
            fireInputEvent(acceptedEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        interrupt();
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
    private void fireInputEvent(final SpokenInputEvent event) {
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
