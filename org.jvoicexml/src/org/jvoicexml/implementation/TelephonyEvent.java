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
 * Event generated from the {@link Telephony} implementation.
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
public final class TelephonyEvent {
    /** A call has been answered. */
    public static final int ANSWERED = 1;

    /** A hangup occured. */
    public static final int HUNGUP = ANSWERED << 1;

    /** A play has been started. */
    public static final int PLAY_STARTED = HUNGUP << 1;

    /** A play has been stopped. */
    public static final int PLAY_STOPPED = PLAY_STARTED << 1;

    /** A record has been started. */
    public static final int RECORD_STARTED = PLAY_STOPPED << 1;

    /** A record has been stopped. */
    public static final int RECORD_STOPPED = RECORD_STARTED << 1;

    /** A call has been transferred. */
    public static final int TRANSFERRED = RECORD_STOPPED << 1;

    /** Object that caused the event. */
    private final ObservableTelephony source;

    /** Event identifier. */
    private final int event;

    /** Optional parameter. */
    private final Object param;


    /**
     * Constructs a new object.
     * @param telephony object that caused the event.
     * @param eventType event identifier.
     */
    public TelephonyEvent(final ObservableTelephony telephony,
            final int eventType) {
        this(telephony, eventType, null);
    }

    /**
     * Constructs a new object.
     * @param telephony object that caused the event.
     * @param eventType event identifier.
     * @param parameter optional parameter.
     */
    public TelephonyEvent(final ObservableTelephony telephony,
            final int eventType, final Object parameter) {
        source = telephony;
        event = eventType;
        param = parameter;
    }

    /**
     * Retrieves the object that caused the event.
     * @return the source object.
     */
    public ObservableTelephony getSource() {
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
