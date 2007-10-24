/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import org.jvoicexml.xml.srgs.ModeType;

import junit.framework.TestCase;

/**
 * Test cases for {@link TextRecognitionResult}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestTextRecognitionResult extends TestCase {
    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}.
     */
    public void testTextRecognitionResult() {
        final String utterance = "test me";
        final TextRecognitionResult result =
            new TextRecognitionResult(utterance);
        assertEquals(utterance, result.getUtterance());
        assertEquals(ModeType.VOICE, result.getMode());
        assertEquals(1.0f, result.getConfidence());
        assertNull(result.getMark());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}.
     */
    public void testTextRecognitionResultNull() {
        final TextRecognitionResult result =
            new TextRecognitionResult(null);
        assertNull(result.getUtterance());
        assertEquals(ModeType.VOICE, result.getMode());
        assertEquals(1.0f, result.getConfidence());
        assertNull(result.getMark());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#setMark(java.lang.String)}.
     */
    public void testSetMark() {
        final String utterance = "test me";
        final TextRecognitionResult result =
            new TextRecognitionResult(utterance);
        assertNull(result.getMark());

        final String mark = "testmark";
        result.setMark(mark);
        assertEquals(mark, result.getMark());
    }

}
