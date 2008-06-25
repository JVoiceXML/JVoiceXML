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
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SynthesizedOutputEvent {
    /** An output has been started. */
    public static final int OUTPUT_STARTED = 1;

    /** An output has ended. */
    public static final int OUTPUT_ENDED = OUTPUT_STARTED << 1;

    /** The output has reached a marker. */
    public static final int MARKER_REACHED = OUTPUT_ENDED << 1;

    /** The output queue is empty. */
    public static final int QUEUE_EMPTY = MARKER_REACHED << 1;

    /** The output queue is empty. */
    public static final int OUTPUT_UPDATE = QUEUE_EMPTY << 1;

    /** Object that caused the event. */
    private final ObservableSynthesizedOutput source;

    /** Event identifier. */
    private final int event;

    /** Optional parameter. */
    private final Object param;


    /**
     * Constructs a new object.
     * @param output object that caused the event.
     * @param eventType event identifier.
     */
    public SynthesizedOutputEvent(final ObservableSynthesizedOutput output,
            final int eventType) {
        this(output, eventType, null);
    }

    /**
     * Constructs a new object.
     * @param output object that caused the event.
     * @param eventType event identifier.
     * @param parameter optional parameter.
     */
    public SynthesizedOutputEvent(final ObservableSynthesizedOutput output,
            final int eventType, final Object parameter) {
        source = output;
        event = eventType;
        param = parameter;
    }

    /**
     * Retrieves the object that caused the event.
     * @return the source object.
     */
    public ObservableSynthesizedOutput getSource() {
        return source;
    }

    /**
     * Retrieves the event type.
     * @return the event type.
     */
    public int getEvent() {
        return event;
    }

    /**
     * Retrieves the parameter.
     * @return the parameter, maybe <code>null</code>.
     */
    public Object getParam() {
        return param;
    }
}
