/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedSet;
import org.jvoicexml.interpreter.scope.ScopedSetObserver;


/**
 * The set of grammars active during a VoiceXML interpreter context's input
 * collection operation. This grammar set is scope aware. {@link ScopeObserver}s
 * can be added to monitor scope related changes in this grammar set.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 * @see org.jvoicexml.interpreter.scope.Scope
 */
public final class ActiveGrammarSet
    implements ScopedSetObserver<GrammarDocument> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ActiveGrammarSet.class);

    /** Set of active grammars. */
    private final ScopedSet<GrammarDocument> grammars;

    /** Scope change observers. */
    private final Collection<ActiveGrammarSetObserver> observers;

    /**
     * Constructs a new object.
     * @param scopeObserver The current scope observer.
     */
    public ActiveGrammarSet(final ScopeObserver scopeObserver) {
        observers = new java.util.ArrayList<ActiveGrammarSetObserver>();
        grammars = new ScopedSet<GrammarDocument>(scopeObserver);
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
    public void add(final GrammarDocument grammar) {
        grammars.add(grammar);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added 1 grammar - now containing " + grammars.size());
        }
    }

    /**
     * Adds the given grammars to the active grammar set.
     * @param grams the grammars to add
     */
    public void addAll(final Collection<GrammarDocument> grams) {
        grammars.addAll(grams);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added " + grams.size()
                    + " grammar(s) - now containing " + grammars.size());
        }
    }

    /**
     * Removes the given grammars to the active grammar set.
     * @param grams the grammars to remove
     * @since 0.7.6
     */
    public void removeAll(final Collection<GrammarDocument> grams) {
        grammars.removeAll(grams);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removed " + grams.size()
                    + " grammar(s) - now containing " + grammars.size());
        }
    }

    /**
     * Checks if the active grammar set contains the given grammar
     * document.
     * @param document the grammar document to look for.
     * @return <code>true</code> if the active grammar set contains the
     *          given grammar document
     */
    public boolean contains(final GrammarDocument document) {
        return grammars.contains(document);
    }

    /**
     * Retrieves the grammars that are currently contained in the set.
     * @return the grammars in the set
     * @since 0.7.5
     */
    public Collection<GrammarDocument> getGrammars() {
        return grammars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopedSetChange(final ScopedSet<GrammarDocument> set,
            final Collection<GrammarDocument> removed) {
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
