/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedSet;


/**
 * The set of grammars active during a VoiceXML interpreter context's input
 * collection operation.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class ActiveGrammarSet {
    /** Set of active grammars. */
    private final ScopedSet<ProcessedGrammar> grammars;

    /**
     * Constructs a new object.
     * @param scopeObserver The current scope observer.
     */
    public ActiveGrammarSet(final ScopeObserver scopeObserver) {
        grammars = new ScopedSet<ProcessedGrammar>(scopeObserver);
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

    /**
     * Filters all grammar implementations from the given implementations
     * that are not contained in this grammar set.
     * @param implementations grammar implementations
     * @return grammar implementations that are not contained in this
     *         grammar set
     */
    public Collection<GrammarImplementation<?>> notContained(
            final Collection<GrammarImplementation<?>> implementations) {
        final Collection<GrammarImplementation<?>> col =
            new java.util.ArrayList<GrammarImplementation<?>>();
        for (GrammarImplementation<?> implementation : implementations) {
            if (!contains(implementation)) {
                col.add(implementation);
            }
        }
        return col;
    }
}
