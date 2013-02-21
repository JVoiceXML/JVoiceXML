/*
 * File:    $RCSfile: ScopedMapItem.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * An entry of the <code>ScopedMap</code>.
 *
 * <p>
 * Main purpose of this class is to create a relationship of a map value
 * with a scope.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.3
 *
 * @see ScopedMap
 * @see Scope
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
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
