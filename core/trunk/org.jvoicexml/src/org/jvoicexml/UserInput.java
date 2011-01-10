/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;



/**
 * Facade for easy control and monitoring of the user's input.
 *
 * <p>
 * Objects that implement this interface are able to detect and report character
 * and/or spoken input simultaneously and to control input detection interval
 * duration with a timer whose length is specified by a VoiceXML document.
 * </p>
 *
 * <p>
 * If an input resource is not available, an <code>error.noresource</code>
 * event must be thrown.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public interface UserInput {
    /**
     * Detects and reports character and/or spoken input simultaneously.
     *
     * @exception NoresourceError
     * The input resource is not available.
     * @exception BadFetchError
     * The active grammar contains some errors.
     */
    void startRecognition()
            throws NoresourceError, BadFetchError;

    /**
     * Stops a previously started recognition.
     *
     * @see #startRecognition
     */
    void stopRecognition();

    /**
     * Retrieves the grammar types that are supported by this implementation
     * for the given mode.
     * <p>
     * It is guaranteed that the implementation is only asked to load grammars
     * via the {@link #loadGrammar(Reader, GrammarType)} method or
     * activate ({@link #activateGrammars(Collection)}) and deactivate
     * ({@link UserInput#deactivateGrammars(Collection)}) grammars whos format
     * is returned by this method.
     * </p>
     *
     * @return supported grammars.
     * @param mode grammar mode
     *
     * @since 0.5.5
     */
    Collection<GrammarType> getSupportedGrammarTypes(final ModeType mode);

    /**
     * Activates the given grammars. It is guaranteed that all grammars types
     * are supported by this implementation. The supported grammar types are
     * retrieved from {@link #getSupportedGrammarTypes(ModeType)}.
     *
     * <p>
     * {@link GrammarImplementation}s may be cached. This means that a grammar
     * implementation object is loaded either by this or by another instance of
     * the {@link UserInput}. For some implementation platforms it may be
     * necessary that the instance activating the grammar also loaded the
     * grammar. In these cases, the grammar implementation must be loaded in
     * this call. The grammar source may ba accessed by the grammar
     * implementation itself, e.g. SRGS grammar sources can be accessed via
     * {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#getGrammar()}.
     * </p>
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
     * This method is mainly needed for non SRGS grammars, e.g. JSGF. Loading 
     * an SRGS grammar is quite easy and can be implemented e.g. as
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
     *             {@link #getSupportedGrammarTypes(ModeType)}.
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
}
