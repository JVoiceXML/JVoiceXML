/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation;

import org.jvoicexml.event.ErrorEvent;

/**
 * Listener for events from the {@link Telephony} implementation.
 * implementation.
 *
 * @author Hugo Monteiro
 * @author Dirk Schnelle-Walka
 * @author Renato Cassaca
 * @since 0.6
 */
public interface TelephonyListener {
    /**
     * Invoked when the {@link Telephony} implementation triggered a call
     * answered event.
     * 
     * @param event
     *            the event.
     */
    void telephonyCallAnswered(TelephonyEvent event);

    /**
     * Invoked when the {@link Telephony} implementation triggered an event that
     * was associated with media. Examples include start and end of a playback.
     * 
     * @param event
     *            the event.
     */
    void telephonyMediaEvent(TelephonyEvent event);

    /**
     * Invoked when the {@link Telephony} implementation recognized a DTMF
     * input.
     * 
     * @param dtmf
     *            the DTMF character
     * @since 0.7.8
     */
    void dtmfInput(char dtmf);

    /**
     * Invoked when the {@link Telephony} implementation triggered a call hangup
     * event.
     * 
     * @param event
     *            the event.
     */
    void telephonyCallHungup(TelephonyEvent event);

    /**
     * Invoked when the {@link Telephony} implementation triggered a transfer
     * event.
     * 
     * @param event
     *            the event.
     */
    void telephonyCallTransferred(TelephonyEvent event);

    /**
     * An error occurred while communicating over the telephony.
     * <p>
     * This method is intended to feed back errors that happen while the
     * {@link org.jvoicexml.CallControl} processes an output asynchronously.
     * </p>
     * 
     * @param error
     *            the error
     * @since 0.7.4
     */
    void telephonyError(ErrorEvent error);
}
