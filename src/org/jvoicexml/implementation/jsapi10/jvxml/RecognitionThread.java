/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;

import edu.cmu.sphinx.frontend.DataProcessor;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;

/**
 * Recognition thread to run the recognizer in parallel.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
final class RecognitionThread
        extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecognitionThread.class);

    /** The wrapper for the sphinx4 recognizer. */
    private Sphinx4Recognizer recognizer;

    /**
     * Creates a new object.
     * @param rec The wrapper for the sphinx4 recognizer.
     */
    public RecognitionThread(final Sphinx4Recognizer rec) {
        recognizer = rec;
        setDaemon(true);
        setName("Sphinx4RecognitionThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition thread started");
        }

        final Recognizer rec = recognizer.getRecognizer();
        final Microphone microphone = getMicrophone();
        final boolean started;

        if (microphone != null) {
            microphone.clear();
            started = microphone.startRecording();
        } else {
            started = true;
        }

        if (started) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("start recognizing ..");
            }

            recognize(rec, microphone);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition thread...");
        }

        if (microphone != null) {
            // Stop recording from the microphone.
            while (microphone.isRecording()) {
                microphone.stopRecording();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition thread terminated");
        }
    }

    /**
     * Recognition loop. Continue recognizing until this thread is
     * requested to stop.
     * @param rec The recognizer to use.
     * @param mic The microphone to use.
     */
    private void recognize(final Recognizer rec, final Microphone mic) {
        while (hasMoreData(mic) && !isInterrupted()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("recognizing...");
                    final String [] grammars =
                        recognizer.getRuleGrammar().listRuleNames();
                    LOGGER.debug("RuleGrammars that will be used:");
                    for (int i = 0; i < grammars.length; i++) {
                        LOGGER.debug("grammar: '" + grammars[i].toString()
                                + "'");
                    }
                }

                rec.recognize();
            } catch (IllegalArgumentException iae) {
                LOGGER.debug("unmatched utterance", iae);
            }
        }
    }

    /**
     * Checks, if the microphone has more data to deliver.
     * @param mic The microphone or <code>null</code> if the data processor
     * is not a microphone.
     * @return <code>true</code> if there is more data.
     */
    private boolean hasMoreData(final Microphone mic) {
        if (mic == null) {
            return true;
        }

        return mic.hasMoreData();
    }

    /**
     * Stop this recognition thread.
     */
    public void stopRecognition() {
        final Microphone microphone = getMicrophone();
        if (microphone != null) {
            microphone.stopRecording();
        }

        interrupt();
    }

    /**
     * Retrieves the microphone.
     * @return The microphone, <code>null</code> if the data processor is
     * not a microphone.
     * @since 0.5.5
     */
    private Microphone getMicrophone() {
        final DataProcessor dataProcessor = recognizer.getDataProcessor();
        if (dataProcessor instanceof Microphone) {
            return (Microphone) dataProcessor;
        }

        return null;
    }
}
