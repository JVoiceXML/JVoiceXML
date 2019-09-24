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

import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.LifeCycleEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
     * @since 0.7.9
     */
    public String toJson() {
        final Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(JsonMmi.class,
                        new LifeCycleEventSerializer())
                .registerTypeAdapter(AnyComplexType.class,
                        new AnyComplexTypeSerializer())
                .create();
        return gson.toJson(this);
    }

}
