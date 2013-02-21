/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/event/error/BadFetchError.java $
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

import org.jvoicexml.event.ErrorEvent;

/**
 * The interpreter context throws this event when a fetch of a document has
 * failed and the interpreter context has reached a place in the document
 * interpretation where the fetch result is required. Fetch failures result from
 * unsupported scheme references, malformed URIs, client aborts, communication
 * errors, timeouts, security violations, unsupported resource types, resource
 * type mismatches, document parse errors, and a variety of errors represented
 * by scheme-specific error codes.
 *
 * <p>
 * If the interpreter context has speculatively prefetched a document and that
 * document turns out not to be needed, error.badfetch is not thrown. Likewise
 * if the fetch of an <code>&lt;audio&gt;</code> document fails and if there
 * is a nested alternate <code>&lt;audio&gt;</code> document whose fetch then
 * succeeds, or if there is nested alternate text, no
 * <code>error.badfetch</code> occurs.<br>
 * When an interpreter context is transitioning to a new document, the
 * interpreter context throws <code>error.badfetch</code> on an error until
 * the interpreter is capable of executing the new document, but again only at
 * the point in time where the new document is actually needed, not before.
 * Whether or not variable initialization is considered part of executing the
 * new document is platform-dependent.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 2476 $
 */
public class BadFetchError
        extends ErrorEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -4235595186877931639L;

    /** The detail message. */
    public static final String EVENT_TYPE = "error.badfetch";

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @see #getEventType()
     */
    public BadFetchError() {
        super();
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * @param message
     *        The detail message.
     *
     * @see #getEventType()
     */
    public BadFetchError(final String message) {
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
    public BadFetchError(final Throwable cause) {
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
    public BadFetchError(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Appends detail information to the type information.
     * @param str type prefix.
     */
    protected void appendSpecificationDetails(final StringBuilder str) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getEventType() {
        final StringBuilder str = new StringBuilder();

        str.append(EVENT_TYPE);
        appendSpecificationDetails(str);

        return str.toString();
    }
}
