/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.implementation.jsapi20;

import java.io.IOException;
import java.util.Locale;

import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.synthesis.SynthesizerMode;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.mock.TestProperties;
import org.jvoicexml.mock.implementation.MockSynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Test cases for {@link Jsapi20SynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 *
* <p>
* Run this unit test with the VM option:
* <code>-Djava.library.path=3rdparty/jsr113jsebase/lib</code>.
* </p>
*/

public final class TestJsapi20SynthesizedOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TestJsapi20SynthesizedOutput.class);

    /** Timeout to wait for the listener. */
    private static final int TIMEOUT = 5000;

    /** The test object. */
    private Jsapi20SynthesizedOutput output;

    /** Listener for output events. */
    private MockSynthesizedOutputListener listener;

    /** The document server. */
    private DocumentServer documentServer;

    /** The session id. */
    private SessionIdentifier sessionId;

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     * @throws IOException
     *         error reading the test properties file
     */
    @BeforeClass
    public static void init() throws EngineException, IOException {
        final TestProperties properties = new TestProperties();
        final String factory = properties.get("JVOICEXML_IMPLEMENTATION_JSAPI20_TTS_ENGINE_LIST_FACTORY");
        LOGGER.info("registering engine factory: '" + factory + "'");
        EngineManager.registerEngineListFactory(factory);
        System.setProperty("java.library.path", "3rdparty/jsr113jsebase/lib");
        System.setProperty("javax.speech.supports.audio.management",
                Boolean.TRUE.toString());
        System.setProperty("javax.speech.supports.audio.capture",
                Boolean.TRUE.toString());
    }

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         set up failed
     * @throws JVoiceXMLEvent
     *         set up failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        final SynthesizerMode mode = SynthesizerMode.DEFAULT;
        output = new Jsapi20SynthesizedOutput(mode, null);
        output.open();
        output.activate();
        listener = new MockSynthesizedOutputListener();
        output.addListener(listener);
        documentServer = new JVoiceXmlDocumentServer();
        sessionId = new UuidSessionIdentifier();
    }

    /**
     * Test tear down.
     * @exception Exception
     *            tear down failed
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @After
    public void tearDown() throws Exception, JVoiceXMLEvent {
        output.passivate();
        output.close();
    }

    /**
     * Test method for {@link Jsapi20SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @throws Exception
     *         Test failed.
     */
    @Test
    public void testQueueSpeakableSsml() throws Exception, JVoiceXMLEvent {
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("This is a test for SSML");
        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        output.queueSpeakable(speakable, sessionId, documentServer);
        output.waitQueueEmpty();
        Assert.assertFalse(output.isBusy());
        final int size = 3;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(OutputStartedEvent.EVENT_TYPE, start.getEventType());
        OutputStartedEvent startedEvent = (OutputStartedEvent) start;
        Assert.assertEquals(speakable, startedEvent.getSpeakable());
        SynthesizedOutputEvent stop = listener.get(1);
        Assert.assertEquals(OutputEndedEvent.EVENT_TYPE, stop.getEventType());
        OutputEndedEvent stoppedEvent = (OutputEndedEvent) stop;
        Assert.assertEquals(speakable, stoppedEvent.getSpeakable());
        SynthesizedOutputEvent empty = listener.get(2);
        Assert.assertEquals(QueueEmptyEvent.EVENT_TYPE, empty.getEventType());
        Assert.assertTrue(empty instanceof QueueEmptyEvent);
    }

    /**
     * Test method for {@link Jsapi20SynthesizedOutput#waitQueueEmpty()}.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     * @since 0.7.5
     */
    @Test
    public void testWaitQueueEmpty() throws JVoiceXMLEvent, Exception {
        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("this is a test");
        final SpeakableText speakable1 = new SpeakableSsmlText(ssml);
        output.queueSpeakable(speakable1, sessionId, documentServer);
        output.waitQueueEmpty();
        Assert.assertFalse("output should be busy", output.isBusy());
    }

    /**
     * Test method for {@link Jsapi20SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         test failed.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testQueueMultipleSpeakables() throws JVoiceXMLEvent, Exception {
        final int max = 10;
        for (int i = 0; i < max; i++) {
            final SpeakableText speakable;
            final SsmlDocument ssml = new SsmlDocument();
            final Speak speak = ssml.getSpeak();
            speak.setXmlLang(Locale.US);
            speak.addText("this is test " + i);
            speakable = new SpeakableSsmlText(ssml);
            output.queueSpeakable(speakable, sessionId, documentServer);
        }

        output.waitQueueEmpty();
        Assert.assertFalse(output.isBusy());
        listener.waitSize(2 * max + 1, TIMEOUT);

        int started = 0;
        int ended = 0;
        int emptied = 0;
        for (int i = 0; i < listener.size(); i++) {
            final SynthesizedOutputEvent event = listener.get(i);
            if (event.isType(OutputStartedEvent.EVENT_TYPE)) {
                ++started;
            } else if (event.isType(OutputEndedEvent.EVENT_TYPE)) {
                ++ended;
            } else if (event.isType(QueueEmptyEvent.EVENT_TYPE)) {
                ++emptied;
            } else {
                Assert.fail("unknown event " + event.getEventType());
            }
        }
        Assert.assertEquals(max, started);
        Assert.assertEquals(max, ended);
    }
    

    /**
     * Test method for {@link Jsapi20SynthesizedOutput#cancelOutput()}.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     * @since 0.7.5
     */
    @Test
    public void testCancelSpeakable() throws JVoiceXMLEvent, Exception {
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("this is a test to interrupt the Text" +
                " to Speech Engine it is a very long sentence it really" +
                " is long very long longer than longcat");
        final SpeakableText speakable1 =
                new SpeakableSsmlText(ssml, true, BargeInType.SPEECH);

        output.queueSpeakable(speakable1, sessionId, documentServer);
        Thread.sleep(1500);
        output.cancelOutput(BargeInType.SPEECH);
        final int size = 2;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(OutputStartedEvent.EVENT_TYPE, start.getEventType());
        OutputStartedEvent startedEvent = (OutputStartedEvent) start;
        Assert.assertEquals(speakable1, startedEvent.getSpeakable());
        SynthesizedOutputEvent empty = listener.get(1);
        Assert.assertEquals(QueueEmptyEvent.EVENT_TYPE, empty.getEventType());
    }
    
    /**
     * Test method for {@link Jsapi20SynthesizedOutput#cancelOutput()}.
     * @throws JVoiceXMLEvent
     *         test failed
     * @throws Exception
     *         test failed
     * @since 0.7.5
     */
    @Test
    public void testCancelSpeakableWithNoBargein()
            throws JVoiceXMLEvent, Exception {
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("this is a test to interrupt the Text"
                + " to Speech Engine it is a very long sentence it really"
                + " is long very long longer than long");
        final SpeakableText speakable1 =
                new SpeakableSsmlText(ssml, true, BargeInType.SPEECH);
        output.queueSpeakable(speakable1, sessionId, documentServer);
        final SsmlDocument ssml2 = new SsmlDocument();
        final Speak speak2 = ssml2.getSpeak();
        speak2.addText("No bargein text");
        final SpeakableText speakable2 =
                new SpeakableSsmlText(ssml2, false, null);
        output.queueSpeakable(speakable2, sessionId, documentServer);
        Thread.sleep(1500);
        output.cancelOutput(BargeInType.SPEECH);
        final int size = 4;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start1 = listener.get(0);
        Assert.assertEquals(OutputStartedEvent.EVENT_TYPE, start1.getEventType());
        OutputStartedEvent started1Event = (OutputStartedEvent) start1;
        Assert.assertEquals(speakable1, started1Event.getSpeakable());
        SynthesizedOutputEvent start2 = listener.get(1);
        Assert.assertEquals(OutputStartedEvent.EVENT_TYPE, start2.getEventType());
        OutputStartedEvent started2Event = (OutputStartedEvent) start2;
        Assert.assertEquals(speakable2, started2Event.getSpeakable());
        SynthesizedOutputEvent end2 = listener.get(2);
        Assert.assertEquals(OutputEndedEvent.EVENT_TYPE, end2.getEventType());
        SynthesizedOutputEvent empty = listener.get(3);
        Assert.assertEquals(QueueEmptyEvent.EVENT_TYPE, empty.getEventType());
        output.waitQueueEmpty();
    }
}
