/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A subscription for a specific event type.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
final class EventSubscription {
    /** The type of subscription. */
    private final String type;
    /** The associated event subscriber. */
    private final EventSubscriber subscriber;
    
    /**
     * Constructs a new object.
     * @param eventType the type of subscription
     * @param eventSubscriber the associated subscriber
     */
    public EventSubscription(final String eventType,
            final EventSubscriber eventSubscriber) {
        type = eventType;
        subscriber = eventSubscriber;
    }

    /**
     * Retrieves the type of the subscription.
     * @return the type of the subscription
     */
    public String getType() {
        return type;
    }

    /**
     * Checks if the subscription matches the given event type.
     * @param eventType the event type to check
     * @return <code>true</code> if the subscription matches the event type
     * @since 0.7.7
     */
    public boolean matches(final String eventType) {
        return eventType.startsWith(type);
    }
    /**
     * Retrieves the subscriber.
     * @return the subscriber
     */
    public EventSubscriber getSubscriber() {
        return subscriber;
    }

    /**
     * Publishes the given event to the associated subscriber.
     * @param event the event to publish
     */
    public void publish(final JVoiceXMLEvent event) {
        subscriber.onEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((subscriber == null) ? 0 : subscriber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EventSubscription other = (EventSubscription) obj;
        if (subscriber == null) {
            if (other.subscriber != null) {
                return false;
            }
        } else if (!subscriber.equals(other.subscriber)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
