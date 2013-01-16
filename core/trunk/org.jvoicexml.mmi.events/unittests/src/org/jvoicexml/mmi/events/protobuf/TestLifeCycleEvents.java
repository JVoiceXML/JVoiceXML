/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.mmi.events.protobuf;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents.LifeCycleEvent.LifeCycleEventType;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Test methods for {@link LifeCycleEvents}. 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public class TestLifeCycleEvents {
    /** The registry for protobuf extensions. */
    private ExtensionRegistry registry;

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() {
        registry = ExtensionRegistry.newInstance();
        LifeCycleEvents.registerAllExtensions(registry);
    }

    /**
     * Test method for {@link LifeCycleEvents.NewContextRequest}.
     * @throws InvalidProtocolBufferException
     *         test failed
     */
    @Test
    public void testNewContextRequest() throws InvalidProtocolBufferException {
        final LifeCycleEvents.NewContextRequest builder =
                LifeCycleEvents.NewContextRequest.newBuilder().build();
        final String requestId = "requestId1";
        final String source = "source1";
        final String target = "target1";
        final LifeCycleEvents.LifeCycleEvent request1 = 
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .setType(LifeCycleEventType.NEW_CONTEXT_REQUEST)
                .setRequestID(requestId)
                .setSource(source)
                .setTarget(target)
                .setExtension(
                        LifeCycleEvents.NewContextRequest.request, builder)
                .build();
        final byte[] buffer = request1.toByteArray();
        LifeCycleEvents.LifeCycleEvent event =
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .mergeFrom(buffer, registry).build();
        Assert.assertEquals(LifeCycleEventType.NEW_CONTEXT_REQUEST,
                event.getType());
        Assert.assertEquals(requestId, event.getRequestID());
        Assert.assertEquals(source, event.getSource());
        Assert.assertEquals(target, event.getTarget());
    }

    /**
     * Test method for {@link LifeCycleEvents.PrepareRequest}.
     * @throws InvalidProtocolBufferException
     *         test failed
     */
    @Test
    public void testPrepareRequest() throws InvalidProtocolBufferException {
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
                .setExtension(LifeCycleEvents.PrepareRequest.request, prepareRequest)
                .build();
        final LifeCycleEvents.LifeCycleEvent event1 = 
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .setType(LifeCycleEventType.PREPARE_REQUEST)
                .setRequestID(requestId)
                .setSource(source)
                .setTarget(target)
                .setExtension(LifeCycleEvents.LifeCycleRequest.request, lifeCycleRequest)
                .build();
        final byte[] buffer = event1.toByteArray();

        final LifeCycleEvents.LifeCycleEvent decodedEvent =
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .mergeFrom(buffer, registry).build();
        Assert.assertEquals(LifeCycleEventType.PREPARE_REQUEST,
                decodedEvent.getType());
        Assert.assertEquals(requestId, decodedEvent.getRequestID());
        Assert.assertEquals(source, decodedEvent.getSource());
        Assert.assertEquals(target, decodedEvent.getTarget());
        final LifeCycleEvents.LifeCycleRequest decodedLifeCycleRequest =
                decodedEvent.getExtension(LifeCycleEvents.LifeCycleRequest.request);
        Assert.assertEquals(context, decodedLifeCycleRequest.getContext());
        final LifeCycleEvents.PrepareRequest decodedPrepareRequest =
                decodedLifeCycleRequest.getExtension(LifeCycleEvents.PrepareRequest.request);
        Assert.assertEquals(content, decodedPrepareRequest.getContent());
        Assert.assertEquals(contentUrl, decodedPrepareRequest.getContentURL());
    }
}
