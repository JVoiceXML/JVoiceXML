/*
 * File:    $RCSfile: GotoNextFormEvent.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * The FIA processed a <code>&lt;goto&gt;</code> event to another form.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.3
 */
public final class GotoNextFormEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -6012881759947791405L;

    /** The detail message. */
    public static final String EVENT_TYPE = GotoNextFormEvent.class.getName();

    /** name of the next form. */
    private final String form;

    /**
     * Construct a new object.
     * @param name
     *        name of the next form.
     */
    public GotoNextFormEvent(final String name) {
        form = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the name of the next form.
     * @return name of the next form.
     */
    public String getForm() {
        return form;
    }
}
