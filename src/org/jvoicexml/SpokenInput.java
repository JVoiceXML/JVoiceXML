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
     * Activates the given grammars. It is guaranteed that all grammars types
     * are supported by this implementation.
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
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError;

    /**
     * Deactivates the given grammar. Do nothing if the input resource is not
     * available. It is guaranteed that all grammars types are supported by this
     * implementation.
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
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError;

    /**
     * Creates a {@link GrammarImplementation} from the contents provided by
     * the Reader. If the grammar contained in the Reader already exists, it is
     * over-written.
     *
     * <p>
     * This method is mainly needed for non SRGS grammars, e.g. JSGF. If
     * the implementation supports SRGS the method may always throw an
     * {@link UnsupportedFormatError}. However, loading an SRGS grammar is
     * quite easy and can be implemented e.g. as
     * </p>
     * <p>
     * <code>
     * final InputSource inputSource = new InputSource(reader);<br>
     * SrgsXmlDocument doc = new SrgsXmlDocument(inputSource);<br>
     * &#47;&#47; Pass it to the recognizer<br>
     * return doc;
     * </code>
     * </p>
     *
     * @param reader The Reader from which the grammar text is loaded
     * @param type type of the grammar to read. The type is one of the supported
     *             types of the implementation, that has been requested via
     *             {@link #getSupportedGrammarTypes()}.
     *
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
    GrammarImplementation<?> loadGrammar(final Reader reader,
            final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError;

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
    void record(final OutputStream out)
            throws NoresourceError;

    /**
     * Activates this spoke input, when it is retrieved from the pool.
     * @since 0.5.5
     */
    void activate();

    /**
     * Passivates this spoken input, when it is returned to the pool.
     * @since 0.5.5
     */
    void passivate();
}
