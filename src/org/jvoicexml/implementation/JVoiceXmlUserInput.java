/*
 * File:    $RCSfile: JVoiceXmlUserInput.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.vxml.BargeInType;


/**
 * Basic wrapper for <code>UserInput</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
final class JVoiceXmlUserInput
        implements UserInput {
    /** The character input device. */
    private final CharacterInput characterInput;

    /** The soken input device. */
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
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf) {
        characterInput.addCharacter(dtmf);
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(final Collection<RuleGrammar> grammars)
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
    public void deactivateGrammars(final Collection<RuleGrammar> grammars)
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
    public RuleGrammar loadGrammar(final Reader reader)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        return spokenInput.loadGrammar(reader);
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar newGrammar(final String name)
            throws NoresourceError {
        return spokenInput.newGrammar(name);
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
    public void setUserInputListener(final UserInputListener listener) {
        spokenInput.setUserInputListener(listener);
        characterInput.setUserInputListener(listener);
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
     *
     * @todo implement this method.
     */
    public void connect(final RemoteClient client) throws NoresourceError {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @todo implement this method.
     */
    public String getType() {
        return spokenInput.getType();
    }
}
