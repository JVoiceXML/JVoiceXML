/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event;

/**
 * Base class for all events.
 *
 * <p>
 * The platform throws events when the user does not respond, doesn't respond in
 * a way that the application understands, requests help, etc. The interpreter
 * throws events if it finds a semantic error in a VoiceXML document, or when it
 * encounters a <code>&lt;throw&gt;</code> element. Events are identified by
 * character strings.
 * </p>
 *
 * <p>
 * Events are subdivided into plain events (things that happen normally), and
 * error events (abnormal occurrences).
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 */
@SuppressWarnings("serial")
public abstract class JVoiceXMLEvent
        extends Throwable {
    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @see #getEventType()
     */
    public JVoiceXMLEvent() {
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt;>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * @param message
     *        The detail message.
     *
     * @see #getEventType()
     */
    public JVoiceXMLEvent(final String message) {
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
    public JVoiceXMLEvent(final Throwable cause) {
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
    public JVoiceXMLEvent(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Get the event type of this event.
     *
     * @return Event type.
     */
    public abstract String getEventType();

    /**
     * {@inheritDoc}
     *
     * <p>
     * Preceed the type to the original message. The returned String
     * evaluates to
     * <code>type: message</code>.
     * </p>
     */
    @Override
    public final String getMessage() {
        final StringBuilder expandedMessage = new StringBuilder();

        expandedMessage.append(getEventType());

        final String message = super.getMessage();

        if (message != null) {
            expandedMessage.append(": ");
            expandedMessage.append(message);
        }

        return expandedMessage.toString();
    }
}
