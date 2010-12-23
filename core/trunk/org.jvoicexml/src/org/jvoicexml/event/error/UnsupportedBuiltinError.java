/*
 * File:    $RCSfile: UnsupportedBuiltinError.java,v $
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
 * The platform does not support a requested builtin type/grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public class UnsupportedBuiltinError
        extends UnsupportedElementError {
    /** The serial version UID. */
    private static final long serialVersionUID = -4527038478843916684L;

    /** The element. */
    public static final String ELEMENT = "builtin";

    /**
     * Constructs a new UnsupportedBuiltinError with the default
     * detail message. The cause is not initialized.
     */
    public UnsupportedBuiltinError() {
        super(ELEMENT);
    }

    /**
     * Constructs a new UnsupportedBuiltinError with the specified
     * cause and the default detail message.
     * @param cause The cause.
     */
    public UnsupportedBuiltinError(final Throwable cause) {
        super(ELEMENT, cause);
    }
}
