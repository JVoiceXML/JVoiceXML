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

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;

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
        implements CharacterInput, ObservableUserInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(BufferedCharacterInput.class);

    /** All queued characters. */
    private final Queue<Character> buffer;

    /** Flag, if the recognition process started. */
    private boolean started;

    /** Listener for user input events. */
    private final Collection<UserInputListener> listener;

    /**
     * Constructs a new object.
     */
    public BufferedCharacterInput() {
        buffer = new ConcurrentLinkedQueue<Character>();
        listener = new java.util.ArrayList<UserInputListener>();
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
            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.inputStarted(ModeType.DTMF, BargeInType.SPEECH);
                }
            }

            final Character first = buffer.poll();
            final RecognitionResult result =
                    new CharacterInputRecognitionResult(first.toString());

            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.resultAccepted(result);
                }
            }
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

            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.resultAccepted(result);
                }
            }
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
    public void addUserInputListener(final UserInputListener inputListener) {
        synchronized (listener) {
            listener.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeUserInputListener(final UserInputListener inputListener) {
        synchronized (listener) {
            listener.remove(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client)
        throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }
}
