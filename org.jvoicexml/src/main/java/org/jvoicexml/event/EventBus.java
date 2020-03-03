/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

/**
 * An event bus to transport events between interpreter and implementation
 * platform. Events are published via a simple publish-subscribe mechanism.
 * {@link EventSubscriber}s can indicate their interest in a certain event
 * type by registering via {@link #subscribe(String, EventSubscriber)}. Events
 * can be published to all {@link EventSubscriber}s via
 * {@link #publish(JVoiceXMLEvent)}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class EventBus {
    /** The registered event subscriptions. */
    private final Collection<EventSubscription> subscriptions;
    
    /**
     * Constructs a new object.
     */
    public EventBus() {
        subscriptions = new java.util.ArrayList<EventSubscription>();
    }

    /**
     * Subscribes the given subscriber for the given event type or subtypes. The
     * latter are separated by a '.'. For instance, a subscription to events of
     * the type {@link org.jvoicexml.event.error.BadFetchError} would include
     * a subscription to events of the type
     * {@link org.jvoicexml.event.error.BadFetchHttpResponsecodeError}.
     * @param type the event type
     * @param subscriber the subscriber
     */
    public void subscribe(final String type, final EventSubscriber subscriber) {
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        synchronized (subscriptions) {
            subscriptions.add(subscription);
        }
    }

    /**
     * Unsubscribes the given subscriber from the given event type.
     * @param type the event type
     * @param subscriber the subscriber
     * @return <code>true</code> if the subscription was removed
     */
    public boolean unsubscribe(final String type,
            final EventSubscriber subscriber) {
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        synchronized (subscriptions) {
            for (EventSubscription current : subscriptions) {
                if (subscription.equals(current)) {
                    subscriptions.remove(subscription);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Publishes the given event to all registered subscribers.
     * @param event the event to publish
     */
    public void publish(final JVoiceXMLEvent event) {
        final String type = event.getEventType();
        synchronized (subscriptions) {
            for (EventSubscription subscription : subscriptions) {
                if (subscription.matches(type)) {
                    subscription.publish(event);
                }
            }
        }
    }
}
