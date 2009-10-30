/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Spoken input that uses the JSAPI 1.0 to address the recognition engine.
 *
 * <p>
 * Handle all JSAPI calls to the recognizer to make JSAPI transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class Jsapi10SpokenInput
        implements SpokenInput, ObservableSpokenInput, StreamableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(Jsapi10SpokenInput.class);

    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** Supported grammar types. */
    private static final Collection<GrammarType> GRAMMAR_TYPES;

    /** The speech recognizer. */
    private Recognizer recognizer;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listener;

    /** The default recognizer mode descriptor. */
    private final RecognizerModeDesc desc;

    /** A custom handler to handle remote connections. */
    private SpokenInputConnectionHandler handler;

    /** Reference to a remote client configuration data. */
    private RemoteClient client;

    /** Listener for recognition results. */
    private ResultListener resultListener;

    /** The encapsulated streamable input. */
    private StreamableSpokenInput streamableInput;

    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);

        GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        GRAMMAR_TYPES.add(GrammarType.JSGF);
    }

    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     */
    public Jsapi10SpokenInput(final RecognizerModeDesc defaultDescriptor) {
        desc = defaultDescriptor;
        listener = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            recognizer = Central.createRecognizer(desc);
            if (recognizer == null) {
                throw new NoresourceError("Error creating the recognizer!");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("allocating JSAPI 1.0 recognizer...");
            }

            recognizer.allocate();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...JSAPI recognizer allocated");
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (recognizer == null) {
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio input...");
            LOGGER.debug("deallocating recognizer...");
        }

        try {
            recognizer.deallocate();
        } catch (EngineException ee) {
            LOGGER.error("error deallocating the recognizer", ee);
        } finally {
            recognizer = null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio input closed");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.remove(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return BARGE_IN_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> loadGrammar(final Reader reader,
            final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (type != GrammarType.JSGF) {
            throw new UnsupportedFormatError(
                    "JSAPI 1.0 implementation supports only type "
                    + GrammarType.JSGF.getType());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        final RuleGrammar grammar;

        try {
            grammar = recognizer.loadJSGF(reader);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        } catch (javax.speech.recognition.GrammarException ge) {
            throw new UnsupportedFormatError(ge);
        }

        return new RuleGrammarImplementation(grammar);
    }

    /**
     * Activates the given grammar.
     * @param name
     *        Name of the grammar.
     * @param activate
     *        <code>true</code> if the grammar should be activated.
     *
     * @return <code>true</code> if the grammar is active.
     * @exception BadFetchError
     *        Error creating the grammar.
     */
    private boolean activateGrammar(final String name, final boolean activate)
            throws BadFetchError {
        final RuleGrammar grammar = recognizer.getRuleGrammar(name);
        if (grammar == null) {
            throw new BadFetchError("unable to activate unregistered grammar '"
                                    + name + "'!");
        }

        grammar.setEnabled(activate);
        grammar.setActivationMode(Grammar.RECOGNIZER_FOCUS);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("grammar '" + name + "' activation mode: "
                         + grammar.getActivationMode()
                         + " enabled: " + grammar.isEnabled());
        }

        return activate;
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                    (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("activating grammar '" + name + "'...");
                }

                activateGrammar(name, true);
            }
        }

        // Commit the changes.
        try {
            recognizer.commitChanges();
        } catch (GrammarException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (EngineStateError e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError {
        if (recognizer == null) {
            return;
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                    (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("deactivating grammar '" + name + "'...");
                }

                activateGrammar(name, false);
            }
        }
        // Commit the changes.
        try {
            recognizer.commitChanges();
        } catch (GrammarException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (EngineStateError e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * @todo Implement this record() method.
     */
    public void record(final OutputStream out)
            throws NoresourceError {
        throw new NoresourceError("not implemented yet");
//        TargetDataLine line;
//        DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
//        // format is an AudioFormat object
//        // Obtain and open the line.
//        try {
//            line = (TargetDataLine) AudioSystem.getLine(info);
//            line.open(format);
//        } catch (LineUnavailableException ex) {
//            // Handle the error ...
//        }
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int numBytesRead;
//        byte[] data = new byte[line.getBufferSize() / 5];
//        // Begin audio capture.
//        line.start();
//        // Here, stopped is a global boolean set by another thread.
//        while (!stopped) {
//            // Read the next chunk of data from the TargetDataLine.
//            numBytesRead = line.read(data, 0, data.length);
//            // Save this chunk of data.
//            out.write(data, 0, numBytesRead);
//        }
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition()
            throws NoresourceError, BadFetchError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }

        try {
            recognizer.commitChanges();
            recognizer.waitEngineState(Recognizer.LISTENING);
        } catch (GrammarException ge) {
            throw new BadFetchError(ge.getMessage(), ge);
        } catch (IllegalArgumentException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new BadFetchError(e.getMessage(), e);
        }

        if (LOGGER.isDebugEnabled()) {
            final RuleGrammar[] grammars = recognizer.listRuleGrammars();
            for (RuleGrammar grammar : grammars) {
                LOGGER.debug("grammar '" + grammar.getName() + "', active:" 
                        + grammar.isActive() + ", enabled:"
                        + grammar.isEnabled());
            }
        }
        recognizer.requestFocus();
        try {
            recognizer.resume();
        } catch (AudioException ae) {
            throw new NoresourceError(ae);
        }

        // Create a new result listener.
        resultListener = new JVoiceXMLRecognitionListener(this);
        recognizer.addResultListener(resultListener);

        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...recognition started");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        if (!recognizer.testEngineState(Recognizer.RESUMED)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("recognition not started. No need to stop.");
            }

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition...");
        }

        // If a result listener exists: Remove it.
        if (resultListener != null) {
            recognizer.removeResultListener(resultListener);
            resultListener = null;
        }
        recognizer.pause();

        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STOPPED);
        fireInputEvent(event);


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...recognition stopped");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activated input");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        listener.clear();
        if (resultListener != null) {
            recognizer.removeResultListener(resultListener);
            resultListener = null;
        }
        
        handler = null;
        client = null;
        streamableInput = null;
        final RuleGrammar[] grammars = recognizer.listRuleGrammars();
        for (RuleGrammar grammar : grammars) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleting grammar '" + grammar.getName() + "'");
            }
            recognizer.deleteRuleGrammar(grammar);
        }
        try {
            recognizer.commitChanges();
        } catch (GrammarException e) {
            LOGGER.warn("error deactivating grammars", e);
        } catch (EngineStateError e) {
            LOGGER.warn("error deactivating grammars", e);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivated input");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient remoteClient)
        throws IOException {
        if (handler != null) {
            handler.connect(client, this, recognizer);
        }

        client = remoteClient;
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {
        if (handler != null) {
            handler.disconnect(client, this, recognizer);
        }

        client = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10";
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return GRAMMAR_TYPES;
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setSpokenInputConnectionHandler(
            final SpokenInputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        if (handler != null) {
            return handler.getUriForNextSpokenInput(client);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return recognizer.testEngineState(Recognizer.RESUMED);
    }

    /**
     * Sets the streamable input.
     * @param streamable the streamable input to set.
     */
    public void setStreamableSpokenInput(
            final StreamableSpokenInput streamable) {
        streamableInput = streamable;
    }

    /**
     * {@inheritDoc}
     */
    public void writeRecognizerStream(final byte[] buffer, final int offset,
            final int length) throws IOException {
        if (streamableInput == null) {
            return;
        }

        streamableInput.writeRecognizerStream(buffer, offset, length);
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) {
        synchronized (listener) {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listener);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }
}
