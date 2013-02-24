/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/unittests/org/jvoicexml/implementation/text/TestTextRecognitionResult.java $
 * Version: $LastChangedRevision: 2245 $
 * Date:    $Date: 2010-05-27 09:24:13 -0400 (Do, 27 Mai 2010) $
 * Author:  $LastChangedBy: g-assiouras $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Test cases for {@link TextRecognitionResult}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2245 $
 * @since 0.6
 */
public final class TestTextRecognitionResult extends TestCase {
    
    
    /**Reference to the GrammarChecker object.*/
    private GrammarChecker grammarChecker;
    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}.
     */
    public void testTextRecognitionResult() {
        final String utterance = "test me";
        final TextRecognitionResult result =
            new TextRecognitionResult(utterance, grammarChecker);
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
            new TextRecognitionResult(null, grammarChecker);
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
            new TextRecognitionResult(utterance, grammarChecker);
        assertNull(result.getMark());

        final String mark = "testmark";
        result.setMark(mark);
        assertEquals(mark, result.getMark());
    }

}
