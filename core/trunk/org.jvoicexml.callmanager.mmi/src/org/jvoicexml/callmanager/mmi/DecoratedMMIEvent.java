/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

import org.jvoicexml.mmi.events.LifeCycleEvent;

/**
 * An {@link MMIEvent} decorated with a source.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class DecoratedMMIEvent {

    /** The channel over which the {@link MMIEvent} was received. */
    private final Object channel;

    /** The received MMI event. */
    private final LifeCycleEvent event;

    /**
     * Constructs a new object.
     * @param ch channel over which the {@link MMIEvent} was received 
     * @param evt received MMI event
     */
    public DecoratedMMIEvent(final Object ch, final LifeCycleEvent evt) {
        channel = ch;
        event = evt;
    }

    /**
     * Retrieves the channel over which the {@link MMIEvent} was received.
     * @return the channel
     */
    public Object getChannel() {
        return channel;
    }

    /**
     * Retrieves the received MMI event.
     * @return the event
     */
    public LifeCycleEvent getEvent() {
        return event;
    }
}
