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
import java.util.Objects;

import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.LifeCycleEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

/**
 * JSON support for MMI events.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JsonMmi {
    /** The event to serialize. */
    private LifeCycleEvent event;

    /**
     * Constructs a new object.
     */
    public JsonMmi() {
    }
    
    /**
     * Constructs a new objects with the given encapsulated lifecycle event.
     * @param ev the lifecycle event
     */
    public JsonMmi(final LifeCycleEvent ev) {
        event = ev;
    }

    /**
     * Sets the encapsulated lifecycle event.
     * 
     * @param ev
     *            the life cycle event.
     */
    public void setLifeCycleEvent(final LifeCycleEvent ev) {
        event = ev;
    }

    /**
     * Retrieves the lifecycle event.
     * 
     * @return the lifecycle event
     */
    public LifeCycleEvent getLifeCycleEvent() {
        return event;
    }

    /**
     * Converts this event to JSON
     * 
     * @return JSON representation of this object.
     */
    public String toJson() {
        final GsonBuilder builder = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(JsonMmi.class,
                        new JsonMmiSerializer())
                .registerTypeAdapter(AnyComplexType.class,
                        new AnyComplexTypeSerializer());
        final Gson gson = builder.create();
        return gson.toJson(this);
    }

    /**
     * Converts JSON into a {@link JsonMmi} object.
     * @param json the JSON to parse
     * @return parsed object
     */
    public static JsonMmi fromJson(final String json) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(JsonMmi.class, new JsonMmiDeserializer())
                .registerTypeAdapter(CancelRequest.class, new CancelRequestDeserializer());
        final Gson gson = builder.create();
        return gson.fromJson(json, JsonMmi.class);
    }

    /**
     * Converts JSON into a {@link JsonMmi} object.
     * @param json the JSON to parse
     * @param dataType type of the object in the data section
     * @return parsed object
     */
    public static JsonMmi fromJson(final String json, final Type dataType,
            final JsonDeserializerConfiguration... deserializers) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(JsonMmi.class, new JsonMmiDeserializer())
                .registerTypeAdapter(CancelRequest.class,
                        new CancelRequestDeserializer(dataType));
        for (JsonDeserializerConfiguration current : deserializers) {
            final Type type = current.getType();
            final JsonDeserializer<?> deserializer = current.getDeserializer();
            builder.registerTypeAdapter(type, deserializer);
        }
        final Gson gson =  builder.create();
        return gson.fromJson(json, JsonMmi.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JsonMmi)) {
            return false;
        }
        JsonMmi other = (JsonMmi) obj;
        return Objects.equals(event, other.event);
    }
}
