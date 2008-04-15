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

import junit.framework.TestCase;

import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
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
 * @author Dirk Schnelle
 * @version $Revision: 541 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestJsapi10SynthesizedOutput extends TestCase {
    /** The test object. */
    private Jsapi10SynthesizedOutput synthesizer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        synthesizer.activate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        synthesizer.passivate();
        super.tearDown();
    }

    /**
     * Constructs a new object.
     */
    public TestJsapi10SynthesizedOutput() {
        try {
            Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
        } catch (EngineException e) {
            fail(e.getLocalizedMessage());
        }
        final SynthesizerModeDesc desc = new SynthesizerModeDesc(Locale.US);
        synthesizer = new Jsapi10SynthesizedOutput(desc);
        try {
            synthesizer.open();
        } catch (NoresourceError e) {
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test method for {@link Jsapi10SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testQueueSpeakable() throws JVoiceXMLEvent {
        final SpeakableText speakable1 =
            new SpeakablePlainText("this is a test");
        synthesizer.queueSpeakable(speakable1, false, null);
        assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        assertFalse(synthesizer.isBusy());

        final SpeakableText speakable2 =
            new SpeakablePlainText("this is another test");
        synthesizer.queueSpeakable(speakable1, false, null);
        synthesizer.queueSpeakable(speakable2, false, null);
        assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        assertFalse(synthesizer.isBusy());
    }

    /**
     * Test method for {@link Jsapi10SynthesizedOutput#queueSpeakable(SpeakableText, boolean, org.jvoicexml.DocumentServer)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @throws Exception
     *         Test failed.
     */
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
        voiceKevin.setName("kevin");
        voiceKevin.addText("This is Kevin");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        synthesizer.queueSpeakable(speakable, false, null);
        assertTrue(synthesizer.isBusy());
        synthesizer.waitQueueEmpty();
        assertFalse(synthesizer.isBusy());
    }
}
