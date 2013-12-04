/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

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
import org.jvoicexml.implementation.GrammarImplementation;
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
 * <p>
 * This implementation is more or less a bridge that receives its input
 * from {@link TextTelephony} and forwards them to the voice browser.
 * </p>
 *
 * @author Dirk Schnelle-Walka
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
        for (GrammarImplementation<?> grammar : grammars) {
            activateGrammar(grammar);
        }
    }

    /**
     * Activates a given grammar. It's the implementation for 
     * activateGrammars().
     * @param grammar the grammar to activate
     * @exception BadFetchError
     *            Grammar is not known by the recognizer.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     */
    public void activateGrammar(final GrammarImplementation<?> grammar) 
            throws BadFetchError, UnsupportedLanguageError, 
            NoresourceError {
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

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return BARGE_IN_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return GRAMMAR_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void passivate() {
        listener.clear();
        grammarCheckers.clear();
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
    public void startRecognition(
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
        throws NoresourceError, BadFetchError {
        recognizing = true;
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        recognizing = false;
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STOPPED);
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
    @Override
    public URI getUriForNextSpokenInput() throws NoresourceError {
        return null;
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
