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

import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.LifeCycleEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A deserializer for {@link LifeCycleEvent}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
abstract class LifeCycleEventDeserializer<T extends LifeCycleEvent>
    implements JsonDeserializer<T> {
    /** Type of the data field. */
    private final Type dataType;
    
    /** The deserialized event. */
    protected T event;
    
    /**
     * Constructs a new object assuming the data field contains any
     * {@link Object}.
     */
    public LifeCycleEventDeserializer() {
        this(Object.class);
    }

    /**
     * Constructs a new object assuming the data field containing an object of
     * type {@code type}.
     * @param data type of the object in the data field
     */
    public LifeCycleEventDeserializer(final Type data) {
        dataType = data;
    }
    
    /**
     * Retrieves property {@code memberName} of {@code object} as a string.
     * @param object the JSON object
     * @param memberName the property to retreive
     * @return value of the property as a string
     */
    protected String getAsString(final JsonObject object, final String memberName) {
        final JsonElement element = object.get(memberName);
        if (element == null) {
            return null;
        }
        return element.getAsString();
    }
    
    /**
     * Creates the life cycle event to deserialize
     * @return lif cycle event to deserialize
     */
    abstract T createLifeCycleEvent();

    /**
     * Retrieves the lifecycle event to be deserialized.
     * @return life cycle event to be deserialized.
     */
    protected T getLifeCycleEvent() {
        if (event == null) {
            event = createLifeCycleEvent();
        }
        return event;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public T deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final T event = getLifeCycleEvent();
        final JsonObject object = json.getAsJsonObject();
        final String requestId = getAsString(object, "requestID");
        event.setRequestId(requestId);
        final String source = getAsString(object, "source");
        event.setSource(source);
        final String target = getAsString(object, "target");
        event.setTarget(target);
        if (object.has("data")) {
            final AnyComplexType any = new AnyComplexType();
            final JsonElement dataElement = object.get("data");
            final JsonArray data = dataElement.getAsJsonArray();
            for (int i=0; i< data.size(); i++) {
                final JsonElement current = data.get(i);
                final Object o = context.deserialize(current, dataType);
                any.addContent(o);
            }
            event.setData(any);
        }
        return event;
    }

}
