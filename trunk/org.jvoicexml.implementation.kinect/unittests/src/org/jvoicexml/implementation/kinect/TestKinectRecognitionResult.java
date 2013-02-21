/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.kinect;

import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test cases for {@link KinectRecognitionResult}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestKinectRecognitionResult {
    /** Maximal diff for parsing the confidence value. */
    private static final float MAX_CONFIDENCE_DIFF = 0.0001f;

    /**
     * Parses the given resource into an extractor.
     * @param resource the resource to parse.
     * @return parsed resource
     * @throws Exception
     *          error parsing the resource
     */
    private SmlInterpretationExtractor readSml(final String resource)
               throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final InputStream in =
                TestSmlInterpretationExtractor.class.getResourceAsStream(
                        resource);
        final Source source = new StreamSource(in);
        final SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        return extractor;
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getUtterance()}.
     * @exception Exception
     *                  test failed
     */
    @Test
    public void testGetUtterance() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals("Hello Dirk", result.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getUtterance()}.
     * @exception Exception
     *                  test failed
     */
    @Test
    public void testGetUtteranceTag() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-tag.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals("Projectmanager", result.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getUtterance()}.
     * @exception Exception
     *                  test failed
     */
    @Test
    public void testGetUtteranceMultipleTags() throws Exception {
        final SmlInterpretationExtractor extractor =
                readSml("sml-multiple-tags.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals("Hello Dirk", result.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getUtterance()}.
     * @exception Exception
     *                  test failed
     */
    @Test
    public void testGetUtteranceCompound() throws Exception {
        final SmlInterpretationExtractor extractor =
                readSml("sml-compound.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals("a small pizza with salami", result.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getWords()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetWordsCompound() throws Exception {
        final SmlInterpretationExtractor extractor =
                readSml("sml-compound.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        final String utterance = extractor.getUtterance();
        final String[] words = utterance.split(" ");
        Assert.assertArrayEquals(words, result.getWords());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getConfidence()}.
     * @exception Exception
     *            test faield
     */
    @Test
    public void testGetConfidence() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals(0.5100203, result.getConfidence(),
                MAX_CONFIDENCE_DIFF);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getWordsConfidence()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetWordsConfidence() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        final float[] confidences = result.getWordsConfidence();
        Assert.assertEquals(2, confidences.length);
        for (int i=0; i < confidences.length; i++) {
            Assert.assertEquals(0.5100203, confidences[i],
                    MAX_CONFIDENCE_DIFF);
        }
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMode()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetMode() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertEquals(ModeType.VOICE, result.getMode());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#isAccepted()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIsAccepted() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertTrue(result.isAccepted());
        
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#isRejected()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIsRejected() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertFalse(result.isRejected());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMark()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetMark() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        final String mark = "testmark";
        result.setMark(mark);
        Assert.assertEquals(mark, result.getMark());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMark()}.
     */
    @Test
    public void testGetSemanticInterpretation() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-simple.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertNull(result.getSemanticInterpretation());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMark()}.
     */
    @Test
    public void testGetSemanticInterpretationTag() throws Exception {
        final SmlInterpretationExtractor extractor = readSml("sml-tag.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertNull(result.getSemanticInterpretation());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMark()}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetSemanticInterpretationMultipleTags()
              throws Exception, JVoiceXMLEvent {
        final SmlInterpretationExtractor extractor = 
                readSml("sml-multiple-tags.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertNotNull(result.getSemanticInterpretation());
        final String json = ScriptingEngine.toJSON(
                (ScriptableObject) result.getSemanticInterpretation());
        Assert.assertEquals(
                "{\"greet\":\"general\",\"who\":\"Projectmanager\"}", json);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.kinect.KinectRecognitionResult#getMark()}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetSemanticInterpretationCompound()
              throws Exception, JVoiceXMLEvent {
        final SmlInterpretationExtractor extractor = 
                readSml("sml-compound.xml");
        final KinectRecognitionResult result =
                new KinectRecognitionResult(extractor);
        Assert.assertNotNull(result.getSemanticInterpretation());
        final String json = ScriptingEngine.toJSON(
                (ScriptableObject) result.getSemanticInterpretation());
        Assert.assertEquals(
                "{\"order\":{\"topping\":\"salami\",\"size\":\"small\"}}",
                json);
    }
}
