/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mmi.events.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializer;

/**
 * A JSON deserializer configuration.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public final class JsonDeserializerConfiguration {
    /** The type to deserialize. */
    private final Type type;
    /** The actual deserializer. */
    private final JsonDeserializer<?> deserializer;
    
    /**
     * Constructs a new object.
     * @param t the type to deserialize
     * @param d the actual deserializer
     */
    public JsonDeserializerConfiguration(final Type t, final JsonDeserializer<?> d) {
        type = t;
        deserializer = d;
    }
    
    /**
     * Retrieves the type to deserialize
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Retrieves the actual deserializer.
     * @return the deserializer
     */
    public JsonDeserializer<?> getDeserializer() {
        return deserializer;
    }
}
