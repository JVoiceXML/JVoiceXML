/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test case for org.jvoicexml.implementation.SpeakableSsmlText.
 *
 * @see org.jvoicexml.SpeakableSsmlText
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSpeakableSsmlText
        extends TestCase {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        super.setUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
            throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link SpeakableSsmlText#isSpeakableTextEmpty()}.
     * @exception Exception
     *            Test failed.
     */
    public void testIsSpeakableTextEmpty() throws Exception {
        final SsmlDocument simple = new SsmlDocument();

        final SpeakableSsmlText simpleSpeakable = new SpeakableSsmlText(simple);

        assertTrue(simpleSpeakable.isSpeakableTextEmpty());

        simpleSpeakable.appendSpeakableText("some text");
        assertFalse(simpleSpeakable.isSpeakableTextEmpty());

        final SpeakableSsmlText emptySpeakable = new SpeakableSsmlText(null);

        assertTrue(emptySpeakable.isSpeakableTextEmpty());

        emptySpeakable.appendSpeakableText("some text");
        assertTrue(emptySpeakable.isSpeakableTextEmpty());
    }

    /**
     * Test method for
     * {@link SpeakableSsmlText#appendSpeakableText(String)}.
     * @exception Exception
     *            Test failed.
     */
    public void testAppendSpeakableText() throws Exception {
        final SsmlDocument doc = new SsmlDocument();

        final SpeakableSsmlText speakable = new SpeakableSsmlText(doc);
        assertTrue(speakable.isSpeakableTextEmpty());
        speakable.appendSpeakableText("some");
        final Speak speak = doc.getSpeak();
        assertEquals("some", speak.getTextContent());
        speakable.appendSpeakableText(" text");
        assertEquals("some text", speak.getTextContent());
    }
}
