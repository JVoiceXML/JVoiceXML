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

package org.jvoicexml.implementation;

/**
 * Event generated from the {@link SynthesizedOutput} implementation.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public class SynthesizedOutputEvent {
    /** An output has been started. */
    public static final int OUTPUT_STARTED = 1;

    /**
     * An output has ended.
     * <p>
     * After this event has been issued, JVoiceXML will start the corresponding
     * timers of the speakable.
     * </p>
     */
    public static final int OUTPUT_ENDED = OUTPUT_STARTED << 1;

    /** The output has reached a marker. */
    public static final int MARKER_REACHED = OUTPUT_ENDED << 1;

    /**
      The output queue is empty. */
    public static final int QUEUE_EMPTY = MARKER_REACHED << 1;

    /** The output queue was updated. */
    public static final int OUTPUT_UPDATE = QUEUE_EMPTY << 1;

    /** Object that caused the event. */
    private final ObservableSynthesizedOutput source;

    /** Event identifier. */
    private final int event;

    /**
     * Constructs a new object.
     * @param output object that caused the event.
     * @param eventType event identifier.
     */
    public SynthesizedOutputEvent(final ObservableSynthesizedOutput output,
            final int eventType) {
        source = output;
        event = eventType;
    }

    /**
     * Retrieves the object that caused the event.
     * @return the source object.
     */
    public final ObservableSynthesizedOutput getSource() {
        return source;
    }

    /**
     * Retrieves the event type.
     * @return the event type.
     */
    public final int getEvent() {
        return event;
    }
}
