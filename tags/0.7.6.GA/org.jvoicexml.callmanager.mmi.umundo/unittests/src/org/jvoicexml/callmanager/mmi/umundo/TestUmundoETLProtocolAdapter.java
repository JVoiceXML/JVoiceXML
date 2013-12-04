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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.callmanager.mmi.umundo.mock.MockReceiver;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.umundo.core.Node;
import org.umundo.s11n.TypedSubscriber;

import com.google.protobuf.ExtensionRegistry;

/**
 * Test cases for the {@link MmiReceiver}.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 *
 */
public final class TestUmundoETLProtocolAdapter {
    /** The umundo receiving node. */
    private Node receivingNode;
    /** A receiver for MMI events. */
    private MockReceiver receiver;

    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        receivingNode = new Node();
        receiver = new MockReceiver();
        TypedSubscriber subscriber =
                new TypedSubscriber("mmi:jvoicexml", receiver);
        receivingNode.addSubscriber(subscriber);
        subscriber.registerType(LifeCycleEvents.LifeCycleEvent.class);

        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        LifeCycleEvents.registerAllExtensions(registry);
        subscriber.setExtensionRegistry(registry);
    }


    /**
     * Tear down the test environment.
     * @throws Exception tear down failed
     */
    @After
    public void tearDown() throws Exception {
        if (receivingNode != null) {
            receivingNode.suspend();
        }
    }

    /**
     * Test case for {@link UmundoETLProtocolAdapter#sendMMIEvent(Object, LifeCycleEvent)}.
     * @throws Exception
     *         test failed
     */
    @Test(timeout = 5000)
    public void testSendMMIEvent() throws Exception {
        final PrepareResponse response = new PrepareResponse();
        final String requestId = "requestId1";
        final String source = "source1";
        final String target = "target1";
        final String context = "context1";
        response.setRequestId(requestId);
        response.setSource(source);
        response.setTarget(target);
        response.setContext(context);
        final UmundoETLProtocolAdapter adapter = new UmundoETLProtocolAdapter();
        adapter.start();
        Thread.sleep(1000);
        adapter.sendMMIEvent("dummy", response);
        receiver.waitReceivedObject();
        final Object object = receiver.getReceivedObject();
        adapter.stop();
    }
}
