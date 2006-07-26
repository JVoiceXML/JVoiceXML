/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java $
 * Version: $LastChangedRevision: 23 $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Buffered DTMF input.
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision: 23 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class BufferedCharacterInput
        implements CharacterInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(BufferedCharacterInput.class);

    /** All queued characters. */
    private final Queue<Character> buffer;

    /** Flag, if the recognition process started. */
    private boolean started;

    /** The listener for input events. */
    private UserInputListener inputListener;

    /**
     * Constructs a new object.
     */
    public BufferedCharacterInput() {
        buffer = new ConcurrentLinkedQueue<Character>();
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
            if (inputListener != null) {
                final Character first = buffer.poll();
                final RecognitionResult result =
                        new CharacterInputRecognitionResult(first.toString());

                inputListener.resultAccepted(result);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void startRecognition()
            throws NoresourceError, BadFetchError {
        started = true;

        if (!buffer.isEmpty()) {
            if (inputListener != null) {
                final Character dtmf = buffer.poll();
                final RecognitionResult result =
                        new CharacterInputRecognitionResult(dtmf.toString());

                inputListener.resultAccepted(result);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        started = false;
    }

    /**
     * {@inheritDoc}
     */
    public void setUserInputListener(final UserInputListener listener) {
        inputListener = listener;
    }
}
