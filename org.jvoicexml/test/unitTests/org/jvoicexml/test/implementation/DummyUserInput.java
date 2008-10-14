/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.test.implementation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;

import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RuleGrammar;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.implementation.jsapi10.RuleGrammarImplementation;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4RecognizerModeDesc;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.speech.engine.recognition.JSGFParser;

/**
 * This class provides a dummy implementation of a {@link UserInput} for
 * testing purposes.
 *
 * <p>
 * This class uses parts of the sphinx distribution. Thus it is necessary
 * to add the corresponding jars to the <code>CLASSPATH</code> and also
 * the sphinx configuration file.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DummyUserInput
        implements UserInput, SpokenInputProvider {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(DummyUserInput.class);

    /** Supported grammar types of this user input. */
    private static final Collection<GrammarType> SUPPORTED_GRAMMAR_TYPES;

    /** The encapuslated spoken input. */
    private final SpokenInput input;

    static {
        SUPPORTED_GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.JSGF);
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_ABNF);
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
    }

    /** The recognizer. */
    private static Recognizer recognizerCache;

    /** Flag if the recognition has been started. */
    private boolean recognitionStarted;

    /**
     * Constructs a new object.
     */
    public DummyUserInput() {
        this(null);
    };

    /**
     * Constructs a new object.
     * @param spokenInput the encapsulated spoken input.
     */
    public DummyUserInput(final SpokenInput spokenInput) {
        input = spokenInput;
    }

    /**
     * Lazy instantiation of the recognizer.
     * @return Reference to the recognizer.
     */
    private Recognizer getRecognizer() {
        if (recognizerCache != null) {
            return recognizerCache;
        }

        Sphinx4RecognizerModeDesc desc = new Sphinx4RecognizerModeDesc();
        try {
            recognizerCache = (Recognizer) desc.createEngine();
            recognizerCache.allocate();
        } catch (EngineException e) {
            LOGGER.warn("unable to create a recognizer");
        }

        return recognizerCache;
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
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return SUPPORTED_GRAMMAR_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError,
            UnsupportedFormatError {
        if (type == GrammarType.JSGF) {
            final RuleGrammar grammar;
            final Recognizer recognizer = getRecognizer();
            try {
                grammar = JSGFParser.newGrammarFromJSGF(reader, recognizer);
            } catch (GrammarException e) {
                throw new BadFetchError("unabale to read the grammar", e);
            }
            return new RuleGrammarImplementation(grammar);
        } else if (type == GrammarType.SRGS_XML) {
            final InputSource inputSource = new InputSource(reader);
            SrgsXmlDocument doc;
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

        throw new UnsupportedFormatError(type + " is not supported");
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> newGrammar(final GrammarType type)
        throws NoresourceError, UnsupportedFormatError {
        if (type == GrammarType.JSGF) {
            final RuleGrammar grammar = new DummyRuleGrammar();
            return new RuleGrammarImplementation(grammar);
        } else if (type == GrammarType.SRGS_XML) {
            return new SrgsXmlGrammarImplementation(null);
        }

        throw new UnsupportedFormatError(type + " is not supported");
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {
        recognitionStarted = true;
    }

    /**
     * Check if the recognition has been started.
     * @return <code>true</code> if the recognition has been started.
     */
    public boolean isRecognitionStarted() {
        return recognitionStarted;
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        recognitionStarted = false;
    }

    /**
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf) {
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
    public SpokenInput getSpokenInput() throws NoresourceError {
        return input;
    }

}
