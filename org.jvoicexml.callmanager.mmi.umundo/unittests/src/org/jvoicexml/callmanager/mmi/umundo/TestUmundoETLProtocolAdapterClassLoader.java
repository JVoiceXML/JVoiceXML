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

import java.io.File;

import org.junit.Test;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents.LifeCycleEvent.LifeCycleEventType;
import org.umundo.core.Node;
import org.umundo.s11n.TypedPublisher;

/**
 * Test cases for the {@link MmiReceiver}.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 *
 */
public final class TestUmundoETLProtocolAdapterClassLoader {
    /**
     * Test case for {@link UmundoETLProtocolAdapter#sendMMIEvent(Object, LifeCycleEvent)}.
     * @throws Exception
     *         test failed
     */
    @Test(timeout = 5000)
    public void testSendMMIEvent() throws Exception {
        final String requestId = "1";
        final String source = "test";
        final String target = "umundo://mmi/jvoicexml";
        final String context = "textctx";
        final File file = new File("unittests/vxml/hello.vxml");
        final LifeCycleEvents.PrepareRequest prepareRequest =
                LifeCycleEvents.PrepareRequest.newBuilder()
                .setContentURL(file.getCanonicalPath())
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
        final Node node = new Node();
        final TypedPublisher publisher = new TypedPublisher("mmi:jvoicexml");
        node.addPublisher(publisher);
        publisher.waitForSubscribers(1);
        publisher.sendObject("LifeCycleEvent", event1);
        Thread.sleep(1000);
    }
}
