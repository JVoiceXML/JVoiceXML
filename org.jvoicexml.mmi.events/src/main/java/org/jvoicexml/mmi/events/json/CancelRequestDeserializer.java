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
import org.jvoicexml.mmi.events.CancelRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A deserializer for {@link CancelRequest}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class CancelRequestDeserializer implements JsonDeserializer<CancelRequest> {
    /** Type of the data field. */
    private final Type dataType;
    
    /**
     * Constructs a new object assuming the data field contains any
     * {@link Obejct}.
     */
    public CancelRequestDeserializer() {
        dataType = Object.class;
    }
    
    /**
     * Constructs a new object assuming the data field containing an object of
     * type {@code type}.
     * @param type type of the object in the data field
     */
    public CancelRequestDeserializer(final Type type) {
        dataType = type;
    }
    
    /**
     * Retrieves property {@code memberName} of {@code object} as a string.
     * @param object the JSON object
     * @param memberName the property to retreive
     * @return value of the property as a string
     */
    private String getAsString(final JsonObject object, final String memberName) {
        final JsonElement element = object.get(memberName);
        if (element == null) {
            return null;
        }
        return element.getAsString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CancelRequest deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final String requestId = getAsString(object, "requestID");
        final String source = getAsString(object, "source");
        final String target = getAsString(object, "target");
        final String ctx = getAsString(object, "context");
        final CancelRequest request = new CancelRequest(requestId, source, target, ctx);
        if (object.has("data")) {
            final AnyComplexType any = new AnyComplexType();
            final JsonElement dataElement = object.get("data");
            final JsonArray data = dataElement.getAsJsonArray();
            for (int i=0; i< data.size(); i++) {
                final JsonElement current = data.get(i);
                final Object o = context.deserialize(current, dataType);
                any.addContent(o);
            }
            request.setData(any);
        }
        return request;
    }

}
