/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.text.TextConnectionInformation;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.processor.srgs.GrammarGraph;
import org.jvoicexml.processor.srgs.SrgsXmlGrammarParser;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Text based implementation for a {@link SpokenInput}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
final class TextSpokenInput implements SpokenInput, ObservableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSpokenInput.class);

    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** Supported grammar types. */
    private static final Collection<GrammarType> GRAMMAR_TYPES;

    /**Reference to the SrgsXmlGrammarParser.*/
    private final SrgsXmlGrammarParser parser;
    
    /** Active grammar checkers.*/
    private final Map<SrgsXmlGrammarImplementation, GrammarChecker>
        grammarCheckers;

    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);

        GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
    }

    /** Registered listener for input events. */
    private final Collection<SpokenInputListener> listener;

    /** Flag, if recognition is turned on. */
    private boolean recognizing;

    /**
     * Constructs a new object.
     */
    public TextSpokenInput() {
        listener = new java.util.ArrayList<SpokenInputListener>();
        grammarCheckers = new java.util.HashMap<SrgsXmlGrammarImplementation,
            GrammarChecker>();
        parser = new SrgsXmlGrammarParser();
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        for (GrammarImplementation<?> grammar : grammars) {
            final SrgsXmlGrammarImplementation impl =
                (SrgsXmlGrammarImplementation) grammar;
            if (!grammarCheckers.containsKey(impl)) {
                final SrgsXmlDocument doc = impl.getGrammar();
                final GrammarGraph graph = parser.parse(doc);
                if (graph != null) {
                    final GrammarChecker checker = new GrammarChecker(graph);
                    grammarCheckers.put(impl, checker);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn("Cannot create a grammar graph "
                                + "from the grammar file");
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        for (GrammarImplementation<?> grammar : grammars) {
            final SrgsXmlGrammarImplementation impl =
                (SrgsXmlGrammarImplementation) grammar;
            if (grammarCheckers.containsKey(impl)) {
                grammarCheckers.remove(impl);
            }
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
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return GRAMMAR_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError("Only SRGS XML is supported!");
        }

        final InputSource inputSource = new InputSource(reader);
        final SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return new SrgsXmlGrammarImplementation(doc);
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        listener.clear();
        grammarCheckers.clear();
        recognizing = false;
    }

    /**
     * {@inheritDoc}
     */
    public void record(final OutputStream out) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return TextConnectionInformation.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {
        recognizing = true;
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        recognizing = false;
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STOPPED);
        fireInputEvent(event);
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
     * Notifies the interpreter about an observer user input.
     * @param text received utterance.
     */
    void notifyRecognitionResult(final String text) {
        if (!recognizing || (listener == null)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received utterance '" + text + "'");
        }

        final SpokenInputEvent inputStartedEvent =
            new SpokenInputEvent(this, SpokenInputEvent.INPUT_STARTED,
                    ModeType.VOICE);
        fireInputEvent(inputStartedEvent);

        final String[] tokens = text.split(" ");
        GrammarChecker grammarChecker = null;
        for (GrammarChecker checker : grammarCheckers.values()) {
            if (checker.isValid(tokens)) {
                grammarChecker = checker;
                break;
            }
        }
        final RecognitionResult result = new TextRecognitionResult(
                text, grammarChecker);
        
        if (result.isAccepted()) {
            final SpokenInputEvent acceptedEvent =
                  new SpokenInputEvent(this, 
                          SpokenInputEvent.RESULT_ACCEPTED, result);

            fireInputEvent(acceptedEvent);
        } else {
            final SpokenInputEvent rejectedEvent =
                new SpokenInputEvent(this, 
                        SpokenInputEvent.RESULT_REJECTED, result);
       
           fireInputEvent(rejectedEvent); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
       return recognizing;
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
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
