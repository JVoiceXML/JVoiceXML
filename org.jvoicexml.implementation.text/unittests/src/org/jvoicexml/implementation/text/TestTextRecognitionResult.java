/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.implementation.GrammarsExecutor;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link TextRecognitionResult}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestTextRecognitionResult {

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}.
     */
    @Test
    public void testTextRecognitionResult() {
        final String utterance = "test me";
        final TextRecognitionResult result =
            new TextRecognitionResult(utterance, null);
        Assert.assertEquals(utterance, result.getUtterance());
        Assert.assertEquals(ModeType.VOICE, result.getMode());
        Assert.assertEquals(1.0f, result.getConfidence(), .001f);
        Assert.assertNull(result.getMark());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}.
     */
    @Test
    public void testTextRecognitionResultNull() {
        final TextRecognitionResult result =
            new TextRecognitionResult(null, null);
        Assert.assertNull(result.getUtterance());
        Assert.assertEquals(ModeType.VOICE, result.getMode());
        Assert.assertEquals(1.0f, result.getConfidence(), .001f);
        Assert.assertNull(result.getMark());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextRecognitionResult#setMark(java.lang.String)}.
     */
    @Test
    public void testSetMark() {
        final String utterance = "test me";
        final TextRecognitionResult result =
            new TextRecognitionResult(utterance, null);
        Assert.assertNull(result.getMark());

        final String mark = "testmark";
        result.setMark(mark);
        Assert.assertEquals(mark, result.getMark());
    }
    
    public void testAccepts() throws ParserConfigurationException {
        final SrgsXmlDocument doc = new SrgsXmlDocument();
        doc.setGrammarSimple("root", "adam");
        final SrgsXmlGrammarImplementation impl = 
                new SrgsXmlGrammarImplementation(doc);
        final GrammarsExecutor grammars = new GrammarsExecutor();
        grammars.getSet().add(impl);
        
        final TextRecognitionResult resultAccept =
                new TextRecognitionResult("adam", grammars);
        Assert.assertTrue(resultAccept.isAccepted());
        Assert.assertFalse(resultAccept.isRejected());
        
        final TextRecognitionResult resultReject = 
                new TextRecognitionResult("eva", grammars);
        Assert.assertFalse(resultReject.isAccepted());
        Assert.assertTrue(resultReject.isRejected());
        
    }

}
