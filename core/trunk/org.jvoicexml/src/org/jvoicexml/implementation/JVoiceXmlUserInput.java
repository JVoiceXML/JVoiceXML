/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


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
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
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
        final Collection<GrammarImplementation<?>> voiceGrammars =
            new java.util.ArrayList<GrammarImplementation<?>>();
        final Collection<GrammarImplementation<?>> dtmfGrammars =
            new java.util.ArrayList<GrammarImplementation<?>>();

        for (GrammarImplementation<?> grammar : grammars) {
            final ModeType type = grammar.getModeType();
            // A grammar is voice by default.
            if (type == ModeType.DTMF) {
                dtmfGrammars.add(grammar);
            } else {
                voiceGrammars.add(grammar);
            }
        }

        if (voiceGrammars.size() > 0) {
            spokenInput.activateGrammars(voiceGrammars);
        }
        if (dtmfGrammars.size() > 0) {
            characterInput.activateGrammars(dtmfGrammars);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws NoresourceError, BadFetchError {
        final Collection<GrammarImplementation<?>> voiceGrammars =
            new java.util.ArrayList<GrammarImplementation<?>>();
        final Collection<GrammarImplementation<?>> dtmfGrammars =
            new java.util.ArrayList<GrammarImplementation<?>>();

        for (GrammarImplementation<?> grammar : grammars) {
            final ModeType type = grammar.getModeType();
            // A grammar is voice by default.
            if (type == ModeType.DTMF) {
                dtmfGrammars.add(grammar);
            } else {
                voiceGrammars.add(grammar);
            }
        }

        if (voiceGrammars.size() > 0) {
            spokenInput.deactivateGrammars(voiceGrammars);
        }
        if (dtmfGrammars.size() > 0) {
            characterInput.deactivateGrammars(dtmfGrammars);
        }
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
        if (type == GrammarType.SRGS_XML) {
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
                spokenInput;
            observableSpokenInput.addListener(listener);
        }

        if (characterInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableCharacterInput =
                characterInput;
            observableCharacterInput.addListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener listener) {
        if (spokenInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableSpokenInput =
                spokenInput;
            observableSpokenInput.removeListener(listener);
        }

        if (characterInput instanceof ObservableSpokenInput) {
            final ObservableSpokenInput observableCharacterInput =
                characterInput;
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
    public Collection<GrammarType> getSupportedGrammarTypes(
            final ModeType mode) {
        if (mode == ModeType.DTMF) {
            final Collection<GrammarType> types =
                new java.util.ArrayList<GrammarType>();
            types.add(GrammarType.SRGS_XML);
            return types;
        } else {
            return spokenInput.getSupportedGrammarTypes();
        }
    }


    /**
     * Checks if the corresponding input device is busy.
     * @return <code>true</code> if the input devices is busy.
     */
    public boolean isBusy() {
        return spokenInput.isBusy();
    }
}
