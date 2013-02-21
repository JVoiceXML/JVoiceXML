/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;
import java.util.Set;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.implementation.GrammarImplementation;


/**
 * The grammars that have been processed by the grammar processor.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class GrammarCache {
    /** Set of active grammars. */
    private final Set<ProcessedGrammar> grammars;

    /**
     * Constructs a new object.
     */
    public GrammarCache() {
        grammars = new java.util.HashSet<ProcessedGrammar>();
    }

    /**
     * Retrieves the number of active grammars.
     * @return number of active grammars
     */
    public int size() {
        return grammars.size();
    }

    /**
     * Adds the given grammar to the active grammar set.
     * @param grammar the grammar to add
     */
    public void add(final ProcessedGrammar grammar) {
        grammars.add(grammar);
    }

    /**
     * Retrieves the set of active grammar implementations.
     * @return set of active grammar implementations.
     */
    public Collection<GrammarImplementation<?>> getImplementations() {
        final Collection<GrammarImplementation<?>> col =
            new java.util.ArrayList<GrammarImplementation<?>>();
        for (ProcessedGrammar grammar : grammars) {
            final GrammarImplementation<?> impl = grammar.getImplementation();
            col.add(impl);
        }
        return col;
    }

    /**
     * Retrieves the processed grammar for the given document.
     * @param document the grammar document to look for
     * @return the processed grammar, <code>null</code> if there is no
     *         processed grammar.
     */
    public ProcessedGrammar get(final GrammarDocument document) {
        for (ProcessedGrammar grammar : grammars) {
            final GrammarDocument current = grammar.getDocument();
            if (current.equals(document)) {
                return grammar;
            }
        }
        return null;
    }

    /**
     * Checks if the active grammar set contains the given grammar
     * document.
     * @param document the grammar document to look for.
     * @return <code>true</code> if the active grammar set contains the
     *          given grammar document
     */
    public boolean contains(final GrammarDocument document) {
        for (ProcessedGrammar grammar : grammars) {
            final GrammarDocument current = grammar.getDocument();
            if (current.equals(document)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the active grammar set contains the given grammar
     * implementation.
     * @param implementation the grammar implementation to look for.
     * @return <code>true</code> if the active grammar set contains the
     *          given grammar implementation
     */
    public boolean contains(final GrammarImplementation<?> implementation) {
        for (ProcessedGrammar grammar : grammars) {
            final GrammarImplementation<?> current =
                grammar.getImplementation();
            if (current.equals(implementation)) {
                return true;
            }
        }
        return false;
    }
}
