/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter.scope;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * A simple {@link java.util.Set} which is scope aware.
 *
 * <p>
 * This enables the user to store in a set and retrieve these values
 * without taking care about scope changes. The values are always scope
 * aware.
 * </p>
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 * @param <E> Type of the elements in this set.
 */
public final class ScopedSet<E>
    implements ScopeSubscriber, Set<E> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScopedSet.class);

    /** Scope stack. */
    private final Stack<ScopedCollectionItem<E>> stack;

    /** A view onto all items of all elements. */
    private final Set<E> view;

    /** The scope observer. */
    private final ScopeObserver observer;

    /** The current scope. */
    private Scope scope;

    /** Scope change observers. */
    private final Collection<ScopedSetObserver<E>> observers;

    /**
     * Constructs a new object.
     * @param scopeObserver The current scope observer.
     */
    public ScopedSet(final ScopeObserver scopeObserver) {
        stack = new Stack<ScopedCollectionItem<E>>();
        view = new java.util.HashSet<E>();
        observers = new java.util.ArrayList<ScopedSetObserver<E>>();

        if (scopeObserver != null) {
            observer = scopeObserver;
            observer.addScopeSubscriber(this);
            scope = observer.currentScope();
        } else {
            LOGGER.warn("no monitoring of scope transitions possible");
            observer = null;
            scope = null;
        }
    }

    /**
     * Adds the given observer to the list of known observers.
     * @param obs the observer to add
     * @since 0.7.3
     */
    public void addScopedSetObserver(final ScopedSetObserver<E> obs) {
        synchronized (observers) {
            observers.add(obs);
        }
    }

    /**
     * Removes the given scope observer from the list of known observers.
     * @param obs the observer to remove
     * @since 0.7.3
     */
    public void removeScopedSetObserver(final ScopedSetObserver<E> obs) {
        synchronized (observers) {
            observers.remove(obs);
        }
    }

    /**
     * Unsubscribe this scoped container from the <code>ScopePublisher</code>.
     */
    public void close() {
        if (observer != null) {
            observer.removeScopeSubscriber(this);
        }
    }

    /**
     *{@inheritDoc}
     */
    public void enterScope(final Scope previous, final Scope next) {
        scope = next;
    }

    /**
     *{@inheritDoc}
     */
    public void exitScope(final Scope previous, final Scope next) {
        if (!stack.isEmpty()) {
            final ScopedCollectionItem<E> item = stack.peek();
            if (item.getScope() == previous) {
                stack.pop();
                view.removeAll(item);
            }

            // Notify all registered scoped set observers
            final Collection<E> removed = new java.util.ArrayList<E>();
            removed.addAll(item);
            synchronized (observers) {
                for (ScopedSetObserver<E> obs : observers) {
                    obs.scopedSetChange(this, removed);
                }
            }
        }

        scope = next;
    }

    /**
     * Retrieves the current collection. Usually this is the topmost item
     * from the stack. If the stack is empty, a new item is created and
     * pushed onto the stack
     *
     * @return current collection.
     */
    private ScopedCollectionItem<E> getCurrentCollection() {
        ScopedCollectionItem<E> item = null;
        if (!stack.empty()) {
            item = stack.peek();
        }

        if (item == null) {
            item = new ScopedCollectionItem<E>(scope);
            stack.push(item);
        } else if (item.getScope() != scope) {
            item = new ScopedCollectionItem<E>(scope);
            stack.push(item);
        }

        return item;
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(final E e) {
        if (view.contains(e)) {
            return false;
        }
        final ScopedCollectionItem<E> collection = getCurrentCollection();

        view.add(e);

        return collection.add(e);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(final Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }

        return modified;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        view.clear();
        stack.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        return view.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(final Collection<?> c) {
        return view.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return stack.empty();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator() {
        return view.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(final Object o) {
        if (!view.remove(o)) {
            return false;
        }

        for (ScopedCollectionItem<E> item : stack) {
            if (item.remove(o)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            if (remove(o) && !changed) {
                changed = true;
            }
        }

        return changed;
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return view.size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return view.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(final T[] a) {
        return view.toArray(a);
    }
}

