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

package org.jvoicexml.event.error;

/**
 * The platform does not support the language for either speech synthesis or
 * speech recognition.
 *
 * @author Dirk Schnelle-Walka
 */
public class UnsupportedLanguageError
        extends UnsupportedElementError {
    /** The serial version UID. */
    private static final long serialVersionUID = 2540390073316107161L;

    /** The unsupported element. */
    public static final String ELEMENT = "language";

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized
     *
     * <p>
     * The <code>ELEMENT</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     * @see #ELEMENT
     */
    public UnsupportedLanguageError() {
        super(ELEMENT);
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt;: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * <p>
     * The <code>ELEMENT</code> is used to construct the event type.
     * </p>
     *
     * @param message
     *        The detail message.
     *
     * @see #getEventType()
     * @see #ELEMENT
     */
    public UnsupportedLanguageError(final String message) {
        super(ELEMENT, message);
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * <p>
     * The <code>ELEMENT</code> is used to construct the event type.
     * </p>
     *
     * @param cause
     *        The cause.
     *
     * @see #getEventType()
     * @see #ELEMENT
     */
    public UnsupportedLanguageError(final Throwable cause) {
        super(ELEMENT, cause);
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * <p>
     * The <code>ELEMENT</code> is used to construct the event type.
     * </p>
     *
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     *
     * @see #getEventType()
     * @see #ELEMENT
     */
    public UnsupportedLanguageError(final String message,
                                    final Throwable cause) {
        super(ELEMENT, message, cause);
    }
}
