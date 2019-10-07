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
import org.jvoicexml.mmi.events.StatusResponse;
import org.jvoicexml.mmi.events.StatusResponseType;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A deserializer for {@link StatusResponse}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class StatusResponseDeserializer extends LifeCycleEventDeserializer<StatusResponse> {
    /** Type of the statusInfo field. */
    private final Type statusInfoType;

    /**
     * Constructs a new object assuming the data field contains any
     * {@link Object}.
     */
    public StatusResponseDeserializer() {
        this(Object.class, Object.class);
    }
    
    /**
     * Constructs a new object assuming the data field containing an object of
     * type {@code type}.
     * @param type type of the object in the data field
     * @param statusInfo type of the object in the status field
     */
    public StatusResponseDeserializer(final Type data, final Type statusInfo) {
        super(data);
        statusInfoType = statusInfo;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    StatusResponse createLifeCycleEvent() {
        return new StatusResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatusResponse deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final StatusResponse response =
                super.deserialize(json, typeOfT, context);
        final JsonObject object = json.getAsJsonObject();
        if (object.has("context")) {
            final String ctx = getAsString(object, "context");
            response.setContext(ctx);
        }
        final String statusString = getAsString(object, "status");
        final StatusResponseType status = StatusResponseType.valueOf(statusString);
        response.setStatus(status);
        if (object.has("statusInfo")) {
            final AnyComplexType any = new AnyComplexType();
            final JsonElement dataElement = object.get("statusInfo");
            final JsonArray statusInfo = dataElement.getAsJsonArray();
            for (int i=0; i< statusInfo.size(); i++) {
                final JsonElement current = statusInfo.get(i);
                final Object o = context.deserialize(current, statusInfoType);
                any.addContent(o);
            }
            response.setStatusInfo(any);
        }
        return response;
    }
}
