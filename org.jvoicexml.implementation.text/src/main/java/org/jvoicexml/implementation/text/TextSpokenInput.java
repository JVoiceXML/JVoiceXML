/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.client.text.TextConnectionInformation;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStoppedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Text based implementation for a {@link SpokenInput}.
 * 
 * <p>
 * This implementation is more or less a bridge that receives its input from
 * {@link TextTelephony} and forwards them to the voice browser.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
final class TextSpokenInput implements SpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextSpokenInput.class);

    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** The grammar parser to use. */
    private final Map<String, GrammarParser<?>> parsers;

    /** Active grammars. */
    private final Collection<GrammarImplementation<?>> activeGrammars;

    /** The data model in use. */
    private DataModel model;
    
    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);
    }

    /** Registered listener for input events. */
    private final Collection<SpokenInputListener> listener;

    /** Flag, if recognition is turned on. */
    private boolean recognizing;

    /**
     * Constructs a new object.
     */
    TextSpokenInput() {
        activeGrammars = new java.util.ArrayList<GrammarImplementation<?>>();
        parsers = new java.util.HashMap<String, GrammarParser<?>>();
        listener = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * Sets the grammar parsers to use.
     * @param grammarParsers the grammar parsers to use
     * @since 0.7.8
     */
    public void setGrammarParsers(final List<GrammarParser<?>> grammarParsers) {
        for (GrammarParser<?> parser : grammarParsers) {
            final GrammarType type = parser.getType();
            parsers.put(type.getType(), parser);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        activeGrammars.addAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                LOGGER.debug("activated grammar "
                        + grammar.getGrammarDocument());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        if (grammars == null) {
            return;
        }
        activeGrammars.removeAll(grammars);
        if (LOGGER.isDebugEnabled()) {
            for (GrammarImplementation<?> grammar : grammars) {
                if (grammar != null) {
                    LOGGER.debug("deactivated grammar "
                            + grammar.getGrammarDocument());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return BARGE_IN_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
        final Collection<GrammarType> types =
                new java.util.ArrayList<GrammarType>();
        for (GrammarParser<?> parser : parsers.values()) {
            types.add(parser.getType());
        }
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(final URI uri,
            final GrammarType type) throws NoresourceError, IOException,
            UnsupportedFormatError {
        final GrammarParser<?> parser = parsers.get(type.getType());
        if (parser == null) {
            throw new UnsupportedFormatError("'" + type + "' is not supported");
        }
        return parser.load(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        listener.clear();
        activeGrammars.clear();
        recognizing = false;
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
    public String getType() {
        return TextConnectionInformation.TYPE;
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
    public void startRecognition(final DataModel dataModel,
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf) throws NoresourceError,
            BadFetchError {
        model = dataModel;
        recognizing = true;
        final SpokenInputEvent event = new RecognitionStartedEvent(this, null);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        recognizing = false;
        final SpokenInputEvent event = new RecognitionStoppedEvent(this, null);
        fireInputEvent(event);
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
     * Notifies the interpreter about an observer user input.
     * 
     * @param text
     *            received utterance.
     */
    void notifyRecognitionResult(final String text) {
        if (!recognizing || (listener == null)) {
            return;
        }

        LOGGER.info("received utterance '" + text + "'");

        final SpokenInputEvent inputStartedEvent = new InputStartedEvent(this,
                null, ModeType.VOICE);
        fireInputEvent(inputStartedEvent);

        Object interpretation = null;
        for (GrammarImplementation<?> grammar : activeGrammars) {
            if (grammar instanceof GrammarEvaluator) {
                final GrammarEvaluator evaluator = (GrammarEvaluator) grammar;
                interpretation =
                        evaluator.getSemanticInterpretation(model, text);
                if (interpretation != null) {
                    break;
                }
            }
        }
        final RecognitionResult result = new TextRecognitionResult(text,
                interpretation);
        if (result.isAccepted()) {
            final SpokenInputEvent acceptedEvent = new RecognitionEvent(this,
                    null, result);
            fireInputEvent(acceptedEvent);
        } else {
            final SpokenInputEvent rejectedEvent = new NomatchEvent(this, null,
                    result);
            fireInputEvent(rejectedEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return recognizing;
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param event
     *            the event.
     * @since 0.6
     */
    private void fireInputEvent(final SpokenInputEvent event) {
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
