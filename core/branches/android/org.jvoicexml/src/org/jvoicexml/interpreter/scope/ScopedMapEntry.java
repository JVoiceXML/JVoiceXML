/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/scope/ScopedMapEntry.java $
 * Version: $LastChangedRevision: 2695 $
 * Date:    $Date: 2011-06-03 08:01:33 -0500 (vie, 03 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.scope;

import java.util.Map;

/**
 * An entry of the scoped map.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2695 $
 * @since 0.7.5
 * @param <K> key type
 * @param <V> value type
 */
class ScopedMapEntry<K, V> implements Map.Entry<K, V> {
    /** The key. */
    private final K key;

    /** The value. */
    private final ScopedMap<K, V> map;
    
    /**
     * Constructs a new object.
     * @param k the key.
     * @param scopedMap the underlying map
     */
    public ScopedMapEntry(final K k, final ScopedMap<K, V> scopedMap) {
        key = k;
        map = scopedMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V setValue(final V v) {
        return map.put(key, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(ScopedMapEntry.class.getCanonicalName());
        str.append('[');
        str.append(key);
        str.append(',');
        final V value = map.get(key);
        str.append(value);
        str.append(']');
        return str.toString();
    }
}
