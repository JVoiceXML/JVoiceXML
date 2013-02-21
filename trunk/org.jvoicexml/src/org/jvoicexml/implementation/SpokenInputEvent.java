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
 * Event generated from the {@link SpokenInput} implementation.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 *
 */
public final class SpokenInputEvent {
    /** The recognition process has been started. */
    public static final int RECOGNITION_STARTED = 1;

    /** The recognition process has ended. */
    public static final int RECOGNITION_STOPPED = RECOGNITION_STARTED << 1;

    /** The user has started to speak. */
    public static final int INPUT_STARTED = RECOGNITION_STOPPED << 1;

    /** The user made an utterance that matched an active grammar. */
    public static final int RESULT_ACCEPTED = INPUT_STARTED << 1;

    /** The user made an utterance that did not match an active grammar. */
    public static final int RESULT_REJECTED = RESULT_ACCEPTED << 1;

    /** Object that caused the event. */
    private final ObservableSpokenInput source;

    /** Event identifier. */
    private final int event;

    /** Optional parameter. */
    private final Object param;

    /**
     * Constructs a new object.
     * @param input object that caused the event.
     * @param eventType event identifier.
     */
    public SpokenInputEvent(final ObservableSpokenInput input,
            final int eventType) {
        this(input, eventType, null);
    }

    /**
     * Constructs a new object.
     * @param input object that caused the event.
     * @param eventType event identifier.
     * @param parameter optional parameter.
     */
    public SpokenInputEvent(final ObservableSpokenInput input,
            final int eventType, final Object parameter) {
        source = input;
        event = eventType;
        param = parameter;
    }

    /**
     * Retrieves the object that caused the event.
     * @return the source object.
     */
    public ObservableSpokenInput getSource() {
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

    /**
     * {@inheritDoc}
     * @since 0.7
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getName());
        str.append('[');
        switch(event) {
        case INPUT_STARTED:
            str.append("INPUT_STARTED");
            break;
        case RECOGNITION_STARTED:
            str.append("RECOGNITION_STARTED");
            break;
        case RECOGNITION_STOPPED:
            str.append("RECOGNITION_STOPPED");
            break;
        case RESULT_ACCEPTED:
            str.append("RESULT_ACCEPTED");
            break;
        case RESULT_REJECTED:
            str.append("RESULT_REJECTED");
            break;
        default:
            str.append(event);
        }
        if (param != null) {
            str.append(',');
            str.append(param);
        }
        str.append(']');
        return str.toString();
    }
}
