/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/scope/ScopedMapItem.java $
 * Version: $LastChangedRevision: 3839 $
 * Date:    $Date: 2013-07-17 09:37:34 +0200 (Wed, 17 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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


/**
 * An entry of the {@link ScopedMap}.
 *
 * <p>
 * Main purpose of this class is to create a relationship of a map value
 * with a scope.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3839 $
 * @since 0.3
 *
 * @see ScopedMap
 * @see Scope
 *
 * @param <V> Value class of the item.
 */
final class ScopedMapItem<V> {
    /** The scope of this item. */
    private final Scope scope;

    /**
     * The current value.
     */
    private final V value;

    /**
     * Construct a new object.
     * @param s The scope of this map item.
     * @param v The value of this map item.
     */
    public ScopedMapItem(final Scope s, final V v) {
        scope = s;
        value = v;
    }

    /**
     * Retrieves the current scope.
     * @return The current scope.
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * The value of this map item.
     * @return Value of this map item.
     */
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();

        str.append('[');
        str.append(scope);
        str.append(", ");
        str.append(value);
        str.append(']');

        return str.toString();
    }
}
