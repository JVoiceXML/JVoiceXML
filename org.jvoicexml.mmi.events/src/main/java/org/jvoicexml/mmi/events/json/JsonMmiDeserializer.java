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
import java.util.Map;

import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.CancelResponse;
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.NewContextRequest;
import org.jvoicexml.mmi.events.NewContextResponse;
import org.jvoicexml.mmi.events.PauseRequest;
import org.jvoicexml.mmi.events.PauseResponse;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.ResumeRequest;
import org.jvoicexml.mmi.events.ResumeResponse;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.mmi.events.StatusRequest;
import org.jvoicexml.mmi.events.StatusResponse;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A deserializer for {@link JsonMmi}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class JsonMmiDeserializer implements JsonDeserializer<JsonMmi> {
    final static Map<String, Type> MMI_TYPES;

    static {
        MMI_TYPES = new java.util.HashMap<String, Type>();
        MMI_TYPES.put(CancelRequest.class.getSimpleName(), CancelRequest.class);
        MMI_TYPES.put(CancelResponse.class.getSimpleName(),
                CancelResponse.class);
        MMI_TYPES.put(ClearContextRequest.class.getSimpleName(),
                ClearContextRequest.class);
        MMI_TYPES.put(ClearContextResponse.class.getSimpleName(),
                ClearContextResponse.class);
        MMI_TYPES.put(DoneNotification.class.getSimpleName(),
                DoneNotification.class);
        MMI_TYPES.put(ExtensionNotification.class.getSimpleName(),
                ExtensionNotification.class);
        MMI_TYPES.put(NewContextRequest.class.getSimpleName(),
                NewContextRequest.class);
        MMI_TYPES.put(NewContextResponse.class.getSimpleName(),
                NewContextResponse.class);
        MMI_TYPES.put(PauseRequest.class.getSimpleName(), PauseRequest.class);
        MMI_TYPES.put(PauseResponse.class.getSimpleName(), PauseResponse.class);
        MMI_TYPES.put(PrepareRequest.class.getSimpleName(),
                PrepareRequest.class);
        MMI_TYPES.put(PrepareResponse.class.getSimpleName(),
                PrepareResponse.class);
        MMI_TYPES.put(ResumeRequest.class.getSimpleName(), ResumeRequest.class);
        MMI_TYPES.put(ResumeResponse.class.getSimpleName(),
                ResumeResponse.class);
        MMI_TYPES.put(StartRequest.class.getSimpleName(), StartRequest.class);
        MMI_TYPES.put(StartResponse.class.getSimpleName(), StartResponse.class);
        MMI_TYPES.put(StatusRequest.class.getSimpleName(), StatusRequest.class);
        MMI_TYPES.put(StatusResponse.class.getSimpleName(),
                StatusResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonMmi deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final JsonElement mmiTypeElement = object.get("mmi");
        final String typeName = mmiTypeElement.getAsString();
        final Type mmiType = MMI_TYPES.get(typeName);
        if (mmiType == null) {
            throw new JsonParseException(
                    "Unable to identify MMI type '" + typeName + "'");
        }
        final LifeCycleEvent event = context.deserialize(json, mmiType);
        return new JsonMmi(event);
    }

}
