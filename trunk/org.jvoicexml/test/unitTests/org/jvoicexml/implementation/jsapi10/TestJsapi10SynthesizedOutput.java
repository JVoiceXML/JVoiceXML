/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/test/unitTests/org/jvoicexml/implementation/jsapi10/TestSynthesizerModeDescFactory.java $
 * Version: $LastChangedRevision: 541 $
 * Date:    $Date: 2007-11-02 09:26:54 +0100 (Fr, 02 Nov 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.test.implementation.DummySynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.ssml.Voice;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/**
 * Test cases for {@link JVoiceXmlSynthesizerModeDescFactory}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 541 $
 * @since 0.6
 */
public final class TestJsapi10SynthesizedOutput {
    /** Timeout to wait for the listener. */
    private static final int TIMEOUT = 500;

    /** The test object. */
    private Jsapi10SynthesizedOutput synthesizer;

    /** Listener for output events. */
    private DummySynthesizedOutputListener listener;

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
    }

    /**
     * Test setup.
     * @exception Exception
     *            setup failed.
     * @throws NoresourceError
     *         error opening the synthesizer
     */
    @Before
    public void setUp() throws Exception, NoresourceError {
        final SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.US);
        synthesizer = new Jsapi10SynthesizedOutput(desc);
        synthesizer.open();
        synthesizer.activate();
        listener = new DummySynthesizedOutputListener();
        synthesizer.addListener(listener);
    }

    /**
     * Test tear down.
     * @exception Exception
     *            tear down failed
     */
    @After
    public void tearDown() throws Exception {
        synthesizer.passivate();
    }

    /**
     * Test method for {@link Jsapi10SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         test failed.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testQueueSpeakable() throws JVoiceXMLEvent, Exception {
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test");
        synthesizer.queueSpeakable(speakable1, false, null);
        Assert.assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        Assert.assertFalse(synthesizer.isBusy());
        final int size = 3;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        SynthesizedOutputEvent start = listener.get(0);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_STARTED,
                start.getEvent());
        Assert.assertEquals(speakable1, start.getParam());
        SynthesizedOutputEvent stop = listener.get(1);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_ENDED ,
                stop.getEvent());
        Assert.assertEquals(speakable1, stop.getParam());
        SynthesizedOutputEvent empty = listener.get(2);
        Assert.assertEquals(SynthesizedOutputEvent.QUEUE_EMPTY ,
                empty.getEvent());
        Assert.assertNull(empty.getParam());
    }

    /**
     * Test method for {@link Jsapi10SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         test failed.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testQueueMultipleSpeakables() throws JVoiceXMLEvent, Exception {
        final int max = 10;
        for (int i = 0; i < max; i++) {
            SpeakableText speakable =
                new SpeakablePlainText("this is test " + i);
            synthesizer.queueSpeakable(speakable, false, null);
        }
        Assert.assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        Assert.assertFalse(synthesizer.isBusy());
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
        Assert.assertEquals(1, emptied);
    }

    /**
     * Test method for {@link Jsapi10SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @throws Exception
     *         Test failed.
     */
    @Test
    public void testQueueSpeakableSsml() throws Exception, JVoiceXMLEvent {
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("This is a test");
        final Break breakNode = speak.appendChild(Break.class);
        breakNode.setTime("500ms");
        final P p1 = speak.appendChild(P.class);
        p1.addText("Text within P");
        final Mark mark = p1.appendChild(Mark.class);
        mark.setName("testmark");
        final P p2 = speak.appendChild(P.class);
        final Audio ssmlAudio = p2.appendChild(Audio.class);
        ssmlAudio.setSrc("src.wav");
        ssmlAudio.addText("audio replacement");
        final Voice voiceKevin16 = speak.appendChild(Voice.class);
        voiceKevin16.setName("kevin16");
        voiceKevin16.addText("This is Kevin16");
        final Voice voiceKevin = speak.appendChild(Voice.class);
        voiceKevin.setName("alan");
        voiceKevin.addText("This is Alan");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        synthesizer.queueSpeakable(speakable, false, null);
        Assert.assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        Assert.assertFalse(synthesizer.isBusy());
        final int size = 4;
        listener.waitSize(size, TIMEOUT);
        Assert.assertEquals(size, listener.size());
        int pos = 0;
        SynthesizedOutputEvent start = listener.get(pos);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_STARTED,
                start.getEvent());
        Assert.assertEquals(speakable, start.getParam());
        SynthesizedOutputEvent markEvent = listener.get(++pos);
        Assert.assertEquals(SynthesizedOutputEvent.MARKER_REACHED,
                markEvent.getEvent());
        Assert.assertEquals(mark.getName(), markEvent.getParam());
        SynthesizedOutputEvent stop = listener.get(++pos);
        Assert.assertEquals(SynthesizedOutputEvent.OUTPUT_ENDED,
                stop.getEvent());
        Assert.assertEquals(speakable, stop.getParam());
        SynthesizedOutputEvent empty = listener.get(++pos);
        Assert.assertEquals(SynthesizedOutputEvent.QUEUE_EMPTY,
                empty.getEvent());
        Assert.assertNull(empty.getParam());
    }
}
