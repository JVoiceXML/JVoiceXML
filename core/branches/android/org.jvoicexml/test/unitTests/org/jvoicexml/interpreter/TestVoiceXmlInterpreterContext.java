/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/TestVoiceXmlInterpreterContext.java $
 * Version: $LastChangedRevision: 2655 $
 * Date:    $Date: 2011-05-18 03:13:16 -0500 (mié, 18 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.w3c.dom.Document;

/**
 * Test cases for {@link VoiceXmlInterpreterContext}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2655 $
 * @since 0.7
 */
public final class TestVoiceXmlInterpreterContext {

    /** Mapped document repository. */
    private DocumentMap map;

    /** The server object to test. */
    private JVoiceXmlDocumentServer server;

    /** The test object. */
    private VoiceXmlInterpreterContext context;

    /**
     * Test setup.
     * @exception Exception
     *            set up failed.
     */
    @Before
    public void setUp() throws Exception {
        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());

        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.VoiceXmlInterpreterContext#loadDocument(org.jvoicexml.DocumentDescriptor)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testLoadDocument() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final Document retrievedDocument = context.loadDocument(descriptor);
        Assert.assertEquals(document.toString(), retrievedDocument.toString());
    }

    /**
     * Test method for {@link VoiceXmlInterpreterContext#getSpeechRecognizerProperties()}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.5
     */
    @Test
    public void testGetSpeechRecognizerProperties()
        throws Exception, JVoiceXMLEvent {
        final SpeechRecognizerProperties props =
            context.getSpeechRecognizerProperties(null);
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_CONFIDENCE_LEVEL),
                new Float(props.getConfidencelevel()));
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_SENSITIVITY),
                new Float(props.getSensitivity()));
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_SPEED_VS_ACCURACY),
                new Float(props.getSpeedvsaccuracy()));
        Assert.assertEquals(0, props.getCompletetimeoutAsMsec());
        Assert.assertEquals(0, props.getIncompletetimeoutAsMsec());
        Assert.assertEquals(0, props.getMaxspeechtimeoutAsMsec());
    }

    /**
     * Test method for {@link VoiceXmlInterpreterContext#getSpeechRecognizerProperties()}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.5
     */
    @Test
    public void testGetSpeechRecognizerPropertiesSetProps()
        throws Exception, JVoiceXMLEvent {
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_CONFIDENCE_LEVEL, "0.2");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_SENSITIVITY, "0.3");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_SPEED_VS_ACCURACY, "0.4");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_COMPLETE_TIMEOUT, "800ms");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_INCOMPLETE_TIMEOUT, "45s");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_MAX_SPEECH_TIMEOUT, "10ms");
        final SpeechRecognizerProperties props =
            context.getSpeechRecognizerProperties(null);
        Assert.assertEquals(new Float(0.2f),
                new Float(props.getConfidencelevel()));
        Assert.assertEquals(new Float(0.3f),
                new Float(props.getSensitivity()));
        Assert.assertEquals(new Float(0.4f),
                new Float(props.getSpeedvsaccuracy()));
        Assert.assertEquals(800, props.getCompletetimeoutAsMsec());
        Assert.assertEquals(45000, props.getIncompletetimeoutAsMsec());
        Assert.assertEquals(10, props.getMaxspeechtimeoutAsMsec());
    }
}
