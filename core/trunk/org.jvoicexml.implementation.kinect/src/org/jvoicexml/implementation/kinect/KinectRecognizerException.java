/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.kinect/src/org/jvoicexml/implementation/kinect/KinectRecognizerException.java $
 * Version: $LastChangedRevision: 3346 $
 * Date:    $Date: 2012-11-28 15:50:05 +0100 (Mi, 28 Nov 2012) $
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

package org.jvoicexml.implementation.kinect;

/**
 * Error in the Kinect recognizer.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3346 $
 * @since 0.7.6
 */
public final class KinectRecognizerException extends Exception {
    /**
     * Constructs a new exception without any detail message.
     */
    public KinectRecognizerException() {
    }

    /**
     * Constructs a new exception with the given detail error message.
     * @param message the detail error message
     */
    public KinectRecognizerException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given cause of the exception.
     * @param cause cause of this exception
     */
    public KinectRecognizerException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the given detail and cause of the
     * exception.
     * @param message the detail error message
     * @param cause cause of this exception
     */
    public KinectRecognizerException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
