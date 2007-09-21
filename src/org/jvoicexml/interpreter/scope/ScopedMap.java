/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 */

package org.jvoicexml.interpreter.scope;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * A simple {@link java.util.Map} which is scope aware.
 *
 * <p>
 * This enables the user to store in a map and retrieve these values
 * without taking care about scope changes. The values are always scope
 * aware.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @param <K> Key class of the map.
 * @param <V> Value class of the map.
 */
public final class ScopedMap<K, V>
        implements ScopeSubscriber, Map<K, V> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScopedMap.class);

    /** The encapsulated map. */
    private final Map<K, Stack<ScopedMapItem<V>>> map;

    /** The scope observer. */
    private final ScopeObserver observer;

    /** The current scope. */
    private Scope scope;

    /**
     * Construct a new object.
     * @param scopeObserver The current scope observer.
     */
    public ScopedMap(final ScopeObserver scopeObserver) {
        super();

        map = new java.util.HashMap<K, Stack<ScopedMapItem<V>>>();

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
        final Collection<Stack<ScopedMapItem<V>>> stacks = map.values();
        for (Stack<ScopedMapItem<V>> stack : stacks) {
            if (!stack.empty()) {
                final ScopedMapItem<V> item = stack.peek();
                if (item.getScope() == previous) {
                    stack.pop();
                }
            }
        }

        scope = next;
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the
     *   specified key.
     */
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *   specified value.
     */
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    /**
     * Returns the value to which this map maps the specified key.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *   <tt>null</tt> if the map contains no mapping for this key.
     */
    public V get(final Object key) {
        final Stack<ScopedMapItem<V>> stack = map.get(key);

        if (stack == null) {
            return null;
        }

        if (stack.empty()) {
            return null;
        }

        final ScopedMapItem<V> item = stack.peek();
        return item.getValue();
    }

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or
     *   <tt>null</tt> if there was no mapping for key. A <tt>null</tt>
     *   return can also indicate that the map previously associated
     *   <tt>null</tt> with the specified key, if the implementation
     *   supports <tt>null</tt> values.
     */
    public V put(final K key, final V value) {
        Stack<ScopedMapItem<V>> stack = map.get(key);
        ScopedMapItem<V> previousItem;

        if (stack == null) {
            stack = new Stack<ScopedMapItem<V>>();

            map.put(key, stack);

            previousItem = null;
        } else {
            if (stack.empty()) {
                previousItem = null;
            } else {
                previousItem = stack.peek();
            }
        }

        final ScopedMapItem<V> item = new ScopedMapItem<V>(scope, value);
        stack.push(item);

        if (previousItem == null) {
            return null;
        }

        return previousItem.getValue();
    }

    /**
     * Removes the mapping for this key from this map if it is present
     * (optional operation).
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or
     *   <tt>null</tt> if there was no mapping for key.
     */
    public V remove(final Object key) {
        final Stack<ScopedMapItem<V>> stack = map.remove(key);

        if (stack == null) {
            return null;
        }

        if (stack.empty()) {
            return null;
        }

        final ScopedMapItem<V> item = stack.peek();
        return item.getValue();
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).
     *
     * @param t Mappings to be stored in this map.
     */
    public void putAll(final Map<? extends K, ? extends V> t) {
    }

    /**
     * Removes all mappings from this map (optional operation).
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns a set view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Returns a collection view of the values contained in this map.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection<V> values() {
        final Collection<Stack<ScopedMapItem<V>>> stacks = map.values();
        final Collection<V> values = new java.util.ArrayList<V>();

        for (Stack<ScopedMapItem<V>> stack : stacks) {
            if (!stack.empty()) {
                final ScopedMapItem<V> item = stack.peek();
                final V value = item.getValue();
                values.add(value);
            }
        }

        return values;
    }

    /**
     * Returns a set view of the mappings contained in this map.
     *
     * @return a set view of the mappings contained in this map.
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    /**
     * Compares the specified object with this map for equality.
     *
     * @param o object to be compared for equality with this map.
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    public boolean equals(final Object o) {
        return map.equals(o);
    }

    /**
     * Returns the hash code value for this map.
     *
     * @return the hash code value for this map.
     */
    public int hashCode() {
        return map.hashCode();
    }
}
