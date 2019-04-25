/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.jvxml;

import org.jvoicexml.event.PlainEvent;

/**
 * The FIA processed a <code>&lt;goto&gt;</code> event to another form item.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.3
 */
public final class GotoNextFormItemEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -5676541131276871270L;

    /** The detail message. */
    public static final String EVENT_TYPE =
            GotoNextFormItemEvent.class.getName();

    /** name of the next form item. */
    private final String item;

    /**
     * Construct a new object.
     * @param name
     *        name of the next form item.
     */
    public GotoNextFormItemEvent(final String name) {
        item = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the name of the next form item.
     * @return name of the next form item.
     */
    public String getItem() {
        return item;
    }
}
