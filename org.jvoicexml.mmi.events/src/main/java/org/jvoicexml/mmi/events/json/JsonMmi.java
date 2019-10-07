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
     * 
     * @param ev
     *            the lifecycle event
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
                .registerTypeAdapter(JsonMmi.class, new JsonMmiSerializer())
                .registerTypeAdapter(AnyComplexType.class,
                        new AnyComplexTypeSerializer());
        final Gson gson = builder.create();
        return gson.toJson(this);
    }

    /**
     * Converts JSON into a {@link JsonMmi} object.
     * 
     * @param json
     *            the JSON to parse
     * @return parsed object
     */
    public static JsonMmi fromJson(final String json) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(JsonMmi.class, new JsonMmiDeserializer())
                .registerTypeAdapter(CancelRequest.class,
                        new CancelRequestDeserializer())
                .registerTypeAdapter(CancelResponse.class,
                        new CancelResponseDeserializer())
                .registerTypeAdapter(ClearContextRequest.class,
                        new ClearContextRequestDeserializer())
                .registerTypeAdapter(ClearContextResponse.class,
                        new ClearContextResponseDeserializer())
                .registerTypeAdapter(DoneNotification.class,
                        new DoneNotificationDeserializer())
                .registerTypeAdapter(ExtensionNotification.class,
                        new ExtensionNotificationDeserializer())
                .registerTypeAdapter(NewContextRequest.class,
                        new NewContextRequestDeserializer())
                .registerTypeAdapter(NewContextResponse.class,
                        new NewContextResponseDeserializer())
                .registerTypeAdapter(PauseRequest.class,
                        new PauseRequestDeserializer())
                .registerTypeAdapter(PauseResponse.class,
                        new PauseResponseDeserializer())
                .registerTypeAdapter(PrepareRequest.class,
                        new PrepareRequestDeserializer())
                .registerTypeAdapter(PrepareResponse.class,
                        new PrepareResponseDeserializer())
                .registerTypeAdapter(ResumeRequest.class,
                        new ResumeRequestDeserializer())
                .registerTypeAdapter(ResumeResponse.class,
                        new ResumeResponseDeserializer())
                .registerTypeAdapter(StartRequest.class,
                        new StartRequestDeserializer())
                .registerTypeAdapter(StartResponse.class,
                        new StartResponseDeserializer())
                .registerTypeAdapter(StatusRequest.class,
                        new StatusRequestDeserializer())
                .registerTypeAdapter(StatusResponse.class,
                        new StatusResponseDeserializer());
        final Gson gson = builder.create();
        return gson.fromJson(json, JsonMmi.class);
    }

    /**
     * Converts JSON into a {@link JsonMmi} object.
     * 
     * @param json
     *            the JSON to parse
     * @param data
     *            type of the object in the data section
     * @return parsed object
     */
    public static JsonMmi fromJson(final String json, final Type data,
            final Type statusInfo,
            final JsonDeserializerConfiguration... deserializers) {
        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(JsonMmi.class, new JsonMmiDeserializer())
                .registerTypeAdapter(CancelRequest.class,
                        new CancelRequestDeserializer(data))
                .registerTypeAdapter(CancelResponse.class,
                        new CancelResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(ClearContextRequest.class,
                        new ClearContextRequestDeserializer(data))
                .registerTypeAdapter(ClearContextResponse.class,
                        new ClearContextResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(DoneNotification.class,
                        new DoneNotificationDeserializer(data, statusInfo))
                .registerTypeAdapter(ExtensionNotification.class,
                        new ExtensionNotificationDeserializer(data))
                .registerTypeAdapter(NewContextRequest.class,
                        new NewContextRequestDeserializer(data))
                .registerTypeAdapter(NewContextResponse.class,
                        new NewContextResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(PauseRequest.class,
                        new PauseRequestDeserializer(data))
                .registerTypeAdapter(PauseResponse.class,
                        new PauseResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(PrepareRequest.class,
                        new PrepareRequestDeserializer(data))
                .registerTypeAdapter(PrepareResponse.class,
                        new PrepareResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(ResumeRequest.class,
                        new ResumeRequestDeserializer(data))
                .registerTypeAdapter(ResumeResponse.class,
                        new ResumeResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(StartRequest.class,
                        new StartRequestDeserializer(data))
                .registerTypeAdapter(StartResponse.class,
                        new StartResponseDeserializer(data, statusInfo))
                .registerTypeAdapter(StatusRequest.class,
                        new StatusRequestDeserializer(data))
                .registerTypeAdapter(StatusResponse.class,
                        new StatusResponseDeserializer(data, statusInfo));
        for (JsonDeserializerConfiguration current : deserializers) {
            final Type type = current.getType();
            final JsonDeserializer<?> deserializer = current.getDeserializer();
            builder.registerTypeAdapter(type, deserializer);
        }
        final Gson gson = builder.create();
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
