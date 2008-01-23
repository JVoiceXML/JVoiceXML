/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.implementation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;
import java.net.URI;


/**
 * Basic wrapper for {@link UserInput}.
 *
 * <p>
 * The {@link UserInput} encapsulates two external resources. A basic
 * implementation for the {@link CharacterInput} is provided by the
 * interpreter. The unknown resource is the spoken input, which must be obtained
 * from a resource pool. This class combines these two as the {@link UserInput}
 * which is been used by the rest of the interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.5
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class JVoiceXmlUserInput
        implements UserInput, ObservableUserInput {
    /** The character input device. */
    private final CharacterInput characterInput;

    /** The spoken input device. */
    private final SpokenInput spokenInput;

    /**
     * Constructs a new object.
     * @param input The spoken input implementation.
     */
    public JVoiceXmlUserInput(final SpokenInput input) {
        spokenInput = input;

        characterInput = new BufferedCharacterInput();
    }

    /**
     * Retrieves the spoken input.
     * @return spoken input.
     *
     * @since 0.5.5
     */
    public SpokenInput getSpokenInput() {
        return spokenInput;
    }

    /**
     * Retrieves the character input.
     * @return character input.
     *
     * @since 0.5.5
     */
    public CharacterInput getCharacterInput() {
        return characterInput;
    }

    /**
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf) {
        characterInput.addCharacter(dtmf);
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        spokenInput.activateGrammars(grammars);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws NoresourceError, BadFetchError {
        spokenInput.deactivateGrammars(grammars);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return spokenInput.getSupportedBargeInTypes();
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        return spokenInput.loadGrammar(reader, type);
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        spokenInput.open();
    }

    /**
     * {@inheritDoc}
     */
    public void record(final OutputStream out)
            throws NoresourceError {
        spokenInput.record(out);
    }


    /**
     * {@inheritDoc}
     */
    public void addUserInputListener(final UserInputListener listener) {
        if (spokenInput instanceof ObservableUserInput) {
            final ObservableUserInput observableSpokenInput =
                (ObservableUserInput) spokenInput;
            observableSpokenInput.addUserInputListener(listener);
        }

        if (characterInput instanceof ObservableUserInput) {
            final ObservableUserInput observableCharacterInput =
                (ObservableUserInput) characterInput;
            observableCharacterInput.addUserInputListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition()
            throws NoresourceError, BadFetchError {
        spokenInput.startRecognition();
        characterInput.startRecognition();
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        spokenInput.stopRecognition();
        characterInput.stopRecognition();
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        spokenInput.activate();
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        spokenInput.passivate();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client)
        throws IOException {
        spokenInput.connect(client);
        characterInput.connect(client);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        spokenInput.disconnect(client);
        characterInput.disconnect(client);
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return spokenInput.getType();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return spokenInput.getSupportedGrammarTypes();
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        return spokenInput.getUriForNextSpokenInput();
    }
}
