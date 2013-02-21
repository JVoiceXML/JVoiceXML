/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.callmanager;


/**
 * Error creating a new {@link org.jvoicexml.ConnectionInformation}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public class ConnectionInformationCreationException extends Exception {
    /**
     * Constructs a new event with the object type as its detail message. The
     * cause is not initialized.
     */
    public ConnectionInformationCreationException() {
    }

    /**
     * Constructs a new object with the specified detail message.
     * The cause is not initialized.
     *
     * @param message
     *        The detail message.
     */
    public ConnectionInformationCreationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new object with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * @param cause
     *        The cause.
     */
    public ConnectionInformationCreationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new object with the specified detail message and cause.
     *
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public ConnectionInformationCreationException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

}
