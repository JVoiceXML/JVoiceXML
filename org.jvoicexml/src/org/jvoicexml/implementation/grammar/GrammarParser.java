/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.grammar;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.event.error.SemanticError;

/**
 * Parser for a {@link GrammarImplementation} into a {@link GrammarGraph}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 * @version $Revision$
 * @param <T> type of the grammar that is parsed.
 */
public interface GrammarParser<T extends GrammarImplementation<?>> {
    /**
     * Parse the grammar.
     * @param grammar the grammar to parse.
     * @return a graph representing the grammar.
     * @exception SemanticError
     *            error parsing the document
     */
    GrammarGraph parse(final T grammar) throws SemanticError;
}
