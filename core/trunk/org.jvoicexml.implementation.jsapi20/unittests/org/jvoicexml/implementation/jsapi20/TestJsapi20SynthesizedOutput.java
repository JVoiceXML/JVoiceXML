/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-04-19 20:20:06 +0200 (Mo, 19 Apr 2010) $, Dirk Schnelle-Walka, project lead
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

import java.util.Locale;

import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.synthesis.SynthesizerMode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.jsapi2.sapi.SapiEngineListFactory;
import org.jvoicexml.test.implementation.DummySynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link Jsapi20SynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.4
 *
* <p>
* Run this unit test with the VM option:
* <code>-Djava.library.path=3rdparty/jsr113jsebase/lib</code>.
* </p>
*/

public final class TestJsapi20SynthesizedOutput {
    /** Timeout to wait for the listener. */
    private static final int TIMEOUT = 1000;

    /** The test object. */
    private Jsapi20SynthesizedOutput output;

    /** Listener for output events. */
    private DummySynthesizedOutputListener listener;

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        EngineManager.registerEngineListFactory(
                SapiEngineListFactory.class.getCanonicalName());
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
        listener = new DummySynthesizedOutputListener();
        output.addListener(listener);
    }

    /**
     * Test tear down.
     * @exception Exception
     *            tear down failed
     */
    @After
    public void tearDown() throws Exception {
        output.passivate();
        output.close();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi20.Jsapi20SynthesizedOutput#queueSpeakable(org.jvoicexml.SpeakableText, org.jvoicexml.DocumentServer)}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @exception Exception
     *            test failed
     */
    @Test
    public void testQueueSpeakable() throws JVoiceXMLEvent, Exception {
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test");
        output.queueSpeakable(speakable1, null);
        output.waitQueueEmpty();
        Assert.assertFalse("output should be busy", output.isBusy());
        final int size = 3;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_STARTED,
                start.getEvent());
        OutputStartedEvent startedEvent = (OutputStartedEvent) start;
        Assert.assertEquals(speakable1, startedEvent.getSpeakable());
        SynthesizedOutputEvent stop = listener.get(1);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_ENDED ,
                stop.getEvent());
        OutputEndedEvent stoppedEvent = (OutputEndedEvent) stop;
        Assert.assertEquals(speakable1, stoppedEvent.getSpeakable());
        SynthesizedOutputEvent empty = listener.get(2);
        Assert.assertEquals(SynthesizedOutputEvent.QUEUE_EMPTY ,
                empty.getEvent());
        Assert.assertTrue(empty instanceof QueueEmptyEvent);
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
        output.queueSpeakable(speakable, null);
        output.waitQueueEmpty();
        Assert.assertFalse(output.isBusy());
        final int size = 3;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_STARTED,
                start.getEvent());
        OutputStartedEvent startedEvent = (OutputStartedEvent) start;
        Assert.assertEquals(speakable, startedEvent.getSpeakable());
        SynthesizedOutputEvent stop = listener.get(1);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_ENDED ,
                stop.getEvent());
        OutputEndedEvent stoppedEvent = (OutputEndedEvent) stop;
        Assert.assertEquals(speakable, stoppedEvent.getSpeakable());
        SynthesizedOutputEvent empty = listener.get(2);
        Assert.assertEquals(SynthesizedOutputEvent.QUEUE_EMPTY ,
                empty.getEvent());
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
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test for queue empty");
        output.queueSpeakable(speakable1, null);
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
            if (i % 2 == 0) {
                final SsmlDocument ssml = new SsmlDocument();
                final Speak speak = ssml.getSpeak();
                speak.setXmlLang(Locale.US);
                speak.addText("this is test " + i);
                speakable = new SpeakableSsmlText(ssml);
            } else {
                speakable = new SpeakablePlainText("this is test " + i);
            }
            output.queueSpeakable(speakable, null);
        }

        output.waitQueueEmpty();
        Assert.assertFalse(output.isBusy());
        listener.waitSize(2 * max + 1, TIMEOUT);

        int started = 0;
        int ended = 0;
        int emptied = 0;
        for (int i = 0; i < listener.size(); i++) {
            SynthesizedOutputEvent event = listener.get(i);
            switch (event.getEvent()) {
            case SynthesizedOutputEvent.OUTPUT_STARTED:
                ++started;
                break;
            case SynthesizedOutputEvent.OUTPUT_ENDED:
                ++ended;
                break;
            case SynthesizedOutputEvent.QUEUE_EMPTY:
                ++emptied;
                break;
            default:
                Assert.fail("unknown event " + event.getEvent());
                break;
            }
        }
        Assert.assertEquals(max, started);
        Assert.assertEquals(max, ended);
    }
    

    @Test
    public void testCancelSpeakable() throws JVoiceXMLEvent, Exception {
        
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test to interrupt the Text to Speech Engine it is a very long sentence it really is long very long longer than longcat");

        output.queueSpeakable(speakable1, null);
        
        Thread.sleep(2000);
        
        //Assert.assertTrue(output.supportsBargeIn());
//        Assert.assertFalse(output.isBusy());
        
        System.out.println("it is waiting till the output finishes");
        output.cancelOutput();
        
        final int size = 3;
        //listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_STARTED,
                start.getEvent());
        OutputStartedEvent startedEvent = (OutputStartedEvent) start;
        Assert.assertEquals(speakable1, startedEvent.getSpeakable());
        SynthesizedOutputEvent stop = listener.get(1);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_ENDED ,
                stop.getEvent());
        OutputEndedEvent stoppedEvent = (OutputEndedEvent) stop;
        Assert.assertEquals(speakable1, stoppedEvent.getSpeakable());
        SynthesizedOutputEvent empty = listener.get(2);
        Assert.assertEquals(SynthesizedOutputEvent.QUEUE_EMPTY ,
                empty.getEvent());
        Assert.assertTrue(empty instanceof QueueEmptyEvent);
    }
    
}
