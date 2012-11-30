/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.kinect/src/org/jvoicexml/implementation/kinect/KinectRecognizer.java $
 * Version: $LastChangedRevision: 3353 $
 * Date:    $Date: 2012-11-28 19:46:19 +0100 (Mi, 28 Nov 2012) $
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

import org.apache.log4j.Logger;


/**
 * A Kinect Recognizer.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3353 $
 * @since 0.7.6
 *
 */
public final class KinectRecognizer {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(KinectRecognizer.class);

    static {
        //Check the processor architecture
        if (System.getProperty("os.arch").equalsIgnoreCase("x86")) {
            System.loadLibrary("JVoiceXmlKinectRecognizer");
        } else {
            System.loadLibrary("JVoiceXmlKinectRecognizer");
//            System.loadLibrary("JVoiceXmlKinectRecognizer_x64");
        }
    }


    /** Kinect recognizer Handle. **/
    private long handle;

    /** The active recognition thread. */
    private KinectRecognitionThread recognitionThread;

    /**
     * Allocates this recognizer.
     * @throws KinectRecognizerException
     *         error allocating the recognizer.
     */
    public void allocate() throws KinectRecognizerException {
        handle = kinectAllocate();
    }

    /**
     * Native method call to startup the kinect recognizer.
     * @return kinect handle
     * @exception KinectRecognizerException
     *          if the recognizer could not be allocated
     */
    private native long kinectAllocate() throws KinectRecognizerException;

    /**
     * Checks if the recognizer is allocated.
     * @return <code>true</code> if the recognizer is allocated
     */
    public boolean isAllocated() {
        return handle != 0;
    }

    /**
     * Starts the recognition process.
     * @throws KinectRecognizerException
     *         error starting the recognizer
     */
    public void startRecognition() {
        recognitionThread = new KinectRecognitionThread(this);
        recognitionThread.start();
    }

    /**
     * Internal call to start the recognition.
     * @return recognition result
     * @throws KinectRecognizerException
     *         error recognizing
     */
    RecognitionResult recognize()
            throws KinectRecognizerException {
        return kinectRecognizeSpeech(handle);
    }

    /**
     * Native method call to start the recognition process
     * @param handle handle to the kinect recognizer
     * @return result of the recognition process
     */
    private native RecognitionResult kinectRecognizeSpeech(long handle)
            throws KinectRecognizerException;

    /**
     * Reports the result of the recognition process.
     * @param result the obtained result.
     */
    void reportResult(final RecognitionResult result) {
        LOGGER.info("recognized: " + result.getStatus());
        LOGGER.info("recognized: " + result.getSml());
    }

    /**
     * Reports the result of the recognition process.
     * @param e error while recognizing
     */
    void reportResult(final KinectRecognizerException e) {
        LOGGER.warn("error recognizing", e);
    }
    
    /**
     * Stops the recognition process.
     * @throws KinectRecognizerException
     *         error starting the recognizer
     */
    public void stopRecognition() throws KinectRecognizerException {
        if (recognitionThread != null) {
            recognitionThread.stopRecognition();
            recognitionThread = null;
        }
        kinectStopRecognition(handle);
    }

    /**
     * Native method call to stop the recognition process
     * @param handle handle to the kinect recognizer
     * @exception KinectRecognizerException
     *          recognition could not be stopped
     */
    private native void kinectStopRecognition(long handle)
            throws KinectRecognizerException;

    /**
     * Deallocates the kinect recognizer.
     * @exception KinectRecognizerException
     *          recognizer could not be deallocated
     */
    public void deallocate() throws KinectRecognizerException{
        try {
            kinectDeallocate(handle);
        } finally {
            handle = 0;
        }
    }

    /**
     * Native method call to shutdown the kinect recognizer.
     * @param handle handle to the kinect recognizer
     * @exception KinectRecognizerException
     *          if the recognizer could not be deallocated
     */
    private native void kinectDeallocate(long handle)
            throws KinectRecognizerException;
    
}
