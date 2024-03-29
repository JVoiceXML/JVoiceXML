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
package org.jvoicexml.callmanager.mmi;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.callmanager.mmi.mock.MockETLProtocolAdapter;
import org.jvoicexml.client.ConnectionInformationFactory;
import org.jvoicexml.client.JVoiceXmlConnectionInformationFactory;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mock.MockJvoiceXmlCore;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link VoiceModalityComponent}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class TestVoiceModalityComponent {
    /** The call manager. */
    private MMICallManager cm;

    /**
     * Set up the test environment.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        final JVoiceXmlCore jvxml = new MockJvoiceXmlCore();
        cm = new MMICallManager();
        cm.setJVoiceXml(jvxml);
        final ETLProtocolAdapter adapter = new MockETLProtocolAdapter();
        cm.setProtocolAdapter(adapter);
        final ConnectionInformationFactory factory =
                new JVoiceXmlConnectionInformationFactory();
        cm.setConnectionInformationFactory(factory);
        cm.setCall("desktop");
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
     * Test method for {@link org.jvoicexml.callmanager.mmi.VoiceModalityComponent#receivedEvent(org.jvoicexml.mmi.events.xml.MMIEvent)}.
     * @throws Exception test failed
     */
    @Test
    public void testReceivedEvent() throws Exception {
        final VoiceModalityComponent mc = cm.getVoiceModalityComponent();
        final Mmi mmi = new Mmi();
        final StartRequest request = new StartRequest();
        mmi.setStartRequest(request);
        request.setContext("http://nowhere");
        request.setRequestId("4242");
        final File file = new File("unittests/vxml/hello.vxml");
        final URL url = file.toURI().toURL();
        request.setContentURL(url);
        final DecoratedMMIEvent event = new DecoratedMMIEvent(this, mmi);
        final CallMetadata data = new CallMetadata();
        mc.receivedEvent(event, data);
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.mmi.VoiceModalityComponent#receivedEvent(org.jvoicexml.mmi.events.xml.MMIEvent)}.
     * @throws Exception test failed
     */
    @Test
    public void testReceivedEventStartRequestContent() throws Exception {
        final VoiceModalityComponent mc = cm.getVoiceModalityComponent();
        final Mmi mmi = new Mmi();
        final StartRequest request = new StartRequest();
        mmi.setStartRequest(request);
        request.setContext("http://nowhere");
        request.setRequestId("4242");
        final AnyComplexType any = new AnyComplexType();
        any.addContent("this is a test");
        request.setContent(any);
        final DecoratedMMIEvent event = new DecoratedMMIEvent(this, mmi);
        final CallMetadata data = new CallMetadata();
        mc.receivedEvent(event, data);
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.mmi.VoiceModalityComponent#receivedEvent(org.jvoicexml.mmi.events.xml.MMIEvent)}.
     * @throws Exception test failed
     */
    @Test
    public void testReceivedEventStartRequestContentPrompt() throws Exception {
        final VoiceModalityComponent mc = cm.getVoiceModalityComponent();
        final Mmi mmi = new Mmi();
        final StartRequest request = new StartRequest();
        mmi.setStartRequest(request);
        request.setContext("http://nowhere");
        request.setRequestId("4242");
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.setXmlLang(Locale.GERMAN);
        prompt.addText("Das ist ein Test");
        final AnyComplexType any = new AnyComplexType();
        any.addContent(prompt.getNode());
        request.setContent(any);
        final DecoratedMMIEvent event = new DecoratedMMIEvent(this, mmi);
        final CallMetadata data = new CallMetadata();
        mc.receivedEvent(event, data);
    }
    
}
