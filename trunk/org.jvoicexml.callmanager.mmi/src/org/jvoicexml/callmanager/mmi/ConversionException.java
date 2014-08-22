/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.callmanager.mmi/src/org/jvoicexml/callmanager/mmi/MMIMessageException.java $
 * Version: $LastChangedRevision: 3950 $
 * Date:    $Date: 2013-11-23 21:32:07 +0100 (Sat, 23 Nov 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

/**
 * Error converting an extension notification.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3950 $
 * @since 0.7.7
 */
public class ConversionException extends Exception {
    /** The serial version UID. */
    private static final long serialVersionUID = 6402894946841631760L;

    /**
     * Constructs a new object with the given detail message.
     * @param message the detail message.
     */
    public ConversionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new object with the given cause.
     * @param cause the cause for this exception.
     */
    public ConversionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new object with the given detail message and cause.
     * @param message the detail message.
     * @param cause the cause for this exception.
     */
    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
