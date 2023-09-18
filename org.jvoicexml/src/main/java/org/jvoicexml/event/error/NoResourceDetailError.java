/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2022 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.event.error;

/**
 * An {@code error.noresource} with specific detail information.
.* @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class NoResourceDetailError extends NoresourceError {
    /** The serial version UID. */
    private static final long serialVersionUID = 6456139552876751394L;
    /** The detail message. */
    private final String detail;
    
    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @param det the specifi detail message
     * @see #getEventType()
     */
    public NoResourceDetailError(final String det) {
        detail = det;
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt;: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * @param message
     *        The detail message.
     * @param det the specific detail message
     *
     * @see #getEventType()
     */
    public NoResourceDetailError(final String det, final String message) {
        super(message);
        detail = det;
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * @param cause
     *        The cause.
     * @param det the specific detail message
     *
     * @see #getEventType()
     */
    public NoResourceDetailError(final String det, final Throwable cause) {
        super(cause);
        detail = det;
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * @param message
     *        The detail message.
     * @param det the specific detail message
     * @param cause
     *        The cause.
     */
    public NoResourceDetailError(final String det, final String message, final Throwable cause) {
        super(message, cause);
        detail = det;
    }

    /**
     * Appends the protocol and response code to the type.
     * The event type has the following form
     *
     * <p>
     * <code>
     * &lt;NoResoureError.EVENT_TYPE&gt;.&lt;detail&gt;.
     * </code>
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    protected final void appendSpecificationDetails(final StringBuilder str) {
        str.append('.');
        str.append(detail);
    }

}
