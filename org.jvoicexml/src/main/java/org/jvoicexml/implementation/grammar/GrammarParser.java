/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A grammar processor is able to parse any {@link GrammarDocument} into
 * something that can be evaluated later to retrieve semantic interpretation
 * from a given utterance .
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 * @param <T> grammar implementation
 */
public interface GrammarParser<T> {
    /**
     * Retrieves the grammar type that this parser will process.
     * 
     * @return the grammar type
     */
    GrammarType getType();

    /**
     * Loads a grammar by its URI.
     * @param uri the URI to load the grammar from
     * @return grammar implementation
     * @throws IOException
     *         error loading the grammar
     */
    GrammarImplementation<T> load(URI uri) throws IOException;
    
    /**
     * Parses a given {@link GrammarDocument} into a {@link GrammarEvaluator}.
     * This also means that there is a 1:1 relationship between grammars and
     * their evaluators.
     * 
     * @param document
     *            the document to process.
     * @return grammar evaluator
     * @throws IOException
     *             error reading the grammar
     * @throws UnsupportedFormatError
     *             the grammar format is not supported
     */
    GrammarEvaluator parse(GrammarDocument document) throws IOException,
            UnsupportedFormatError;
}
