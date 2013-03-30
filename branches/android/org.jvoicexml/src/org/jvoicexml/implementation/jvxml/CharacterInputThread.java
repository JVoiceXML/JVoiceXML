/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jvxml/CharacterInputThread.java $
 * Version: $LastChangedRevision: 2905 $
 * Date:    $Date: 2012-01-24 02:15:03 -0600 (mar, 24 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import org.apache.log4j.Logger;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.implementation.SpokenInputEvent;


/**
 * Thread waiting for DTMF input.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2905 $
 * @since 0.7
 */
class CharacterInputThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(CharacterInputThread.class);

    /** The related character input. */
    private final BufferedCharacterInput input;

    /** Reference to the current DTMF recognition properties. */
    private final DtmfRecognizerProperties props;

    /**
     * Constructs a new object.
     * @param characterInput the related character input.
     * @param dtmf DTM recognition properties
     */
    public CharacterInputThread(final BufferedCharacterInput characterInput,
            final DtmfRecognizerProperties dtmf) {
        setDaemon(true);
        setName("CharacterInput");
        input = characterInput;
        props = dtmf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("started DTMF recognition thread");
        }
        boolean sentStartedEvent = false;
        final StringBuilder utterance = new StringBuilder();
        char dtmf = 1;
        final char termchar = props.getTermchar();
        while (!isInterrupted() && dtmf != termchar) {
            try {
                dtmf = input.getNextCharacter();
                if (!sentStartedEvent) {
                    notifyStartEvent();
                    sentStartedEvent = true;
                }
                if (dtmf != termchar) {
                    utterance.append(dtmf);
                }
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("reading DTMF interrupted", e);
                }
                return;
            }
        }

        if (!isInterrupted()) {
            final String utteranceString = utterance.toString();
            notifyInput(utteranceString);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("DTMF recognition thread terminated");
        }
    }

    /**
     * Notifies all listeners that input has started.
     * @since 0.7.5
     */
    private void notifyStartEvent() {
        final SpokenInputEvent startedEvent =
                new SpokenInputEvent(input,
                        SpokenInputEvent.INPUT_STARTED);
        input.fireInputEvent(startedEvent);
    }

    /**
     * Notifies all listeners about the received input.
     * @param utterance the received input
     * @since 0.7.5
     */
    private void notifyInput(final String utterance) {
        final CharacterInputRecognitionResult result =
            new CharacterInputRecognitionResult(utterance);
        final boolean accepted = input.isAccepted(result);
        result.setAccepted(accepted);
        final SpokenInputEvent event;
        if (accepted) {
            event = new SpokenInputEvent(input,
                        SpokenInputEvent.RESULT_ACCEPTED, result);
        } else {
            event = new SpokenInputEvent(input,
                    SpokenInputEvent.RESULT_REJECTED, result);
        }
        input.fireInputEvent(event);
    }
}
