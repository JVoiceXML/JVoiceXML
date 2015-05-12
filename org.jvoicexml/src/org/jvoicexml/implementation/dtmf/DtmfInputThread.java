/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.dtmf;

import org.apache.log4j.Logger;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Thread waiting for DTMF input.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
class DtmfInputThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(DtmfInputThread.class);

    /** The related character input. */
    private final BufferedDtmfInput input;

    /** Reference to the current DTMF recognition properties. */
    private final DtmfRecognizerProperties props;

    /**
     * Constructs a new object.
     * 
     * @param characterInput
     *            the related character input.
     * @param dtmf
     *            DTM recognition properties
     */
    public DtmfInputThread(final BufferedDtmfInput characterInput,
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
                    LOGGER.debug("reading DTMF interrupted");
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
     * 
     * @since 0.7.5
     */
    private void notifyStartEvent() {
        final SpokenInputEvent startedEvent = new InputStartedEvent(input,
                null, ModeType.DTMF);
        input.fireInputEvent(startedEvent);
    }

    /**
     * Notifies all listeners about the received input.
     * 
     * @param utterance
     *            the received input
     * @since 0.7.5
     */
    private void notifyInput(final String utterance) {
        final DtmfInputResult result = new DtmfInputResult(
                utterance);
        final boolean accepted = input.isAccepted(result);
        result.setAccepted(accepted);
        final SpokenInputEvent event;
        if (accepted) {
            event = new RecognitionEvent(input, null, result);
        } else {
            event = new NomatchEvent(input, null, result);
        }
        input.fireInputEvent(event);
    }
}
