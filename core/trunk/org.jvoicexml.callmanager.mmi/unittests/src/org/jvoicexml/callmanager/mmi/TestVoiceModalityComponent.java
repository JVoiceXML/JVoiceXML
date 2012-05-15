/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.callmanager.mmi;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.callmanager.mmi.test.DummyETLProtocolAdapter;
import org.jvoicexml.client.ConnectionInformationFactory;
import org.jvoicexml.client.JVoiceXmlConnectionInformationFactory;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;
import org.jvoicexml.test.DummyJvoiceXmlCore;

/**
 * Test cases for {@link VoiceModalityComponent}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class TestVoiceModalityComponent {
    /** The call manager. */
    private MMICallManager cm;

    /**
     * Set up the test environment
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        final JVoiceXml jvxml = new DummyJvoiceXmlCore();
        cm = new MMICallManager();
        cm.setJVoiceXml(jvxml);
        final ETLProtocolAdapter adapter = new DummyETLProtocolAdapter();
        cm.setProtocolAdapter(adapter);
        final ConnectionInformationFactory factory =
                new JVoiceXmlConnectionInformationFactory();
        cm.setConnectionInformationFactory(factory);
        cm.setCall("dummy");
        cm.setInput("jsapi20");
        cm.setOutput("jsapi20");
        cm.start();
    }

    /**
     * Tear down the test environment.
     */
    public void tearDown() {
        cm.stop();
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.mmi.VoiceModalityComponent#receivedEvent(org.jvoicexml.mmi.events.MMIEvent)}.
     */
    @Test
    public void testReceivedEvent() {
        final VoiceModalityComponent mc = cm.getVoiceModalityComponent();
        final StartRequestBuilder builder = new StartRequestBuilder();
        builder.setContextId("http://nowhere");
        builder.setRequestId("4242");
        final File file = new File("unittests/vxml/hello.vxml");
        final URI uri = file.toURI();
        builder.setHref(uri);
        final StartRequest request = builder.toStartRequest();
        mc.receivedEvent(request);
    }

}
