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
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link SmlInterpretationExtractor}.
 * @author Dirk Schnelle-Walka
 *
 */
public final class TestSmlInterpretationExtractor {
    /** Maximal diff for parsing the confidence value. */
    private static final float MAX_CONFIDENCE_DIFF = 0.0001f;

    /**
     * Test case for a simple recognition process.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testSimple() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final InputStream in =
                TestSmlInterpretationExtractor.class.getResourceAsStream(
                        "sml-simple.xml");
        final Source source = new StreamSource(in);
        final SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        Assert.assertEquals("Hello Dirk",
                extractor.getUtterance());
        Assert.assertEquals(0.5100203, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        Assert.assertEquals(0, interpretations.size());
        Assert.assertEquals("Hello Dirk", extractor.getUtteranceTag());
    }

    /**
     * Test case for a simple recognition process with a single tag.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testTag() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final InputStream in =
                TestSmlInterpretationExtractor.class.getResourceAsStream(
                        "sml-tag.xml");
        final Source source = new StreamSource(in);
        final SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        Assert.assertEquals("Good morning Dirk",
                extractor.getUtterance());
        Assert.assertEquals(0.7378733, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        Assert.assertEquals(0, interpretations.size());
        Assert.assertEquals("Projectmanager", extractor.getUtteranceTag());
    }

    /**
     * Test case for multiple tags.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testMultipleTags() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final InputStream in =
                TestSmlInterpretationExtractor.class.getResourceAsStream(
                        "sml-multiple-tags.xml");
        final Source source = new StreamSource(in);
        final SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        Assert.assertEquals("Hello Dirk",
                extractor.getUtterance());
        Assert.assertEquals(0.6734907, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        Assert.assertEquals(2, interpretations.size());
        final SmlInterpretation greet = interpretations.get(0);
        Assert.assertEquals("greet", greet.getTag());
        Assert.assertEquals("general", greet.getValue());
        Assert.assertEquals(2.069468E-02f, greet.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final SmlInterpretation who = interpretations.get(1);
        Assert.assertEquals("who", who.getTag());
        Assert.assertEquals("Projectmanager", who.getValue());
        Assert.assertEquals(2.069468E-02f, who.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        Assert.assertEquals("", extractor.getUtteranceTag());
    }

    /**
     * Test case for a compound object.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCompundObject() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final InputStream in =
                TestSmlInterpretationExtractor.class.getResourceAsStream(
                        "sml-compound.xml");
        final Source source = new StreamSource(in);
        final SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        Assert.assertEquals("a small pizza with salami",
                extractor.getUtterance());
        Assert.assertEquals(0.8081474f, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        Assert.assertEquals(3, interpretations.size());
        final SmlInterpretation order = interpretations.get(0);
        Assert.assertEquals("order", order.getTag());
        Assert.assertNull(order.getValue());
        Assert.assertEquals(0.8131593f, order.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final SmlInterpretation size = interpretations.get(1);
        Assert.assertEquals("order.size", size.getTag());
        Assert.assertEquals("small", size.getValue());
        Assert.assertEquals(0.8131593f, size.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        final SmlInterpretation topping = interpretations.get(2);
        Assert.assertEquals("order.topping", topping.getTag());
        Assert.assertEquals("salami", topping.getValue());
        Assert.assertEquals(0.8131593f, topping.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        Assert.assertEquals("", extractor.getUtteranceTag());
    }
}
