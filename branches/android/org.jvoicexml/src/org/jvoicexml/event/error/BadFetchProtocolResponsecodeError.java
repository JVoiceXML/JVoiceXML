/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/event/error/BadFetchProtocolResponsecodeError.java $
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
 * In the case of a fetch failure, the interpreter context must use a detailed
 * event type telling which specific HTTP or other protocol-specific response
 * code was encountered. The value of the response code for HTTP is defined in
 * <a href="http://www.w3.org/TR/voicexml20/#dml5.2.6">[RFC2616]</a>. This
 * allows applications to differentially treat a missing document from a
 * prohibited document, for instance. The value of the response code for other
 * protocols (such as HTTPS, RTSP, and so on) is dependent upon the protocol.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 */
public class BadFetchProtocolResponsecodeError
        extends BadFetchError {
    /** The serial version UID. */
    private static final long serialVersionUID = 8246019001033388406L;

    /** The used protocol. */
    private final String protocol;

    /** The protocol-specific response code. */
    private final int responseCode;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * <p>
     * The <code>usedProtocol</code> and the <code>detectedResponseCode</code>
     * are used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param detectedResponseCode
     *        The protocol-specific response code.
     */
    public BadFetchProtocolResponsecodeError(final String usedProtocol,
            final int detectedResponseCode) {
        super();

        protocol = usedProtocol;
        responseCode = detectedResponseCode;
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;getEventType()&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * <p>
     * The <code>usedProtocol</code> and the <code>detectedResponseCode</code>
     * are used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param detectedResponseCode
     *        The protocol-specific response code.
     * @param message
     *        The detail message.
     *
     */
    public BadFetchProtocolResponsecodeError(final String usedProtocol,
            final int detectedResponseCode, final String message) {
        super(message);

        protocol = usedProtocol;
        responseCode = detectedResponseCode;
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * <p>
     * The <code>usedProtocol</code> and the <code>detectedResponseCode</code>
     * are used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param detectedResponseCode
     *        The protocol-specific response code.
     * @param cause
     *        The cause.
     */
    public BadFetchProtocolResponsecodeError(final String usedProtocol,
            final int detectedResponseCode, final Throwable cause) {
        super(cause);

        protocol = usedProtocol;
        responseCode = detectedResponseCode;
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * <p>
     * The <code>usedProtocol</code> and the <code>detectedResponseCode</code>
     * are used to construct the event type.
     * </p>
     *
     * @see #getEventType()
     *
     * @param usedProtocol
     *        The used protocol.
     * @param detectedResponseCode
     *        The protocol-specific response code.
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public BadFetchProtocolResponsecodeError(final String usedProtocol,
            final int detectedResponseCode, final String message,
            final Throwable cause) {
        super(message, cause);

        protocol = usedProtocol;
        responseCode = detectedResponseCode;
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
     * Retrieve the protocol-specific response code.
     *
     * @return Protocol-specific response code.
     *
     * @see #getProtocol()
     */
    public final int getResponseCode() {
        return responseCode;
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
        str.append('.');
        str.append(responseCode);
    }

}
