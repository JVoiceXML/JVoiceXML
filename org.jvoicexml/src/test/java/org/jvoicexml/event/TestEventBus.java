/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.BadFetchHttpResponsecodeError;
import org.mockito.Mockito;

/**
 * Test cases for {@link EventBus}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public final class TestEventBus {

    /**
     * Test method for {@link org.jvoicexml.event.EventBus#subscribe(java.lang.String, org.jvoicexml.event.EventSubscriber)}.
     */
    @Test
    public void testSubscribe() {
        final EventSubscriber subscriber = Mockito.mock(EventSubscriber.class);
        final EventBus bus = new EventBus();
        final String type = "test.dummy.event";
        bus.subscribe(type, subscriber);
        Assert.assertTrue(bus.unsubscribe(type, subscriber));
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventBus#publish(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    @Test
    public void testPublish() {
        final EventSubscriber subscriber = Mockito.mock(EventSubscriber.class);
        final EventBus bus = new EventBus();
        final String type = BadFetchError.EVENT_TYPE;
        bus.subscribe(type, subscriber);
        final JVoiceXMLEvent event = new BadFetchError("test message");
        bus.publish(event);
        Mockito.verify(subscriber).onEvent(event);
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventBus#publish(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    @Test
    public void testPublishSubType() {
        final EventSubscriber subscriber = Mockito.mock(EventSubscriber.class);
        final EventBus bus = new EventBus();
        final String type = BadFetchError.EVENT_TYPE;
        bus.subscribe(type, subscriber);
        final JVoiceXMLEvent event =
                new BadFetchHttpResponsecodeError(440, "test message");
        bus.publish(event);
        Mockito.verify(subscriber).onEvent(event);
    }

    /**
     * Test method for {@link org.jvoicexml.event.EventBus#publish(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    @Test
    public void testPublishAll() {
        final EventSubscriber subscriber = Mockito.mock(EventSubscriber.class);
        final EventBus bus = new EventBus();
        bus.subscribe("", subscriber);
        final JVoiceXMLEvent event =
                new BadFetchHttpResponsecodeError(440, "test message");
        bus.publish(event);
        Mockito.verify(subscriber).onEvent(event);
    }
}
