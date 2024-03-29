/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain;

/**
 * The user has been transferred unconditionally to another line and will not
 * return.
 *
 * @author Dirk Schnelle-Walka
 */
public class ConnectionDisconnectTransferEvent
        extends ConnectionDisconnectEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 5858887089917522537L;

    /** The detail message. */
    public static final String DETAIL = "transfer";

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @see #getEventType()
     */
    public ConnectionDisconnectTransferEvent() {
        super();
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt;: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * @param message
     *        The detail message.
     *
     * @see #getEventType()
     */
    public ConnectionDisconnectTransferEvent(final String message) {
        super(message);
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * @param cause
     *        The cause.
     *
     * @see #getEventType()
     */
    public ConnectionDisconnectTransferEvent(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public ConnectionDisconnectTransferEvent(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * Appends detail information to the type information.
     * @param str type prefix.
     */
    @Override
    protected void appendSpecificationDetails(final StringBuilder str) {
        str.append('.');
        str.append(DETAIL);
    }
}
