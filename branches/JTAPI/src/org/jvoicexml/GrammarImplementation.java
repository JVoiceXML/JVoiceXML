/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Implementation of a grammar that is accessed by the VoiceXML interpreter.
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
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 *
 * @param <T> the grammar implementation.
 */
public interface GrammarImplementation<T extends Object> {
    /**
     * Returns the declared media type of the external grammar.
     *
     * @return The media type of the grammar file.
     */
    GrammarType getMediaType();

    /**
     * Retrieves the grammar object.
     * @return the grammar.
     */
    T getGrammar();

    /**
     * Retrieves the unique identifier for this grammar.
     *
     * <p>
     * Grammars are distinguished by their name.
     * </p>
     *
     * @return name of the grammar.
     */
    String getName();
}
