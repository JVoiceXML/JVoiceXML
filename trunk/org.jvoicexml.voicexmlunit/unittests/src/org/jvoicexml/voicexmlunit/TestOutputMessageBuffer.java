/**
 * 
 */
package org.jvoicexml.voicexmlunit;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link OutputMessageBuffer}.
 * @author Dirk Schnelle-Walka
 *
 */
public class TestOutputMessageBuffer {

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.OutputMessageBuffer#nextMessage()}.
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     */
    @Test
    public void testNextMessage() throws Exception, JVoiceXMLEvent {
        final OutputMessageBuffer buffer = new OutputMessageBuffer();
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("hello world");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                buffer.outputSsml(document);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        final SsmlDocument next = buffer.nextMessage();
        Assert.assertEquals(document, next);
    }

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.OutputMessageBuffer#nextMessage()}.
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     */
    @Test
    public void testNextMessageMultiple() throws Exception, JVoiceXMLEvent {
        final int max = 1000;
        final OutputMessageBuffer buffer = new OutputMessageBuffer();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<max; i++) {
                    SsmlDocument document = null;
                    try {
                        document = new SsmlDocument();
                    } catch (ParserConfigurationException e) {
                        Assert.fail(e.getMessage());
                    }
                    final Speak speak = document.getSpeak();
                    speak.addText("test " + i);
                    buffer.outputSsml(document);
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        for (int i=0; i<max; i++) {
            final SsmlDocument document = buffer.nextMessage();
            final Speak speak = document.getSpeak();
            Assert.assertEquals("test " + i, speak.getTextContent());
        }
    }
}
