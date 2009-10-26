/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Collection that holds all elements of the current scope.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 * @param <E> type of elements in the collection.
 */
final class ScopedCollectionItem<E>
        implements Collection<E> {
    /** The encapsulated collection. */
    private final Collection<E> collection;

    /** The scope of this collection. */
    private final Scope scope;

    /**
     * Constructs a new object.
     * @param s the scope of this collection.
     */
    public ScopedCollectionItem(final Scope s) {
        collection = new java.util.ArrayList<E>();
        scope = s;
    }

    /**
     * Retrieves the current scope.
     * @return The current scope.
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(final E e) {
        return collection.add(e);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(final Collection<? extends E> c) {
        return collection.addAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        collection.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        return collection.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(final Collection<?> c) {
        return collection.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator() {
        return collection.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(final Object o) {
        return collection.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(final Collection<?> c) {
        return collection.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(final Collection<?> c) {
        return collection.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return collection.size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return collection.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(final T[] a) {
        return collection.toArray(a);
    }
}
