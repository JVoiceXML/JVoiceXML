/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URI;
import java.util.Collection;

import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoauthorizationError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Facade for easy control and monitoring of the user's speech input as an
 * external resource.
 *
 * <p>
 * Objects that implement this interface are able to detect spoken input and to
 * control input detection interval duration with a timer whose length is
 * specified by a VoiceXML document.
 * </p>
 *
 * <p>
 * If an input resource is not available, an <code>error.noresource</code> event
 * must be thrown.
 * </p>
 * <p>
 * It is guaranteed that the session remains the same between the calls to
 * {@link org.jvoicexml.RemoteConnectable#connect(org.jvoicexml.ConnectionInformation)}
 * and
 * {@link org.jvoicexml.RemoteConnectable#disconnect(org.jvoicexml.ConnectionInformation)}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5
 */
public interface SpokenInput extends ExternalResource, InputDevice {
    /**
     * In case the user input supports {@link ModeType#VOICE} the input
     * must return the {@link SpeechRecognizerProperties} to use.
     * @return new instance of a {@link SpeechRecognizerProperties} or
     * {@code null} if this is not supported or the default should be used. 
     * @since 0.7.9
     */
    SpeechRecognizerProperties createSpeechRecognizerProperties();
    
    /**
     * In case the user input supports {@link ModeType#DTMF} the input
     * must return the {@link DtmfRecognizerProperties} to use.
     * @return new instance of a {@link DtmfRecognizerProperties} or
     * {@code null} if this is not supported or the default should be used. 
     * @since 0.7.9
     */
    DtmfRecognizerProperties createDtmfRecognizerProperties();

    /**
     * Retrieves the no input timeout that was provided in the
     * {@link org.jvoicexml.SpeechRecognizerProperties} when recognition was
     * started. This value will be used to start a no input timer if the
     * platform is not able to handle this. In case the platform is able
     * to prove a behavior as specified at
     * <a href="https://www.w3.org/TR/voicexml20/#dml4.1.7">https://www.w3.org/TR/voicexml20/#dml4.1.7</a>
     * a value smaller than 0 may be returned.
     * @return no input timeout to be used by the no input timer
     * @since 0.7.9
     */
    long getNoInputTimeout();

    /**
     * Retrieves the grammar types that are supported by this implementation.
     * 
     * @return supported grammars.
     *
     * @since 0.5.5
     */
    Collection<GrammarType> getSupportedGrammarTypes();

    /**
     * Activates the given grammars. It is guaranteed that all grammars types
     * are supported by this implementation.
     * <p>
     * It is guaranteed that the {@link #loadGrammar(URI, GrammarType)} method
     * is always called before a grammar becomes active via this method.
     * </p>
     * 
     * @param grammars
     *            Grammars to activate.
     * @exception BadFetchError
     *                Grammar is not known by the recognizer.
     * @exception UnsupportedLanguageError
     *                The specified language is not supported.
     * @exception UnsupportedFormatError
     *                the grammar format is not supported
     * @exception NoresourceError
     *                The input resource is not available.
     */
    void activateGrammars(Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError,
            UnsupportedFormatError, NoresourceError;

    /**
     * Deactivates the given grammar. Do nothing if the input resource is not
     * available. It is guaranteed that all grammars types are supported by this
     * implementation.
     *
     * @param grammars
     *            Grammars to deactivate.
     *
     * @exception BadFetchError
     *                Grammar is not known by the recognizer.
     * @exception NoresourceError
     *                The input resource is not available.
     */
    void deactivateGrammars(Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError;

    /**
     * Creates a {@link GrammarImplementation} from the contents provided by the
     * Reader. If the grammar contained in the Reader already exists, it is
     * over-written.
     *
     * <p>
     * This method is mainly needed for non SRGS grammars, e.g. JSGF. The
     * grammar implementation is platform specific, so it the responsibility of
     * the implementation platform to return load it's specific grammar.
     * However, loading an SRGS grammar is quite easy and can be implemented
     * e.g. as
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
     * @param uri
     *            the URI to load the grammar
     * @param type
     *            type of the grammar to read. The type is one of the supported
     *            types of the implementation, that has been requested via
     *            {@link #getSupportedGrammarTypes()}.
     *
     * @return Read grammar.
     *
     * @since 0.3
     *
     * @exception NoresourceError
     *                the input resource is not available.
     * @exception IOException
     *                error reading the grammar. Will be converted into
     *                {@link BadFetchError}
     * @exception UnsupportedFormatError
     *                invalid grammar format.
     * @exception SemanticError
     *                semantic error in the grammar file
     * @exception NoauthorizationError
     *                 the grammar could not be loaded because of security
     *                 constraints
     */
    GrammarImplementation<?> loadGrammar(URI uri, GrammarType type)
            throws NoresourceError, IOException, UnsupportedFormatError, 
                SemanticError, NoauthorizationError;

    /**
     * Retrieves the barge-in types supported by this <code>UserInput</code>.
     * 
     * @return Collection of supported barge-in types, an empty collection, if
     *         no types are supported.
     */
    Collection<BargeInType> getSupportedBargeInTypes();

    /**
     * Adds a listener for user input events.
     *
     * <p>
     * The implementation of this interface must notify the listener about all
     * events.
     * </p>
     *
     * @param listener
     *            The listener.
     * @since 0.5
     */
    void addListener(SpokenInputListener listener);

    /**
     * Removes a listener for user input events.
     *
     * @param listener
     *            The listener.
     * @since 0.6
     */
    void removeListener(SpokenInputListener listener);
}
