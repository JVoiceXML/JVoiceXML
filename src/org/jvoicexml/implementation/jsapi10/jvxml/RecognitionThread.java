/*
 * File:    $RCSfile: RecognitionThread.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jsapi10.jvxml;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import org.apache.log4j.Logger;

/**
 * Recognition thread to run the recognizer in parallel.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
final class RecognitionThread
        extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecognitionThread.class);

    /** Flag, if this thread is running.*/
    private boolean running;

    /** The wrapper for the sphinx4 recognizer. */
    private Sphinx4Recognizer recognizer;

    /** Flag, if this decoding thread should be terminated. */
    private boolean stopRequest;

    /**
     * Create a new object.
     * @param rec The wrapper for the sphinx4 recognizer.
     */
    public RecognitionThread(final Sphinx4Recognizer rec) {
        recognizer = rec;
        running = false;
        setDaemon(true);
    }

    /**
     * Runs this thread.
     */
    public void run() {
        stopRequest = false;
        running = true;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition thread started");
        }

        final Microphone microphone = recognizer.getMicrophone();
        final Recognizer rec = recognizer.getRecognizer();

        microphone.clear();
        final boolean started = microphone.startRecording();

        if (started) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("start recognizing ..");
            }

            recognize(rec, microphone);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition thread...");
        }

        // Stop recording from the microphone.
        while (microphone.isRecording()) {
            microphone.stopRecording();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition thread terminated");
        }

        running = false;
    }

    /**
     * Recognition loop. Continue recognizing until this thread is
     * requested to stop.
     * @param rec The recognizer to use.
     * @param mic The microphone to use.
     */
    private void recognize(final Recognizer rec, final Microphone mic) {
        while (mic.hasMoreData() && !stopRequest) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("recognizing...");
                }

                rec.recognize();
            } catch (IllegalArgumentException iae) {
                LOGGER.debug("unmatched utterance?", iae);
            } catch (Exception e) {
                LOGGER.error("error recognizing", e);
            }
        }
    }

    /**
     * Check, if this decoding threas is running.
     * @return <code>true</code>, if the decoder thread is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop this recognition thread.
     */
    public void stopRecognition() {
        stopRequest = true;

        final Microphone microphone = recognizer.getMicrophone();
        microphone.stopRecording();
    }
}
