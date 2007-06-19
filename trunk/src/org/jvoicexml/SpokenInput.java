/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml;

import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.srgs.GrammarType;
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
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public interface SpokenInput
        extends ExternalResource, InputDevice, RemoteConnectable {
    /**
     * Retrieves the grammar types that are supported by this implementation.
     * @return supported grammars.
     *
     * @since 0.5.5
     */
    Collection<GrammarType> getSupportedGrammarTypes();

    /**
     * Activates the given grammars.
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
    void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError;

    /**
     * Deactivates the given grammar. Do nothing if the input resource is not
     * available.
     *
     * @param grammars
     *        Grammars to deactivate.
     *
     * @exception BadFetchError
     *            Grammar is not known by the recognizer.
     * @exception NoresourceError
     *            The input resource is not available.
     */
    void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws NoresourceError, BadFetchError;

    /**
     * Creates a {@link GrammarImplementation} from the contents provided by
     * the Reader. If the grammar contained in the Reader already exists, it is
     * over-written.
     *
     * @param reader The Reader from which the grammar text is loaded
     * @param type type of the grammar to read. The type is one of the supported
     *             types of the implementation, that has been requested via
     *             {@link #getSupportedGrammarTypes()}.
     * @return Read grammar.
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
    GrammarImplementation<? extends Object> loadGrammar(final Reader reader,
            final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError;

    /**
     * Creates a new grammar of the given type for this recognizer with a
     * specified grammar name.
     * @param name Name of the grammar to be created.
     * @param type type of the grammar to read. The type is one of the
     *             supported types of the implementation, that has been
     *             requested via {@link #getSupportedGrammarTypes()}.
     * @return Created grammar.
     * @exception NoresourceError
     *            If the input device is not available.
     * @todo Check if we can ommit the name parameter, since this may be not
     *       unique for other gramamr types.
     */
    GrammarImplementation<? extends Object> newGrammar(final String name,
            final GrammarType type)
            throws NoresourceError;

    /**
     * Retrieves the barge-in types supported by this <code>UserInput</code>.
     * @return Collection of supported barge-in types, an empty
     * collection, if no types are supported.
     */
    Collection<BargeInType> getSupportedBargeInTypes();

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
     * Activate this spoke input, when it is retrieved from the pool.
     * @since 0.5.5
     */
    void activate();

    /**
     * Passivates this spoken input, when it is returned to the pool.
     * @since 0.5.5
     */
    void passivate();
}
