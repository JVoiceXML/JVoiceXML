/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.datamodel;

/**
 * Represents a key-value pair.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class KeyValuePair {
    /** The key. */
    private final String key;

    /** The value. */
    private final Object value;

    /**
     * Creates a new object.
     * 
     * @param k
     *            the key
     * @param v
     *            the value
     */
    public KeyValuePair(final String k, final Object v) {
        key = k;
        value = v;
    }

    /**
     * Retrieves the key.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Retrieves the value.
     * 
     * @return the value
     */
    public Object getValue() {
        return value;
    }
}
