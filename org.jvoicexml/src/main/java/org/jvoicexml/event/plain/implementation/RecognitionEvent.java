/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.implementation;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.plain.jvxml.InputEvent;
import org.jvoicexml.implementation.SpokenInput;

/**
 * The user has responded within the timeout interval.
 *
 * @author Dirk Schnelle-Walka
 */
@SuppressWarnings("serial")
public final class RecognitionEvent
        extends SpokenInputEvent implements InputEvent {

    /** The detailing part. */
    public static final String DETAIL = "accepted";

    /** The result of the recognition process. */
    private final RecognitionResult result;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @param input
     *            the input initiating this event
     * @param sessionId
     *            the session id
     * @param recognitionResult
     *        Result of the recognition process.
     */
    public RecognitionEvent(final SpokenInput input,
            final SessionIdentifier sessionId, final RecognitionResult recognitionResult) {
        super(input, DETAIL, sessionId);

        if (recognitionResult == null) {
            throw new IllegalArgumentException(
                    "recognition result must not be null!");
        }
        result = recognitionResult;
    }

    /**
     * Retrieves the result of the recognition process.
     *
     * @return RecognitionResult
     */
    public RecognitionResult getRecognitionResult() {
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInputResult() {
        return result;
    }
}
