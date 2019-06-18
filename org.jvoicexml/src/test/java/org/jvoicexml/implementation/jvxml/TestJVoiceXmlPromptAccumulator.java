/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link JVoiceXmlPromptAccumulator}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
public final class TestJVoiceXmlPromptAccumulator {
    /** The instance to test. */
    private JVoiceXmlPromptAccumulator accumulator;

    /**
     * Constructs a new object.
     */
    public TestJVoiceXmlPromptAccumulator() {
    }

    /**
     * Sets up the test environment.
     */
    @Before
    public void setUp() {
        final ImplementationPlatform platform =
                new MockImplementationPlatform();
        accumulator = new JVoiceXmlPromptAccumulator(platform);
    }

    /**
     * Test of getLastSpeakableText method, of class JVoiceXmlPromptAccumulator.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetLastSpeakableText() throws Exception {
        Assert.assertNull(accumulator.getLastSpeakableText());
        final SsmlDocument ssml1 = new SsmlDocument();
        final Speak speak1 = ssml1.getSpeak();
        speak1.addText("this is a test");
        final SpeakableText speakable1 =
                new SpeakableSsmlText(ssml1);
        accumulator.queuePrompt(speakable1);
        Assert.assertEquals(speakable1, accumulator.getLastSpeakableText());
        final SsmlDocument ssml2 = new SsmlDocument();
        final Speak speak2 = ssml2.getSpeak();
        speak2.addText("this is another test");
        final SpeakableText speakable2 = new SpeakableSsmlText(ssml2);
        accumulator.queuePrompt(speakable2);
        Assert.assertEquals(speakable2, accumulator.getLastSpeakableText());
    }

    /**
     * Test of renderPrompts method, of class JVoiceXmlPromptAccumulator.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testRenderPrompts() throws Exception, JVoiceXMLEvent {
        final SsmlDocument ssml1 = new SsmlDocument();
        final Speak speak1 = ssml1.getSpeak();
        speak1.addText("this is a test");
        final SpeakableText speakable1 =
                new SpeakableSsmlText(ssml1);
        accumulator.queuePrompt(speakable1);
        final SsmlDocument ssml2 = new SsmlDocument();
        final Speak speak2 = ssml2.getSpeak();
        speak2.addText("this is another test");
        final SpeakableSsmlText speakable2 = new SpeakableSsmlText(ssml2);
        accumulator.queuePrompt(speakable2);
        final CallControlProperties props = new CallControlProperties();
        accumulator.renderPrompts(null, null, props);
    }
}
