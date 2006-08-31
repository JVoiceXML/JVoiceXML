/*
 * File:    $RCSfile: SpokenInput.java,v $
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Facade for easy control and monitoring of the user's speech input.
 *
 * <p>
 * Objects that implement this interface are able to detect spoken input and to
 * control input detection interval duration with a timer whose length is
 * specified by a VoiceXML document.
 * </p>
 *
 * <p>
 * If an input resource is not available, an <code>error.noresource</code>
 * event must be thrown.
 * </p>
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
public interface SpokenInput
        extends ExternalResource, InputDevice {
    /**
     * Sets the input stream, where the input should be obtained.
     *
     * <p>
     * <b>Note:</b> Unfortunately this is not a feature of all TTS engines. If
     * no <code>InputStream</code> is returned, the default input of the
     * recognizer engine is used. This may have consequences on the usability
     * with a calling device.
     * </p>
     *
     * @param in
     *        The input to use.
     * @param listener
     *        The output listener to inform about output events that are
     *        retrieved while communicating with the client.
     *
     * @exception NoresourceError
     *            The input resource is not available or this feature is
     *            not supported by the implementation.
     */
    void setInputStream(final InputStream in,
                        final SystemOutputListener listener)
            throws NoresourceError;

    /**
     * Activates the given grammar.
     *
     * @param grammars
     *        Grammars to activate.
     * @exception BadFetchError
     *            Grammar is not know by the recognizer.
     * @exception UnsupportedLanguageError
     *            The specified language is not supported.
     * @exception NoresourceError
     *            The input resource is not available.
     */
    void activateGrammars(Collection<RuleGrammar> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError;

    /**
     * Dectivates the given grammar. Do nothing if the input resource is not
     * availabale.
     *
     * @param grammars
     *        Grammars to deactivate.
     *
     * @exception BadFetchError
     *            Grammar is not know by the recognizer.
     * @exception NoresourceError
     *            The input resource is not available.
     */
    void deactivateGrammars(Collection<RuleGrammar> grammars)
            throws NoresourceError, BadFetchError;

    /**
     * Retrieves the barge-in types supported by this <code>UserInput</code>.
     * @return Collection of supported barge-in types, an empty
     * collection, if no types are supported.
     */
    Collection<BargeInType> getSupportedBargeInTypes();

    /**
     * Creates a RuleGrammar from Java Speech Grammar Format text provided by
     * the Reader. If the grammar contained in the Reader already exists, it is
     * over-written.
     *
     * @param reader The Reader from which the grammar text is loaded
     * @return RuleGrammar
     *         Read grammar.
     *
     * @since 0.3
     *
     * @exception NoresourceError
     *            The input resource is not available.
     * @exception BadFetchError
     *            Error reading the grammar.
     * @exception UnsupportedFormatError
     *            Invalid grammar format.
     */
    RuleGrammar loadGrammar(Reader reader)
            throws NoresourceError, BadFetchError, UnsupportedFormatError;

    /**
     * Records audio received from the user.
     *
     * @param out
     * OutputStream to write the recorded audio.
     * @exception NoresourceError
     * The input resource is not available.
     */
    void record(OutputStream out)
            throws NoresourceError;

    /**
     * Create a new RuleGrammar for this recognizer with a specified grammar
     * name.
     * @param name Name of the grammar to be created.
     * @return Created <code>RuleGrammar</code>.
     * @exception NoresourceError
     *            If the input device is not available.
     */
    RuleGrammar newGrammar(final String name)
            throws NoresourceError;

    /**
     * Activate this spoke input, when it is retrieved from the pool.
     * @since 0.6
     */
    void activate();

    /**
     * Passivates this spoken input, when it is returned to the pool.
     * @since 0.6
     */
    void passivate();
}
