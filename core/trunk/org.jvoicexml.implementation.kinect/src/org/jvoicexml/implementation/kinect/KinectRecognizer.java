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

import org.jvoicexml.event.error.BadFetchError;

/**
 * A Kinect Recognizer.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 *
 */
public final class KinectRecognizer {
    /** Kinect recognizer Handle. **/
    private long handle;
    
    /**
     * Allocates this recognizer.
     * @throws BadFetchError
     *         error allocating the recognizer.
     */
    public void allocate() throws BadFetchError {
        handle = kinectAllocate();
    }

    /**
     * Native method call to startup the kinect recognizer.
     * @return kinect handle
     */
    private native long kinectAllocate();

    /**
     * Starts the recognition process.
     * @throws BadFetchError
     *         error starting the recognizer
     */
    public void startRecognition() throws BadFetchError {
        kinectStartRecognition(handle);
    }

    /**
     * Native method call to start the recognition process
     * @param handle handle to the kinect recognizer
     */
    private native void kinectStartRecognition(long handle);

    /**
     * Stops the recognition process.
     * @throws BadFetchError
     *         error starting the recognizer
     */
    public void stopRecognition() throws BadFetchError {
        kinectStartRecognition(handle);
    }

    /**
     * Native method call to stop the recognition process
     * @param handle handle to the kinect recognizer
     */
    private native void kinectStopRecognition(long handle);

    /**
     * Deallocates the kinect recognizer.
     */
    public void deallocate() {
        kinectDeallocate(handle);
        handle = 0;
    }

    /**
     * Native method call to shutdown the kinect recognizer.
     * @param handle handle to the kinect recognizer
     */
    private native long kinectDeallocate(long handle);
    
}
