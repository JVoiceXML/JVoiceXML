/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.implementation.jvxml;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link JVoiceXmlPromptAccumulator}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
public class TestJVoiceXmlPromptAccumulator {
    /** The instance to test. */
    private JVoiceXmlPromptAccumulator accumulator;

    /**
     * Constructs a new object.
     */
    public TestJVoiceXmlPromptAccumulator() {
    }

    @Before
    public void setUp() {
        final ImplementationPlatform platform =
                new DummyImplementationPlatform();
        accumulator = new JVoiceXmlPromptAccumulator(platform);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setPromptTimeout method, of class JVoiceXmlPromptAccumulator.
     */
    @Test
    public void testSetPromptTimeout() {
        final long promptTimeout = 30L;
        accumulator.setPromptTimeout(promptTimeout);
        Assert.assertEquals(promptTimeout, accumulator.getPromptTimeout());
        Assert.assertNull(accumulator.getLastSpeakableText());
    }

    /**
     * Test of getLastSpeakableText method, of class JVoiceXmlPromptAccumulator.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetLastSpeakableText() throws Exception {
        Assert.assertNull(accumulator.getLastSpeakableText());
        final SpeakableText speakable1 =
                new SpeakablePlainText("this is a test");
        accumulator.queuePrompt(speakable1);
        Assert.assertEquals(speakable1, accumulator.getLastSpeakableText());
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("this is another test");
        final SpeakableText speakable2 = new SpeakableSsmlText(document);
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
        final SpeakableText speakable1 =
                new SpeakablePlainText("this is a test");
        accumulator.queuePrompt(speakable1);
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("this is another test");
        final SpeakableSsmlText speakable2 = new SpeakableSsmlText(document);
        final long timeout = 40;
        speakable2.setTimeout(timeout);
        accumulator.queuePrompt(speakable2);
        accumulator.renderPrompts(null);
        Assert.assertEquals(timeout, accumulator.getPromptTimeout());
    }

}