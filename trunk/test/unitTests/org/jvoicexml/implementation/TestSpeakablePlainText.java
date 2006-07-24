/*
 * File:    $RCSfile: TestSpeakablePlainText.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/03/30 07:50:46 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
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

import junit.framework.*;

/**
 * Test case for org.jvoicexml.implementation.SpeakablePlainText.
 *
 * @see org.jvoicexml.implementation.SpeakablePlainText
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestSpeakablePlainText extends TestCase {
    /** The object to test. */
    private SpeakablePlainText speakable = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        speakable = new SpeakablePlainText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        speakable = null;
        super.tearDown();
    }

    /**
     * Test method for
     * 'org.jvoicexml.implementation.SpeakablePlainText#isSpeakableTextEmpty()
     *
     * @see SpeakablePlainText#isSpeakableTextEmpty()
     */
    public void testIsSpeakableTextEmpty() {
        assertTrue(speakable.isSpeakableTextEmpty());

        speakable.appendSpeakableText("some text");
        assertFalse(speakable.isSpeakableTextEmpty());
    }

}
