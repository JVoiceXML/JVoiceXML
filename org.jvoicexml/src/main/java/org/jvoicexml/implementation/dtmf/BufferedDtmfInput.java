/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *K
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

package org.jvoicexml.implementation.dtmf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Buffered DTMF input.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.5
 */
public class BufferedDtmfInput implements DtmfInput, SpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(BufferedDtmfInput.class);

    /** Maximum number of DTMFs in this buffer. */
    private static final int MAX_DTMF_INPUT = 512;

    /** All queued characters. */
    private volatile List<Character> buffer;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listener;

    /** Active grammars. */
    private final Collection<GrammarImplementation<?>> activeGrammars;

    /** The thread reading the dtmf sequences. */
    private Thread inputThread;

    /** Thread to monitor the inter digit timeout. */
    private InterdigitTimeoutThread interDigitTimeout;

    /** Reference to the current DTMF recognition properties. */
    private DtmfRecognizerProperties props;

    /** The grammar parser to use. */
    private final Map<GrammarType, GrammarParser<?>> parsers;

    /** The data model in use. */
    private DataModel model;

    /**
     * Constructs a new object.
     */
    public BufferedDtmfInput() {
        buffer = new java.util.ArrayList<Character>(
                MAX_DTMF_INPUT);
        listener = new java.util.ArrayList<SpokenInputListener>();
        activeGrammars = new java.util.ArrayList<GrammarImplementation<?>>();
        parsers = new java.util.HashMap<GrammarType, GrammarParser<?>>();
    }

    /**
     * Sets the grammar parsers to use.
     * 
     * @param grammarParsers
     *            the grammar parsers to use
     * @since 0.7.8
     */
    public void setGrammarParsers(final List<GrammarParser<?>> grammarParsers) {
        for (GrammarParser<?> parser : grammarParsers) {
            final GrammarType type = parser.getType();
            parsers.put(type, parser);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("added parser '" + parser + "' for grammar type '"
                        + type + "'");
            }
        }
    }

    /**
     * Activates the given grammars. It is guaranteed that all grammars types
     * are supported by this implementation.
     * 
     * @param grammars
     *            Grammars to activate.
     * @exception BadFetchError
     *                Grammar is not know by the recognizer.
     * @exception UnsupportedLanguageError
     *                The specified language is not supported.
     * @exception NoresourceError
     *                The input resource is not available.
     * @since 0.7
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        activeGrammars.addAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                LOGGER.debug("activated DTMF grammar "
                        + grammar.getGrammarDocument());
            }
        }
    }

    /**
     * Deactivates the given grammar. Do nothing if the input resource is not
     * available. It is guaranteed that all grammars types are supported by this
     * implementation.
     * 
     * @param grammars
     *            Grammars to deactivate.
     * 
     * @exception BadFetchError
     *                Grammar is not known by the recognizer.
     * @exception NoresourceError
     *                The input resource is not available.
     * @since 0.7
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        activeGrammars.removeAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                LOGGER.debug("deactivated DTMF grammar "
                        + grammar.getGrammarDocument());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addDtmf(final char dtmf)
            throws IllegalArgumentException {
        if ("01234567890#*".indexOf(Character.toString(dtmf)) < 0) {
            throw new IllegalArgumentException(
                    "'" + dtmf + "' is not one of 0123456789#* ");
        }
        synchronized (buffer) {
            buffer.add(dtmf);
            buffer.notifyAll();
        }
        final char termchar = props.getTermchar();
        if (dtmf == termchar) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added char '" + dtmf + "' buffer is now '"
                    + buffer.toString() + "'");
        }
        if (interDigitTimeout == null) {
            final long interdigittimeout = props.getInterdigittimeoutAsMsec();
            interDigitTimeout = new InterdigitTimeoutThread(this,
                    interdigittimeout, termchar);
            interDigitTimeout.start();
        } else {
            interDigitTimeout.enteredDigit();
        }
    }

    /**
     * Reads the next character. If no character is available this methods waits
     * for the next character.
     * 
     * @return next character.
     * @throws InterruptedException
     *             waiting interrupted.
     * @since 0.7
     */
    char getNextCharacter() throws InterruptedException {
        synchronized (buffer) {
            buffer.wait();
            return buffer.remove(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void startRecognition(final DataModel dataModel,
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
            throws NoresourceError, BadFetchError {
        model = dataModel;
        props = dtmf;
        inputThread = new DtmfInputThread(this, props);
        inputThread.start();
        LOGGER.info("started DTMF recognition");
    }

    /**
     * Checks if one of the active grammars accepts the current recognition
     * result.
     * 
     * @param result
     *            the recognized DTMF result
     * @return <code>true</code> if the result is accepted.
     * @since 0.7
     */
    public boolean isAccepted(final RecognitionResult result) {
        for (GrammarImplementation<?> grammar : activeGrammars) {
            if (result instanceof DtmfInputResult) {
                if (grammar instanceof GrammarEvaluator) {
                    final GrammarEvaluator evaluator =
                            (GrammarEvaluator) grammar;
                    final String utterance = result.getUtterance();
                    final Object interpretation = evaluator
                            .getSemanticInterpretation(model, utterance);
                    if (interpretation != null) {
                        final DtmfInputResult dtmfResult =
                                (DtmfInputResult) result;
                        dtmfResult.setSemanticInterpretation(interpretation);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        if (inputThread != null) {
            inputThread.interrupt();
            inputThread = null;
        }
        if (interDigitTimeout != null) {
            interDigitTimeout.interrupt();
            interDigitTimeout = null;
        }
        props = null;
        LOGGER.info("stopped DTMF recognition");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.remove(inputListener);
        }
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param event
     *            the event.
     * @since 0.6
     */
    public void fireInputEvent(final SpokenInputEvent event) {
        final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
        synchronized (listener) {
            copy.addAll(listener);
        }
        for (SpokenInputListener current : copy) {
            current.inputStatusChanged(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return parsers.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(final URI uri,
            final GrammarType type)
            throws NoresourceError, IOException, UnsupportedFormatError {
        final GrammarParser<?> parser = parsers.get(type);
        if (parser == null) {
            throw new UnsupportedFormatError("'" + type + "' is not supported");
        }
        return parser.load(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSpokenInput()
            throws NoresourceError, URISyntaxException {
        return null;
    }
}
