/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a grammar that is accessed by the VoiceXML interpreter and
 * passed to the {@link org.jvoicexml.ImplementationPlatform} if the grammar is
 * activated or deactivated.
 *
 * <p>
 * VoiceXML is designed to support at least
 * </p>
 * <ul>
 * <li>JSGF</li>
 * <li>SRGS grammar with ABNF format</li>
 * <li>SRGS grammar with XML format</li>
 * </ul>
 * Custom implementations must implement this interface to hook their own
 * grammar specification.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.5.5
 *
 * @param <T>
 *            the grammar implementation.
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
     * 
     * @return mode type of the grammar.
     * @since 0.7
     */
    ModeType getModeType();

    /**
     * Retrieves the grammar document.
     * 
     * @return the grammar.
     */
    T getGrammarDocument();

    /**
     * Retrieves the URI of the grammar.
     * 
     * @return URI of the grammar document.
     * @since 0.7.5
     */
    URI getURI();

    /**
     * Checks if this grammar implementation is equal to the given grammar
     * implementation.
     * 
     * @param other
     *            the grammar implementation to compare with.
     * @return <code>true</code> if the grammar implementations are equal.
     * @since 0.7.2
     */
    boolean equals(GrammarImplementation<T> other);
}
