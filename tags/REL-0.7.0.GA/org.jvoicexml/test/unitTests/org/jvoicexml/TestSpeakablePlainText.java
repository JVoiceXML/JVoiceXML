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

package org.jvoicexml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test case for {@link org.jvoicexml.implementation.SpeakablePlainText}.
 *
 * @see org.jvoicexml.SpeakablePlainText
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
public final class TestSpeakablePlainText {
    /** The object to test. */
    private SpeakablePlainText speakable = null;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp()
            throws Exception {
        speakable = new SpeakablePlainText();
    }

    /**
     * {@inheritDoc}
     */
    @After
    public void tearDown()
            throws Exception {
        speakable = null;
    }

    /**
     * Test method for
     * 'SpeakablePlainText#isSpeakableTextEmpty()'.
     *
     * @see SpeakablePlainText#isSpeakableTextEmpty()
     */
    @Test
    public void testIsSpeakableTextEmpty() {
        Assert.assertTrue(speakable.isSpeakableTextEmpty());

        speakable.appendSpeakableText("some text");
        Assert.assertFalse(speakable.isSpeakableTextEmpty());
    }
}
