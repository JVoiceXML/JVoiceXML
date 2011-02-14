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
package org.jvoicexml.interpreter;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedSet;
import org.jvoicexml.interpreter.scope.ScopedSetObserver;


/**
 * The set of grammars active during a VoiceXML interpreter context's input
 * collection operation.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class ActiveGrammarSet
    implements ScopedSetObserver<ProcessedGrammar> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ActiveGrammarSet.class);

    /** Set of active grammars. */
    private final ScopedSet<ProcessedGrammar> grammars;

    /** Scope change observers. */
    private final Collection<ActiveGrammarSetObserver> observers;

    /**
     * Constructs a new object.
     * @param scopeObserver The current scope observer.
     */
    public ActiveGrammarSet(final ScopeObserver scopeObserver) {
        observers = new java.util.ArrayList<ActiveGrammarSetObserver>();
        grammars = new ScopedSet<ProcessedGrammar>(scopeObserver);
        grammars.addScopedSetObserver(this);
    }

    /**
     * Adds the given observer to the list of known observers.
     * @param obs the observer to add
     * @since 0.7.3
     */
    public void addActiveGrammarSetObserver(
            final ActiveGrammarSetObserver obs) {
        synchronized (observers) {
            observers.add(obs);
        }
    }

    /**
     * Removes the given scope observer from the list of known observers.
     * @param obs the observer to remove
     * @since 0.7.3
     */
    public void removeActiveGrammarSetObserver(
            final ActiveGrammarSetObserver obs) {
        synchronized (observers) {
            observers.remove(obs);
        }
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added 1 grammar - now containing " + grammars.size());
        }
    }

    /**
     * Adds the given grammars to the active grammar set.
     * @param grams the grammar to add
     */
    public void addAll(final Collection<ProcessedGrammar> grams) {
        grammars.addAll(grams);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added " + grams.size()
                    + " grammar(s) - now containing " + grammars.size());
        }
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
        for (ProcessedGrammar grammar : grammars) {
            final GrammarImplementation<?> current =
                grammar.getImplementation();
            if (!implementations.contains(current)) {
                col.add(current);
            }
        }
        return col;
    }

    /**
     * Filters all grammar implementations from this set that are not contained
     * in the given collection of grammar implementations.
     * @param implementations grammar implementations.
     * @return grammar implementations of this set that are not contained
     * in the given collection of grammar implementations
     * @since 0.7.3
     */
    public Collection<GrammarImplementation<?>> filter(
            final Collection<GrammarImplementation<?>> implementations) {
        final Collection<GrammarImplementation<?>> col =
            new java.util.ArrayList<GrammarImplementation<?>>();
        for (ProcessedGrammar grammar : grammars) {
            final GrammarImplementation<?> implementation =
                grammar.getImplementation();
            if (!implementations.contains(implementation)) {
                col.add(implementation);
            }
        }
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopedSetChange(final ScopedSet<ProcessedGrammar> set,
            final Collection<ProcessedGrammar> removed) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removed " + removed.size() + " grammars - "
                    + grammars.size() + " grammars remaining");
        }
        synchronized (observers) {
            for (ActiveGrammarSetObserver obs : observers) {
                obs.removedGrammars(this, removed);
            }
        }
    }
}
