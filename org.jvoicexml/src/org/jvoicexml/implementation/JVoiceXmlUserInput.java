/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;


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
 * Copyright &copy; 2006-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class JVoiceXmlUserInput
        implements UserInput, ObservableSpokenInput, SpokenInputProvider {
    /** The character input device. */
    private final BufferedCharacterInput characterInput;

    /** The spoken input device. */
    private final SpokenInput spokenInput;

    /**
     * Constructs a new object.
     * @param input the spoken input implementation.
     * @param character the buffered character input.
     */
    public JVoiceXmlUserInput(final SpokenInput input,
            final BufferedCharacterInput character) {
        spokenInput = input;

        characterInput = character;
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
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        spokenInput.activateGrammars(grammars);
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
    public void record(final OutputStream out)
            throws NoresourceError {
        spokenInput.record(out);
    }


    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener listener) {
        if (spokenInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableSpokenInput =
                (ObservableSpokenInput) spokenInput;
            observableSpokenInput.addListener(listener);
        }

        if (characterInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableCharacterInput =
                (ObservableSpokenInput) characterInput;
            observableCharacterInput.addListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener listener) {
        if (spokenInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableSpokenInput =
                (ObservableSpokenInput) spokenInput;
            observableSpokenInput.removeListener(listener);
        }

        if (characterInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableCharacterInput =
                (ObservableSpokenInput) characterInput;
            observableCharacterInput.removeListener(listener);
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
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return spokenInput.getSupportedGrammarTypes();
    }


    /**
     * Checks if the corresponding input device is busy.
     * @return <code>true</code> if the input devices is busy.
     */
    public boolean isBusy() {
        return spokenInput.isBusy();
    }
}
