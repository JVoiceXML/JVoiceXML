/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/IllegalAttributeException.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml;

/**
 * Thrown if the attributes of a tag are not specification conform. This means
 * that either a required attribute is missing or a combination of two
 * attributes is not allowed. 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 * @since 0.7.3
 */
public class IllegalAttributeException extends Exception {
    /** The serial version UID. */
    private static final long serialVersionUID = 8639093970477862123L;

    /**
     * Constructs a new exception without a root cause or message.
     */
    public IllegalAttributeException() {
        super();
    }

    /**
     * Constructs a new exception with the given root cause and message.
     * @param message the error message
     * @param cause the cause of this exception
     */
    public IllegalAttributeException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the given message.
     * @param message the error message
     */
    public IllegalAttributeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given root cause and message.
     * @param cause the cause of this exception
     */
    public IllegalAttributeException(final Throwable cause) {
        super(cause);
    }

}
