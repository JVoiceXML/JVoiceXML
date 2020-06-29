/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.PriorityType;

/**
 * Test case for org.jvoicexml.implementation.SpeakableSsmlText.
 *
 * @see org.jvoicexml.SpeakableSsmlText
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 */
public final class TestSpeakableSsmlText {
    /**
     * Test method for
     * {@link SpeakableSsmlText#isSpeakableTextEmpty()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testIsSpeakableTextEmpty() throws Exception {
        final SsmlDocument simple = new SsmlDocument();

        final SpeakableSsmlText simpleSpeakable = new SpeakableSsmlText(simple);
        Assert.assertTrue(simpleSpeakable.isSpeakableTextEmpty());

        simpleSpeakable.appendSpeakableText("some text");
        Assert.assertFalse(simpleSpeakable.isSpeakableTextEmpty());

        final SpeakableSsmlText emptySpeakable =
                new SpeakableSsmlText((SsmlDocument)null);

        Assert.assertTrue(emptySpeakable.isSpeakableTextEmpty());

        emptySpeakable.appendSpeakableText("some text");
        Assert.assertTrue(emptySpeakable.isSpeakableTextEmpty());
    }

    /**
     * Test method for
     * {@link SpeakableSsmlText#appendSpeakableText(String)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testAppendSpeakableText() throws Exception {
        final SsmlDocument doc = new SsmlDocument();
        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        Assert.assertTrue(speakable.isSpeakableTextEmpty());
        speakable.appendSpeakableText("some");
        final Speak speak = doc.getSpeak();
        Assert.assertEquals("some", speak.getTextContent());
        speakable.appendSpeakableText(" text");
        Assert.assertEquals("some text", speak.getTextContent());
    }
    

    /**
     * Test method for
     * {@link SpeakableSsmlText#equals(Object)}.
     * @exception Exception
     *            Test failed.
     */
    public void testEquals() throws Exception {
        final SsmlDocument doc = new SsmlDocument();
        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        speakable.appendSpeakableText("some text");
        
        final SsmlDocument otherDoc = new SsmlDocument();
        final SpeakableSsmlText otherSpeakable = new SpeakableSsmlText(otherDoc);
        otherSpeakable.appendSpeakableText("some text");
        Assert.assertEquals(speakable, otherSpeakable);
    }
    
    /**
     * Test method for
     * {@link SpeakableSsmlText#getPriority()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testGetPriority() throws Exception {
        final SsmlDocument doc = new SsmlDocument();
        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        speakable.appendSpeakableText("some text");
        Assert.assertEquals(PriorityType.APPEND, speakable.getPriority());
        speakable.setPriority(PriorityType.CLEAR);
        Assert.assertEquals(PriorityType.CLEAR, speakable.getPriority());
    }
}
