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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.processor.srgs.GrammarGraph;
import org.jvoicexml.processor.srgs.SrgsXmlGrammarParser;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link TextRecognitionResult}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestTextRecognitionResult {
    /** Reference to the GrammarChecker object. */
    private GrammarChecker grammarChecker;

    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     * @since 0.7.7
     */
    @Before
    public void setUp() throws Exception {
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final InputStream input = TestTextRecognitionResult.class
                .getResourceAsStream("yesno.srgs");
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);
        final GrammarGraph graph = parser.parse(document);
        grammarChecker = new GrammarChecker(null, graph);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}
     * .
     */
    @Test
    public void testTextRecognitionResult() {
        final String utterance = "test me";
        final TextRecognitionResult result = new TextRecognitionResult(
                utterance, grammarChecker);
        Assert.assertEquals(utterance, result.getUtterance());
        Assert.assertEquals(ModeType.VOICE, result.getMode());
        Assert.assertEquals(1.0f, result.getConfidence(), .001f);
        Assert.assertNull(result.getMark());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.text.TextRecognitionResult#TextRecognitionResult(java.lang.String)}
     * .
     */
    @Test
    public void testTextRecognitionResultNull() {
        final TextRecognitionResult result = new TextRecognitionResult(null,
                grammarChecker);
        Assert.assertNull(result.getUtterance());
        Assert.assertEquals(ModeType.VOICE, result.getMode());
        Assert.assertEquals(1.0f, result.getConfidence(), .001f);
        Assert.assertNull(result.getMark());
    }

    @Test
    public void testGetSemanticInterpretation() {
        final String utterance = "yes";
        final TextRecognitionResult result = new TextRecognitionResult(
                utterance, grammarChecker);
        Assert.assertTrue(result.isAccepted());
        Assert.assertEquals("yes", result.getSemanticInterpretation());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.text.TextRecognitionResult#setMark(java.lang.String)}
     * .
     */
    @Test
    public void testSetMark() {
        final String utterance = "test me";
        final TextRecognitionResult result = new TextRecognitionResult(
                utterance, grammarChecker);
        Assert.assertNull(result.getMark());

        final String mark = "testmark";
        result.setMark(mark);
        Assert.assertEquals(mark, result.getMark());
    }

}
