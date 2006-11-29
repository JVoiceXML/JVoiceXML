/*
 * File:    $RCSfile: NoresourceError.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A run-time error occurred because a requested platform resource was not
 * available during execution.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public class NoresourceError
        extends ErrorEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 7414023146325970737L;

    /** The detail message. */
    public static final String EVENT_TYPE = "error.noresource";

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @see #getEventType()
     */
    public NoresourceError() {
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
    public NoresourceError(final String message) {
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
    public NoresourceError(final Throwable cause) {
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
    public NoresourceError(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getEventType() {
        return EVENT_TYPE;
    }
}
