/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URL;

import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;

import com.sun.speech.engine.recognition.BaseRecognizer;

import edu.cmu.sphinx.frontend.DataProcessor;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.recognizer.RecognizerState;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * JSAPI wrapper for sphinx4.
 *
 * <p>
 * Unfortunately sphinx4 provides no full support for JSAPI, so we try to
 * build our own wrapper. This is going to be a bit troublesome. Hope we
 * can make it ;-)
 * </p>
 *
 * <p>
 * Sphinx 4 uses a configuration file <code>sphinx4.config.xml</code>. This
 * implementation expects to find this file in the <code>CLASSPATH</code>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
final class Sphinx4Recognizer
        extends BaseRecognizer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Sphinx4Recognizer.class);

    /** Msecs to sleep before the status of the recognizer is checked again. */
    private static final long SLEEP_MSEC = 50;

    /** The encapsulated recognizer. */
    private Recognizer recognizer;

    /** The input device. */
    private DataProcessor dataProcessor;

    /** The grammar manager. */
    private JSGFGrammar grammar;

    /** The result listener. */
    private final Sphinx4ResultListener resultListener;

    /**
     * The decoding thread. It points either to the single decoding thread
     * or is <code>null</code> if no recognition thread is started.
     */
    private RecognitionThread recognitionThread;

    /**
     * Construct a new object.
     */
    public Sphinx4Recognizer() {
        URL url = Sphinx4Recognizer.class.getResource("/sphinx4.config.xml");

        try {
            final ConfigurationManager configuration =
                    new ConfigurationManager(url);

            recognizer = (Recognizer) configuration.lookup("recognizer");
            dataProcessor = (DataProcessor) configuration.lookup("microphone");
            grammar = (JSGFGrammar) configuration.lookup("jsgfGrammar");
        } catch (Exception ex) {
            LOGGER.error("error creating engine properties", ex);
        }

        resultListener = new Sphinx4ResultListener(this);
    }

    /**
     * Called from the <code>resume</code> method.
     */
    @Override
    protected void handleResume() {
        if (recognizer == null) {
            LOGGER.warn("no recognizer: cannot resume!");
            return;
        }

        if (recognitionThread != null) {
            LOGGER.debug("recognition thread already started.");
            return;
        }

        recognitionThread = new RecognitionThread(this);
        recognitionThread.start();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition started");
        }
    }

    /**
     * Called from the <code>pause</code> method.
     */
    @Override
    protected void handlePause() {
        if (recognitionThread == null) {
            LOGGER.warn("cannot pause, no decoder started");
            return;
        }

        stopRecognitionThread();
        if (dataProcessor instanceof Microphone) {
            final Microphone microphone = (Microphone) dataProcessor;
            microphone.stopRecording();
        }
    }


    /**
     * Called from the <code>allocate</code> method.
     *
     * @throws EngineException if problems are encountered
     */
    @Override
    protected void handleAllocate()
            throws EngineException {
        if (recognizer == null) {
            throw new EngineException(
                    "cannot allocate: no recognizer created!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("allocating sphinx4 recognizer...");
        }

        try {
            recognizer.allocate();

            final RuleGrammar[] grammars = listRuleGrammars();
            for (int i = 0; i < grammars.length; i++) {
                deleteRuleGrammar(grammars[i]);
            }

            recognizer.addResultListener(resultListener);

        } catch (java.io.IOException ioe) {
            throw new EngineException(ioe.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...sphinx4 allocated");
            LOGGER.debug("state: " + recognizer.getState());
        }

        setEngineState(CLEAR_ALL_STATE, ALLOCATED);
    }

    /**
     * Called from the <code>deallocate</code> method.
     *
     * @throws EngineException if this <code>Engine</code> cannot be
     *   deallocated.
     */
    @Override
    protected void handleDeallocate()
            throws EngineException {
        if (recognizer == null) {
            throw new EngineException(
                    "cannot deallocate: no recognizer created!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deallocating recognizer...");
        }

        // Stop the decoder thread.
        stopRecognitionThread();

        // Deallocate the recognizer and wait until it stops recognizing.
        recognizer.deallocate();
        while (recognizer.getState() == RecognizerState.RECOGNIZING) {
            try {
                Thread.sleep(SLEEP_MSEC);
            } catch (InterruptedException ie) {
                LOGGER.warn("error waiting for recognizer to deallocate", ie);
            }
        }
        recognizer.resetMonitors();
        recognizer.removeResultListener(resultListener);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...deallocated");
        }
    }

    /**
     * Load a RuleGrammar and its imported grammars from a URL containing
     * JSGF text.
     * From javax.speech.recognition.Recognizer.
     * @param url the base URL containing the JSGF grammar file.
     * @param name the name of the JSGF grammar to load.
     * @return Loaded grammar.
     *
     * @exception GrammarException
     *            Error in the grammar.
     * @exception IOException
     *            Error reading the grammar.
     */
    @Override
    public RuleGrammar loadJSGF(final URL url, final String name)
            throws GrammarException, IOException {
        grammar.loadJSGF(name);
        return super.loadJSGF(url, name);
    }

    /**
     * Selector for the data processor (the microphone).
     * @return The used data processor.
     */
    DataProcessor getDataProcessor() {
        return dataProcessor;
    }

    /**
     * Selector for the wrapped sphinx4 recognizer.
     * @return Recognizer
     */
    Recognizer getRecognizer() {
        return recognizer;
    }

    /**
     * Stop the recognition thread and wait until it is terminated.
     */
    private void stopRecognitionThread() {
        if (recognitionThread == null) {
            LOGGER.debug("recognition thread already stopped");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition thread...");
        }
        recognitionThread.stopRecognition();

        final long maxSleepTime = 5000;
        long sleepTime = 0;

        while (recognitionThread.isAlive() && (sleepTime < maxSleepTime)) {
            try {
                Thread.sleep(SLEEP_MSEC);
                sleepTime += SLEEP_MSEC;
            } catch (InterruptedException ie) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("recognition thread interrupted");
                }
            }
        }

        recognitionThread = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognition thread stopped");
        }
    }

    /**
     * Get the current rule grammar.
     * @return Active grammar.
     */
    RuleGrammar getRuleGrammar() {
        return grammar.getRuleGrammar();
    }
}
