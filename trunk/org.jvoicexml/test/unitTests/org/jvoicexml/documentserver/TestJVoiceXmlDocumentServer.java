/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.documentserver;

import java.io.File;
import java.io.StringReader;
import java.net.URI;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * Test case for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestJVoiceXmlDocumentServer {
    /** Mapped document repository. */
    private DocumentMap map;

    /** The server object to test. */
    private JVoiceXmlDocumentServer server;

    /**
     * Test setup.
     */
    @Before
    public void setUp() {
        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#getObject(java.net.URI, java.lang.String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetObject() throws JVoiceXMLEvent {
        String test = "Pinocchio";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);

        Object object = server.getObject(null, uri, "text/plain");
        Assert.assertEquals(test, object);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#storeAudio(AudioInputStream)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testStoreAudio() throws Exception, JVoiceXMLEvent {
        final File file = new File("test/config/test.wav");
        final AudioInputStream ain = AudioSystem.getAudioInputStream(file);
        final URI result = server.storeAudio(ain);
        Assert.assertNotNull(result);
        final File rec = new File(result);
        Assert.assertTrue("expexcted file exists", rec.exists());
    }

    /**
     * Test case for {@link JVoiceXmlDocumentServer#getDocument(Session, DocumentDescriptor)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test
    public void testGetDocument() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session = new JVoiceXmlSession(platform, jvxml);
        final VoiceXmlDocument retrievedDocument =
            server.getDocument(session, descriptor);
        Assert.assertEquals(document, retrievedDocument);
    }

    /**
     * Test case for {@link JVoiceXmlDocumentServer#getDocument(Session, DocumentDescriptor)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test
    public void testGetInvalidDocument() throws Exception, JVoiceXMLEvent {
        final String str = "<vxml><form><block><prompt>test</prompt>"
            + "</block></form></vxml>";
        final StringReader reader = new StringReader(str);
        final InputSource input = new InputSource(reader);
        final VoiceXmlDocument document = new VoiceXmlDocument(input);
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session = new JVoiceXmlSession(platform, jvxml);
        final VoiceXmlDocument retrievedDocument =
            server.getDocument(session, descriptor);
        Assert.assertEquals(document, retrievedDocument);
    }
}
