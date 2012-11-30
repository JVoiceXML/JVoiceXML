/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
 * Result of a recognition process. used only internally to transfer the data
 * from the C++ part to Java.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class RecognitionResult {
    /** Recognition was successful <code>S_OK</code>. */
    public static final int RECOGNITION_SUCCESSFULL = 0;
    /**
     * Recognition did not match a grammar
     * <code>SPEVENTENUM::SPEI_FALSE_RECOGNITION</code>.
     */
    public static final int RECOGNITION_NOMATCH = 43;
    /**
     * Recognition process was aborted because of an error <code>S_FALSE</code>.
     */
    public static final int RECOGNITION_ABORTED = 1;

    private int status;
    private String sml;

    /**
     * Constructs a new object.
     */
    public RecognitionResult(final int statusCode, final String mssml) {
        status = statusCode;
        sml = mssml;
    }

    public int getStatus() {
        return status;
    }
    
    public String getSml() {
        return sml;
    }
}
