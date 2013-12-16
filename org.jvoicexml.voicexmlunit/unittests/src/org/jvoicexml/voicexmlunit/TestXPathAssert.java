package org.jvoicexml.voicexmlunit;

import org.junit.Test;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link XPathAssert}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4058 $
 * @since 0.7.7
 */
public final class TestXPathAssert {

    /**
     * Test method to check the text contents of a node.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testAssertEqualsText() throws Exception {
        SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("hello world");
        XPathAssert.assertEquals(document, "/speak[text()]", "hello world");
    }


    /**
     * Test method to check the text contents of a node's attribute.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testAssertEqualsAttribute() throws Exception {
        SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        final Audio audio = speak.appendChild(Audio.class);
        audio.setSrc("test.wav");
        XPathAssert.assertEquals(document, "/speak/audio/@src", "test.wav");
    }
}
