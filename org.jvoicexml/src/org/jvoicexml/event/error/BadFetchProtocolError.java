/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
 * In the case of a fetch failure, the interpreter context must use a detailed
 * event type telling which specific HTTP or other protocol-specific response
 * code was encountered. The value of the response code for HTTP is defined in
 * <a href="http://www.w3.org/TR/voicexml20/#dml5.2.6">[RFC2616]</a>. This
 * allows applications to differentially treat a missing document from a
 * prohibited document, for instance. The value of the response code for other
 * protocols (such as HTTPS, RTSP, and so on) is dependent upon the protocol.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public class BadFetchProtocolError
        extends BadFetchError {
    /** The used protocol. */
    private final String protocol;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * <p>
     * The <code>usedProtocol</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     */
    public BadFetchProtocolError(final String usedProtocol) {
        super();

        protocol = usedProtocol;
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;getEventType()&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * <p>
     * The <code>usedProtocol</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param message
     *        The detail message.
     *
     */
    public BadFetchProtocolError(final String usedProtocol,
            final String message) {
        super(message);

        protocol = usedProtocol;
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * <p>
     * The <code>usedProtocol</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param cause
     *        The cause.
     */
    public BadFetchProtocolError(final String usedProtocol,
            final Throwable cause) {
        super(cause);

        protocol = usedProtocol;
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * <p>
     * The <code>usedProtocol</code> is used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public BadFetchProtocolError(final String usedProtocol,
            final String message, final Throwable cause) {
        super(message, cause);

        protocol = usedProtocol;
    }

    /**
     * Retrieve the protocol.
     *
     * @return The protocol.
     */
    public final String getProtocol() {
        return protocol;
    }

    /**
     * Appends the protocol and response code to the type.
     * The event type has the following form
     *
     * <p>
     * <code>
     * &lt;BadFetchError.EVENT_TYPE&gt;.&lt;protocol&gt;.&lt;responseCode&gt;
     * </code>
     * </p>
     *
     * {@inheritDoc}
     */
    @Override
    protected final void appendSpecificationDetails(final StringBuilder str) {
        str.append('.');
        str.append(protocol);
    }
}
