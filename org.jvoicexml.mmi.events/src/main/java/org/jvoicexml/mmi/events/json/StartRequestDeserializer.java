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

import org.jvoicexml.mmi.events.StatusRequest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A deserializer for {@link StatusRequest}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class StartRequestDeserializer
    extends LifeCycleEventDeserializer<StatusRequest> {
    /**
     * Constructs a new object assuming the data field contains any
     * {@link Object}.
     */
    StartRequestDeserializer() {
    }
    
    /**
     * Constructs a new object assuming the data field containing an object of
     * type {@code type}.
     * @param type type of the object in the data field
     */
    StartRequestDeserializer(final Type type) {
        super(type);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StatusRequest deserialize(final JsonElement json,
            final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
        final StatusRequest request =
                super.deserialize(json, typeOfT, context);
        final JsonObject object = json.getAsJsonObject();
        if (object.has("context")) {
            final String ctx = getAsString(object, "context");
            request.setContext(ctx);
        }
        return request;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    StatusRequest createLifeCycleEvent() {
        return new StatusRequest();
    }
}
