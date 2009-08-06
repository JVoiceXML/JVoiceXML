/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.implementation.SpokenInputEvent;


/**
 * Thread waiting for DTMF input.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
class CharacterInputThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(CharacterInputThread.class);

    /** The related character input. */
    private final BufferedCharacterInput input;

    /**
     * Constructs a new object.
     * @param characterInput the related character input.
     */
    public CharacterInputThread(final BufferedCharacterInput characterInput) {
        setDaemon(true);
        setName("CharacterInput");
        input = characterInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("started DTMF recognition thread");
        }
        while (!interrupted()) {
            Character dtmf;
            try {
                dtmf = input.getNextCharacter();
                final SpokenInputEvent startedEvent =
                    new SpokenInputEvent(input, SpokenInputEvent.INPUT_STARTED);
                input.fireInputEvent(startedEvent);
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("reading DTMF interrupted", e);
                }
                return;
            }
            final CharacterInputRecognitionResult result =
                new CharacterInputRecognitionResult(dtmf.toString());
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("DTMF recognition thread terminated");
        }
    }
}
