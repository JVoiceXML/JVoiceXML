/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * An {@link LifeCycleEvent} decorated with a channel. A channel can uniquely be
 * identified within an {@link ETLProtocolAdapter}. Usually this will be the
 * instance that received the request and should send a response.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */

public final class DecoratedMMIEvent {

    /** The channel over which the {@link LifeCycleEvent} was received. */
    private final Object channel;

    /** The received MMI event. */
    private final Mmi mmi;

    /**
     * Constructs a new object.
     * 
     * @param ch
     *            channel over which the {@link LifeCycleEvent} was received
     * @param evt
     *            received MMI event
     */
    public DecoratedMMIEvent(final Object ch, final Mmi evt) {
        channel = ch;
        mmi = evt;
    }

    /**
     * Retrieves the channel over which the {@link LifeCycleEvent} was received.
     * 
     * @return the channel
     */
    public Object getChannel() {
        return channel;
    }

    /**
     * Retrieves the MMI event.
     * 
     * @return the MMI event
     * @since 0.7.7
     */
    public Mmi getMmi() {
        return mmi;
    }

    /**
     * Retrieves the received MMI lifecyle event.
     * 
     * @return the lifecycle event, maybe {@code null}
     */
    public LifeCycleEvent getLifeCycleEvent() {
        return mmi.getLifeCycleEvent();
    }

    /**
     * Retrieves the extension notification.
     * 
     * @return the extension notification, maybe {@code null}
     * @since 0.7.7
     */
    public ExtensionNotification getExtensionNotification() {
        return mmi.getExtensionNotification();
    }
}
