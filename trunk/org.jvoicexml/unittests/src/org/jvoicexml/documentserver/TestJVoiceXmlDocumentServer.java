/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.FileSchemeStrategy;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
        server.addSchemeStrategy(new FileSchemeStrategy());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#getObject(java.net.URI, java.lang.String)}.
     * @exception JVoiceXMLEvent
     *            test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetObjectTextPlain() throws JVoiceXMLEvent, Exception {
        String test = "Pinocchio";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);

        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        Object object = server.getObject(null, descriptor,
                DocumentServer.TEXT_PLAIN);
        Assert.assertEquals(test, object);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#getObject(java.net.URI, java.lang.String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @throws Exception
     *            Test failed.
     * @since 0.7
     */
    @Test
    public void testGetObjectTextXml() throws JVoiceXMLEvent, Exception {
        VoiceXmlDocument document = new VoiceXmlDocument();
        Vxml vxml = document.getVxml();
        vxml.appendChild(Form.class);
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);

        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        Object object = server.getObject(null, descriptor,
                DocumentServer.TEXT_XML);
        Assert.assertTrue("object should be a document",
                object instanceof Document);
        final Document other = (Document) object;
        final Node otherVxml = other.getFirstChild();
        Assert.assertEquals(Vxml.TAG_NAME, otherVxml.getNodeName());
        final Node otherForm = otherVxml.getFirstChild();
        Assert.assertEquals(Form.TAG_NAME, otherForm.getNodeName());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#getObject(java.net.URI, java.lang.String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @throws Exception
     *            Test failed.
     * @since 0.7.5
     */
    @Test
    public void testGetObjectBinary() throws JVoiceXMLEvent, Exception {
        final File file = new File("unittests/config/test.wav");
        final URI uri = file.toURI();
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final Object object = server.getObject(null, descriptor, null);
        Assert.assertTrue(object instanceof ReadBuffer);
        final ReadBuffer buffer = (ReadBuffer) object;
        Assert.assertFalse(buffer.isAscii());
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
        final File file = new File("unittests/config/test.wav");
        final AudioInputStream ain = AudioSystem.getAudioInputStream(file);
        final URI result = server.storeAudio(ain);
        Assert.assertNotNull(result);
        final File rec = new File(result);
        Assert.assertTrue("expexcted file exists", rec.exists());
    }

    /**
     * Test method for {@link JVoiceXmlDocumentServer#getAudioInputStream(Session, URI)}.
     * 
     * @since 0.7.2
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetAudioInputStream() throws Exception, JVoiceXMLEvent {
        final File file = new File("unittests/config/test.wav");
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final Session session = new JVoiceXmlSession(platform, jvxml, null);
        final String sessionId = session.getSessionID();
        final AudioInputStream in =
            server.getAudioInputStream(sessionId, file.toURI());
        Assert.assertNotNull(in);
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
        final Session session = new JVoiceXmlSession(platform, jvxml, null);
        final String sessionId = session.getSessionID();
        final VoiceXmlDocument retrievedDocument =
            server.getDocument(sessionId, descriptor);
        Assert.assertEquals(document.toString(), retrievedDocument.toString());
    }

    /**
     * Test case for {@link JVoiceXmlDocumentServer#getDocument(Session, DocumentDescriptor)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test
    public void testGetDocumentFragment() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);
        final URI fragmentUri = new URI(uri.toString() + "#fragment");
        final DocumentDescriptor descriptor =
            new DocumentDescriptor(fragmentUri);
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session = new JVoiceXmlSession(platform, jvxml, null);
        final String sessionId = session.getSessionID();
        final VoiceXmlDocument retrievedDocument =
            server.getDocument(sessionId, descriptor);
        Assert.assertEquals(document.toString(), retrievedDocument.toString());
    }

    /**
     * Test case for {@link JVoiceXmlDocumentServer#getDocument(Session, DocumentDescriptor)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test(expected = BadFetchError.class)
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
        final Session session = new JVoiceXmlSession(platform, jvxml, null);
        final String sessionId = session.getSessionID();
        final VoiceXmlDocument retrievedDocument =
            server.getDocument(sessionId, descriptor);
        Assert.assertEquals(document, retrievedDocument);
    }
}
