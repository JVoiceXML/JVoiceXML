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

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.mock.event.MockEventSubscriber;

/**
 * Test cases for {@link EventSubscription}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class TestEventSubscription {
    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#getType()}.
     */
    @Test
    public void testGetType() {
        final String type = "test.dummy.event";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertEquals(type, subscription.getType());
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#matches(java.lang.String)}.
     */
    @Test
    public void testMatches() {
        final String type = "test.dummy.event";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertTrue(subscription.matches(type));
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#matches(java.lang.String)}.
     */
    @Test
    public void testMatchesWrongType() {
        final String type = "test.dummy.event";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertFalse(subscription.matches("wrong.event.type"));
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#matches(java.lang.String)}.
     */
    @Test
    public void testMatchesPrefix() {
        final String type = "test.dummy";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertTrue(subscription.matches("test.dummy.event"));
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#matches(java.lang.String)}.
     */
    @Test
    public void testMatchesAll() {
        final String type = "";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertTrue(subscription.matches("test.dummy.event"));
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#getSubscriber()}.
     */
    @Test
    public void testGetSubscriber() {
        final String type = "test.dummy.event";
        final EventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        Assert.assertEquals(subscriber, subscription.getSubscriber());
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventSubscription#publish(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    @Test
    public void testPublish() {
        final String type = "test.dummy.event";
        final MockEventSubscriber subscriber = new MockEventSubscriber();
        final EventSubscription subscription =
                new EventSubscription(type, subscriber);
        final JVoiceXMLEvent event = new ConnectionDisconnectHangupEvent();
        subscription.publish(event);
        Assert.assertEquals(event, subscriber.getEvent());
        Assert.assertEquals(subscriber, subscription.getSubscriber());
    }

}
