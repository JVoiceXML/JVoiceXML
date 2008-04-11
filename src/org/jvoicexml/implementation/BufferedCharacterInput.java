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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Buffered DTMF input.
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 *
 * TODO Check if this class could be replaced by other means.
 */
public final class BufferedCharacterInput
        implements CharacterInput, InputDevice, ObservableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(BufferedCharacterInput.class);

    /** All queued characters. */
    private final Queue<Character> buffer;

    /** Flag, if the recognition process started. */
    private boolean started;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listener;

    /**
     * Constructs a new object.
     */
    public BufferedCharacterInput() {
        buffer = new ConcurrentLinkedQueue<Character>();
        listener = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addCharacter(final char dtmf) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding char '" + dtmf + "'...");
        }

        buffer.add(dtmf);

        if (started) {
            final SpokenInputEvent inputStartedEvent =
                new SpokenInputEvent(this, SpokenInputEvent.INPUT_STARTED,
                        ModeType.DTMF);
            fireInputEvent(inputStartedEvent);

            final Character first = buffer.poll();
            final RecognitionResult result =
                    new CharacterInputRecognitionResult(first.toString());
            final SpokenInputEvent acceptedEvent =
                new SpokenInputEvent(this, SpokenInputEvent.RESULT_ACCEPTED,
                        result);
            fireInputEvent(acceptedEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void startRecognition()
            throws NoresourceError, BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("started recognition");
        }

        started = true;

        if (!buffer.isEmpty()) {
            final Character dtmf = buffer.poll();
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
        started = false;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopped recognition");
        }
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
        synchronized (listener) {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listener);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }
}
