/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Notification that the output queue is empty.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
@SuppressWarnings("serial")
public final class QueueEmptyEvent extends SynthesizedOutputEvent {
    /** The unsupported element. */
    public static final String DETAIL = "emptyqueue";

    /** The detail message. */
    public static final String EVENT_TYPE = SynthesizedOutputEvent.class
            .getCanonicalName() + "." + DETAIL;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized
     * 
     * <p>
     * The {@link #DETAIL} is used to construct the event type.
     * </p>
     * 
     * @see #getEventType()
     * 
     * @param output
     *            object that caused the event.
     * @param sessionId
     *            the session id
     */
    public QueueEmptyEvent(final SynthesizedOutput output,
            final SessionIdentifier sessionId) {
        super(output, DETAIL, sessionId);
    }
}
