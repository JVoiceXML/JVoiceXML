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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents.LifeCycleEvent.LifeCycleEventType;
import org.umundo.core.Node;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import com.google.protobuf.ExtensionRegistry;

/**
 * Test cases for the {@link MmiReceiver}.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 *
 */
public final class TestMmiReceiver implements MMIEventListener {
    /** The umundo receiving node. */
    private Node receivingNode;
    /** The umundo sending node. */
    private Node publishingNode;
    /** Synchronization lock. */
    private Object lock;
    /** The received event. */
    private LifeCycleEvent receivedEvent;

    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        receivingNode = new Node();
        publishingNode = new Node();
        final MmiReceiver receiver = new MmiReceiver("target1");
        receiver.addMMIEventListener(this);
        TypedSubscriber subscriber = new TypedSubscriber("test", receiver);
        receivingNode.addSubscriber(subscriber);
        subscriber.registerType(LifeCycleEvents.LifeCycleEvent.class);

        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        LifeCycleEvents.registerAllExtensions(registry);
        subscriber.setExtensionRegistry(registry);
        lock = new Object();
        receivedEvent = null;
    }

    /**
     * Tear down the test environment.
     * @throws Exception tear down failed
     */
    @After
    public void tearDown() throws Exception {
        receivingNode.suspend();
        publishingNode.suspend();
    }

    /**
     * Tests for {@link MmiReceiver#receiveObject(Object, org.umundo.core.Message)}.
     * @throws Exception
     *         test failed
     */
    @Test//(timeout = 5000)
    public void testReceiveObject() throws Exception {
        final TypedPublisher publisher = new TypedPublisher("test");
        publishingNode.addPublisher(publisher);
        publisher.waitForSubscribers(1);
        final String requestId = "requestId1";
        final String source = "source1";
        final String target = "target1";
        final String context = "context1";
        final String content = "content1";
        final String contentUrl = "contentUrl1";
        final LifeCycleEvents.PrepareRequest prepareRequest =
                LifeCycleEvents.PrepareRequest.newBuilder()
                .setContent(content)
                .setContentURL(contentUrl)
                .build();
        final LifeCycleEvents.LifeCycleRequest lifeCycleRequest =
                LifeCycleEvents.LifeCycleRequest.newBuilder()
                .setContext(context)
                .setExtension(LifeCycleEvents.PrepareRequest.request,
                        prepareRequest)
                .build();
        final LifeCycleEvents.LifeCycleEvent event1 =
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .setType(LifeCycleEventType.PREPARE_REQUEST)
                .setRequestID(requestId)
                .setSource(source)
                .setTarget(target)
                .setExtension(LifeCycleEvents.LifeCycleRequest.request,
                        lifeCycleRequest)
                .build();
        publisher.sendObject("LifeCycleEvent", event1);
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertNotNull(receivedEvent);
        Assert.assertTrue(receivedEvent instanceof PrepareRequest);
    }

    @Override
    public void receivedEvent(final DecoratedMMIEvent event) {
        receivedEvent = event.getEvent();
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
