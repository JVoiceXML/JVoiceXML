/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * The user input something, but it was not recognized.
 * 
 * @author Dirk Schnelle-Walka
 */
public final class NomatchEvent extends SpokenInputEvent implements InputEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 776343576316001103L;

    /** The detail message. */
    public static final String EVENT_TYPE = "nomatch";

    /** The result of the recognition process. */
    private final RecognitionResult result;

    /**
     * Constructs a new event with the specified detail message and cause.
     * 
     * @param input
     *            the input initiating this event
     * @param sessionId
     *            the session id
     * @param recognitionResult
     *            the recognition result
     */
    public NomatchEvent(final SpokenInput input,
            final SessionIdentifier sessionId,
            final RecognitionResult recognitionResult) {
        super(input, sessionId);
        result = recognitionResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the result of the recognition process.
     * 
     * @return RecognitionResult
     * @since 0.7.7
     */
    public RecognitionResult getRecognitionResult() {
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  Object getInputResult() {
        return result;
    }
}
