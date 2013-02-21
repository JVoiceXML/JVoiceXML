/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class provides a dummy implementation of a {@link UserInput} for
 * testing purposes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public class DummyUserInput
        implements UserInput {
    /** Supported grammar types of this user input. */
    private static final Collection<GrammarType> SUPPORTED_GRAMMAR_TYPES;

    /** The encapsulated spoken input. */
    private final SpokenInput input;

    static {
        SUPPORTED_GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
        SUPPORTED_GRAMMAR_TYPES.add(GrammarType.SRGS_ABNF);
    }

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
    public final Collection<BargeInType> getSupportedBargeInTypes() {
        return null;
    }

    /**
     * Adds the given grammar type to the list of supported grammar types.
     * @param type type to add
     * @since 0.7
     */
    public final void addSupportedGrammarType(final GrammarType type) {
        if (!SUPPORTED_GRAMMAR_TYPES.contains(type)) {
            SUPPORTED_GRAMMAR_TYPES.add(type);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<GrammarType> getSupportedGrammarTypes(
            final ModeType type) {
        return SUPPORTED_GRAMMAR_TYPES;
    }

    /**
     * Can be used to handle loading of special grammar types.
     * @param reader
     * @param type
     * @return
     * @throws NoresourceError
     * @throws BadFetchError
     */
    protected GrammarImplementation<?> handleLoadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final GrammarImplementation<?> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError,
            UnsupportedFormatError {
        final GrammarImplementation<?> impl =
            handleLoadGrammar(reader, type);
        if (impl != null) {
            return impl;
        }

        if (type == GrammarType.SRGS_XML) {
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
    public final GrammarImplementation<?> newGrammar(final GrammarType type)
        throws NoresourceError, UnsupportedFormatError {
        if (type == GrammarType.SRGS_XML) {
            return new SrgsXmlGrammarImplementation(null);
        }

        throw new UnsupportedFormatError(type + " is not supported");
    }

    /**
     * {@inheritDoc}
     */
    public final void passivate() {
    }

    /**
     * {@inheritDoc}
     */
    public final void record(final OutputStream out) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public final String getType() {
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
    public final void startRecognition() throws NoresourceError, BadFetchError {
        recognitionStarted = true;
    }

    /**
     * Check if the recognition has been started.
     * @return <code>true</code> if the recognition has been started.
     */
    public final boolean isRecognitionStarted() {
        return recognitionStarted;
    }

    /**
     * {@inheritDoc}
     */
    public final void stopRecognition() {
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
    public final URI getUriForNextSpokenInput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final SpokenInput getSpokenInput() throws NoresourceError {
        return input;
    }

}
