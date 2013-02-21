/*
 * File:    $RCSfile: GenericVoiceXmlEvent.java,v $
 * Version: $Revision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $Author: schnelle $
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

package org.jvoicexml.event;

/**
 * Generic <code>VoiceXmlEvent</code> that is thrown from a
 * <code>&lt;throw&gt;</code> tag.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 */
public final class GenericVoiceXmlEvent
        extends JVoiceXMLEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 7370799172366948455L;

    /** The event type. */
    private final String eventType;

    /**
     * Constructs a new event with the given event type as its detail message.
     * The cause is not initialized.
     *
     * @param type
     *        The event type for this event.
     */
    public GenericVoiceXmlEvent(final String type) {
        super();

        eventType = type;
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * @param type
     *        The event type for this event.

     * @param message
     *        The detail message.
     */
    public GenericVoiceXmlEvent(final String type, final String message) {
        super(message);

        eventType = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return eventType;
    }
}
