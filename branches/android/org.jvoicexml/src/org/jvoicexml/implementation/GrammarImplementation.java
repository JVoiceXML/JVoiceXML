/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/GrammarImplementation.java $
 * Version: $LastChangedRevision: 2632 $
 * Date:    $Date: 2011-05-04 16:52:27 -0500 (mié, 04 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a grammar that is accessed by the VoiceXML interpreter
 * and passed to the {@link org.jvoicexml.ImplementationPlatform} if the grammar
 * is activated or deactivated.
 *
 * <p>
 * VoiceXML is designed to support at least
 * <ul>
 * <li>JSGF</li>
 * <li>SRGS grammar with ABNF format</li>
 * <li>SRGS grammar with XML format</li>
 * </ul>
 * Custom implementations must implement this interface to hook their own
 * grammar specification.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2632 $
 *
 * @since 0.5.5
 *
 * @param <T> the grammar implementation.
 */
public interface GrammarImplementation<T> {
    /**
     * Returns the declared media type of the external grammar.
     *
     * @return The media type of the grammar file.
     */
    GrammarType getMediaType();

    /**
     * Returns the mode type of the grammar.
     * @return mode type of the grammar.
     * @since 0.7
     */
    ModeType getModeType();

    /**
     * Retrieves the grammar object.
     * @return the grammar.
     */
    T getGrammar();

    /**
     * Checks, if this grammar covers the given recognition result.
     * <p>
     * It is not legal to check if the result is accepted using
     * {@link RecognitionResult#isAccepted()}. Implementations
     * should assume that it is a valid input result.
     * </p>
     *
     * <p>
     * Implementations can use the
     * {@link org.jvoicexml.processor.srgs.GrammarChecker}.
     * </p>
     *
     * @param result the result to check.
     * @return <code>true</code> if the utterance is valid for this grammar
     * @since 0.7
     */
    boolean accepts(final RecognitionResult result);

    /**
     * Checks if this grammar implementation is equal to the given grammar
     * implementation.
     * @param other the grammar implementation to compare with.
     * @return <code>true</code> if the grammar implementations are equal.
     * @since 0.7.2
     */
    boolean equals(final GrammarImplementation<T> other);
}
