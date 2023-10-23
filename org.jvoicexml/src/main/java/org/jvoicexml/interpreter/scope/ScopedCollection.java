/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple {@link java.util.Collection} which is scope aware.
 *
 * <p>
 * This enables the user to store a collection and retrieve these values
 * without taking care about scope changes. The values are always scope
 * aware.
 * </p>
 * <p>
 * Views onto this collection, like iterating over its elements, will always
 * return those elements from the topmost scope prior to elements from lower
 * scopes.
 * </p>
 *
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 * @param <E> Type of the elements in this collection.
 */
public final class ScopedCollection<E>
    implements ScopeSubscriber, Collection<E> {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(ScopedCollection.class);

    /** The scope stack. All changes are made to the topmost item. */
    private final Stack<ScopedCollectionItem<E>> stack;

    /** A view onto all items of all elements. */
    private final Collection<E> view;

    /** Known listeners to scope changes. */
    private final Collection<ScopedCollectionListener<E>> listeners;
    
    /** {@code true} if the view must be recreated. */
    private boolean needRecreatedView;
    
    /** The scope observer. */
    private final ScopeObserver observer;

    /** The current scope. */
    private Scope scope;

    /**
     * Constructs a new object.
     * @param scopeObserver The current scope observer.
     */
    public ScopedCollection(final ScopeObserver scopeObserver) {
        stack = new Stack<ScopedCollectionItem<E>>();
        view = new java.util.ArrayList<E>();
        listeners = new java.util.ArrayList<ScopedCollectionListener<E>>();
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
     * Retrieves the current scope of this scoped collection.
     * @return the current scope
     * @since 0.7.9
     */
    public Scope getCurrentScope() {
        return scope;
    }

    /**
     * Adds the provided listener to the list of known scoped collection
     * listeners.
     * @param listener the listener to add
     * @since 0.7.9
     */
    public void addScopedCollectionListener(
            final ScopedCollectionListener<E> listener) {
        listeners.add(listener);
    }

    /**
     * Removes the provided listener from the list of known scoped collection
     * listeners.
     * @param listener the listener to add
     * @since 0.7.9
     */
    public void removeScopedCollectionListener(
            final ScopedCollectionListener<E> listener) {
        listeners.add(listener);
    }

    /**
     * Unsubscribe this scoped container from the {@link ScopePublisher}.
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
                
                // Notify the listeners about this change
                for (ScopedCollectionListener<E> listener : listeners) {
                    listener.removedForScopeChange(previous, next, item);
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
    private ScopedCollectionItem<E> getTopmostCollection() {
        ScopedCollectionItem<E> item = null;
        if (!stack.empty()) {
            item = stack.peek();
        }

        // Create a new  entry in the scope stack if there are no items
        // on the stack or if the topmost scope differs from the current scope.
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
     * Retrieves the current view onto all elements.
     * @return current view
     * @since 0.7.7
     */
    private Collection<E> getView() {
        if (needRecreatedView) {
            view.clear();
            for (int i = stack.size() - 1; i >= 0; i--) {
                final ScopedCollectionItem<E> item = stack.get(i);
                view.addAll(item);
            }
            needRecreatedView = false;
        }
        return view;
    }
    /**
     * {@inheritDoc}
     */
    public boolean add(final E e) {
        needRecreatedView = true;
        final ScopedCollectionItem<E> collection = getTopmostCollection();
        return collection.add(e);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(final Collection<? extends E> c) {
        needRecreatedView = true;
        final ScopedCollectionItem<E> collection = getTopmostCollection();
        return collection.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        needRecreatedView = true;
        stack.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        final Collection<E> currentView = getView();
        return currentView.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(final Collection<?> c) {
        final Collection<E> currentView = getView();
        return currentView.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        final Collection<E> currentView = getView();
        return currentView.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator() {
        final Collection<E> currentView = getView();
        return currentView.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(final Object o) {
        // Iterate over the stack and try to find the collection where
        // the item has been added
        for (ScopedCollectionItem<E> item : stack) {
            if (item.remove(o)) {
                needRecreatedView = true;
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
                needRecreatedView = true;
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
        final Collection<E> currentView = getView();
        return currentView.size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        final Collection<E> currentView = getView();
        return currentView.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(final T[] a) {
        final Collection<E> currentView = getView();
        return currentView.toArray(a);
    }
}

