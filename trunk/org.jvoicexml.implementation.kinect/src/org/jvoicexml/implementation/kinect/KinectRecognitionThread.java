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

import org.apache.log4j.Logger;


/**
 * Perform the recognition in an own java process.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
final class KinectRecognitionThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(KinectRecognitionThread.class);

    /** The calling SapiRecognizer.  **/
    private KinectRecognizer recognizer;
   
    /**
     * Constructs a new object.
     * @param rec the calling recognizer
     */
    public KinectRecognitionThread(final KinectRecognizer rec) {
        recognizer = rec;
        setDaemon(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        //start recognition and get the recognition result
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Starting recognition process");
        }
        try {
            final RecognitionResult result = recognizer.recognize();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Recognitionprocess ended");
            }
            recognizer.reportResult(result);
        } catch (KinectRecognizerException e) {
            recognizer.reportResult(e);
            return;
        }
    }

    /**
     * Stops the recognition process.
     */
    public void stopRecognition() {
        interrupt();
    }
}
