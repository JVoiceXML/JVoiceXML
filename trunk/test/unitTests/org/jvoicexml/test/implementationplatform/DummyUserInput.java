/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.test.implementationplatform;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;

import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.implementation.jsapi10.RuleGrammarImplementation;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4RecognizerModeDesc;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

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
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DummyUserInput
        implements UserInput {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(DummyUserInput.class);

    /** Supported grammar types of this user input. */
    private static final Collection<GrammarType> SUPPORTED_GRAMMAR_TYPES;

    static {
        SUPPORTED_GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.JSGF);
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_ABNF);
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
    }

    /** The recognizer. */
    private static Recognizer recognizerCache;

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
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void record(final OutputStream out) throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        // TODO Auto-generated method stub
        return null;
    }

}
