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

import org.jvoicexml.mmi.events.LifeCycleEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A {@link JsonMmi} serializer for {@link JsonMmi}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class JsonMmiSerializer implements JsonSerializer<JsonMmi> {

    /**
     * Adds the type information as an additional field to the serialized JSON
     * object. 
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(JsonMmi src, Type typeOfSrc,
            JsonSerializationContext context) {
        final LifeCycleEvent event = src.getLifeCycleEvent();
        final JsonElement element = context.serialize(event);
        final JsonObject object = element.getAsJsonObject();
        final String eventName = event.getClass().getSimpleName();
        object.addProperty("mmi", eventName);
        return object;
    }

}
