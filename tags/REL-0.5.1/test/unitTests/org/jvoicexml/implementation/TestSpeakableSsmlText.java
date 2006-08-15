/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test case for org.jvoicexml.implementation.SpeakableSsmlText.
 *
 * @see org.jvoicexml.implementation.SpeakableSsmlText
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestSpeakableSsmlText
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
     * 'org.jvoicexml.implementation.SpeakableSsmlText#isSpeakableTextEmpty()
     *
     * @see SpeakableSsmlText#isSpeakableTextEmpty()
     */
    public void testIsSpeakableTextEmpty() {
        SsmlDocument simple = null;
        try {
            simple = new SsmlDocument();
        } catch (ParserConfigurationException ex) {
            fail(ex.getMessage());
        }

        final SpeakableSsmlText simpleSpeakable = new SpeakableSsmlText(simple);

        assertTrue(simpleSpeakable.isSpeakableTextEmpty());

        simpleSpeakable.appendSpeakableText("some text");
        assertFalse(simpleSpeakable.isSpeakableTextEmpty());

        final SpeakableSsmlText emptySpeakable = new SpeakableSsmlText(null);

        assertTrue(emptySpeakable.isSpeakableTextEmpty());

        emptySpeakable.appendSpeakableText("some text");
        assertTrue(emptySpeakable.isSpeakableTextEmpty());
    }

}
