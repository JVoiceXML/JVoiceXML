/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/event/error/BadFetchHttpResponsecodeError.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A <code>BadFetchProtocolResponsecodeError</code> dedicated to the
 * <code>http</code> protocol.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 */
public class BadFetchHttpResponsecodeError
        extends BadFetchProtocolResponsecodeError {
    /** The serial version UID. */
    private static final long serialVersionUID = -3073151729317425333L;

    /** The fixed HTTP protocol. */
    public static final String HTTP_PROTOCOL = "http";

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * <p>
     * The <code>responseCode</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param responseCode
     *        The protocol-specific response code.
     */
    public BadFetchHttpResponsecodeError(final int responseCode) {
        super(HTTP_PROTOCOL, responseCode);
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;getEventType()&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * <p>
     * The <code>responseCode</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param responseCode
     *        The protocol-specific response code.
     * @param message
     *        The detail message.
     */
    public BadFetchHttpResponsecodeError(final int responseCode,
            final String message) {
        super(HTTP_PROTOCOL, responseCode, message);
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * <p>
     * The <code>responseCode</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param responseCode
     *        The protocol-specific response code.
     * @param cause
     *        The cause.
     */
    public BadFetchHttpResponsecodeError(final int responseCode,
            final Throwable cause) {
        super(HTTP_PROTOCOL, responseCode, cause);
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * <p>
     * The <code>responseCode</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param responseCode
     *        The protocol-specific response code.
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public BadFetchHttpResponsecodeError(final int responseCode,
            final String message, final Throwable cause) {
        super(HTTP_PROTOCOL, responseCode, message, cause);
    }
}
