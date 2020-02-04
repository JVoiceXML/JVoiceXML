/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Application;
import org.jvoicexml.Configuration;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.mock.MockJvoiceXmlCore;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.w3c.dom.Document;

/**
 * Test cases for {@link VoiceXmlInterpreterContext}.
 * 
 * @author Dirk Schnelle-Walka
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
     * 
     * @exception Exception
     *                set up failed.
     */
    @Before
    public void setUp() throws Exception {
        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());
        server.start();

        final ImplementationPlatform platform = new MockImplementationPlatform();
        final JVoiceXmlCore jvxml = new MockJvoiceXmlCore();
        final Profile profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);

        final SessionIdentifier id = new UuidSessionIdentifier();
        final JVoiceXmlSession session = new JVoiceXmlSession(platform, jvxml,
                null, profile, id);
        final Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.loadObject(SpeechRecognizerProperties.class))
                .thenReturn(new SpeechRecognizerProperties());
        final DataModel model = Mockito.mock(DataModel.class);
        context = new VoiceXmlInterpreterContext(session, configuration, null, 
                model);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.VoiceXmlInterpreterContext#loadDocument(org.jvoicexml.DocumentDescriptor)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testLoadDocument() throws Exception, JVoiceXMLEvent {
        final Application application = Mockito.mock(Application.class);
        Mockito.when(application.resolve(Mockito.any(URI.class))).thenAnswer(
                new Answer<URI>() {
                    @Override
                    public URI answer(InvocationOnMock invocation)
                            throws Throwable {
                        return invocation.getArgumentAt(0, URI.class);
                    }
                    
        });
        context.setApplication(application);
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final URI uri = map.getUri("/test");
        map.addDocument(uri, document);
        Mockito.when(application.getLoadedDocuments()).then(
                new Answer<List<URI>>() {
                    @Override
                    public List<URI> answer(InvocationOnMock invocation)
                            throws Throwable {
                        final List<URI> uris = new java.util.ArrayList<URI>();
                        uris.add(uri);
                        return uris;
                    }
                });
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri,
                DocumentDescriptor.MIME_TYPE_XML);
        final Document retrievedDocument = context.loadDocument(descriptor);
        final DataModel model = context.getDataModel();
        Mockito.verify(model).updateArray(Mockito.eq("loadedDocumentURIs$"),
                Mockito.eq(0), Mockito.eq(uri.toString()),  
                Mockito.eq(Scope.APPLICATION));
        Assert.assertEquals(document.toString(), retrievedDocument.toString());
    }

    /**
     * Test method for
     * {@link VoiceXmlInterpreterContext#getSpeechRecognizerProperties(FormInterpretationAlgorithm)}.
     * 
     * @exception Exception
     *                test failed
     * @exception JVoiceXMLEvent
     *                test failed
     * @since 0.7.5
     */
    @Test
    public void testGetSpeechRecognizerProperties() throws Exception,
            JVoiceXMLEvent {
        final SpeechRecognizerProperties props = context
                .getSpeechRecognizerProperties(null);
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_CONFIDENCE_LEVEL),
                new Float(props.getConfidencelevel()));
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_SENSITIVITY), new Float(
                props.getSensitivity()));
        Assert.assertEquals(new Float(
                SpeechRecognizerProperties.DEFAULT_SPEED_VS_ACCURACY),
                new Float(props.getSpeedvsaccuracy()));
        Assert.assertEquals(0, props.getCompletetimeoutAsMsec());
        Assert.assertEquals(0, props.getIncompletetimeoutAsMsec());
        Assert.assertEquals(0, props.getMaxspeechtimeoutAsMsec());
    }

    /**
     * Test method for
     * {@link VoiceXmlInterpreterContext#getSpeechRecognizerProperties(FormInterpretationAlgorithm)}.
     * 
     * @exception Exception
     *                test failed
     * @exception JVoiceXMLEvent
     *                test failed
     * @since 0.7.5
     */
    @Test
    public void testGetSpeechRecognizerPropertiesSetProps() throws Exception,
            JVoiceXMLEvent {
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_CONFIDENCE_LEVEL, "0.2");
        context.setProperty(SpeechRecognizerProperties.PROPERTY_SENSITIVITY,
                "0.3");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_SPEED_VS_ACCURACY, "0.4");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_COMPLETE_TIMEOUT, "800ms");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_INCOMPLETE_TIMEOUT, "45s");
        context.setProperty(
                SpeechRecognizerProperties.PROPERTY_MAX_SPEECH_TIMEOUT, "10ms");
        final SpeechRecognizerProperties props = context
                .getSpeechRecognizerProperties(null);
        Assert.assertEquals(new Float(0.2f),
                new Float(props.getConfidencelevel()));
        Assert.assertEquals(new Float(0.3f), new Float(props.getSensitivity()));
        Assert.assertEquals(new Float(0.4f),
                new Float(props.getSpeedvsaccuracy()));
        Assert.assertEquals(800, props.getCompletetimeoutAsMsec());
        Assert.assertEquals(45000, props.getIncompletetimeoutAsMsec());
        Assert.assertEquals(10, props.getMaxspeechtimeoutAsMsec());
    }
}
