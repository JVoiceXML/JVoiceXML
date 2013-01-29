/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.umundo;

import java.util.Collection;

import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

/**
 * Receiver for MMI events over umundo.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 */
public final class MmiReceiver implements ITypedReceiver {
    /** Registered listeners for MMI events. */
    private final Collection<MMIEventListener> listeners;

    /** The used protocol adapter. */
    private final String sourceUrl;

    /**
     * Constructs a new object.
     * @param source the source URL of this endpoint
     */
    public MmiReceiver(final String source) {
        listeners = new java.util.ArrayList<MMIEventListener>();
        sourceUrl = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveObject(Object object, Message msg) {
        LifeCycleEvents.LifeCycleEvent receivedEvent =
                (LifeCycleEvents.LifeCycleEvent) object;
        System.out.println(object);
        final LifeCycleEvent event = convertToLifeCycleEvent(receivedEvent);
        System.out.println(event);
        if (event == null) {
            return;
        }
        final DecoratedMMIEvent docatedEvent =
                new DecoratedMMIEvent(sourceUrl, event);
        synchronized (listeners) {
            for (MMIEventListener listener : listeners) {
                listener.receivedEvent(docatedEvent);
            }
        }
    }

    private LifeCycleEvent convertToLifeCycleEvent(
            final LifeCycleEvents.LifeCycleEvent receivedEvent) {
        final LifeCycleEvent event;
        switch (receivedEvent.getType()) {
        case PREPARE_REQUEST:
            event = new PrepareRequest();
            break;
        default:
            event = null;
            break;
        }
        final String target = receivedEvent.getTarget();
        if (!target.equals(sourceUrl)) {
            return null;
        }
        event.setTarget(receivedEvent.getTarget());
        event.setRequestId(receivedEvent.getRequestID());
        event.setSource(receivedEvent.getSource());
        return event;
    }
 
    public void addMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}

