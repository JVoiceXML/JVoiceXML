/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/implementation/TestSrgsXmlGrammarImplementation.java $
 * Version: $LastChangedRevision: 2897 $
 * Date:    $Date: 2012-01-16 05:21:39 -0600 (lun, 16 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link SrgsXmlGrammarImplementation}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2897 $
 * @since 0.7
 */
public final class TestSrgsXmlGrammarImplementation {

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(org.jvoicexml.RecognitionResult.RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testAccepts() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("this is a test");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("this is");
        Assert.assertFalse(result2.getUtterance() + " should not be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("this is a test dummy");
        Assert.assertFalse(result3.getUtterance() + " should not be accepted",
                impl.accepts(result3));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(org.jvoicexml.RecognitionResult.RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testAcceptsOneOf() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf politeOneOf = rule.appendChild(OneOf.class);
        final Item politeItem = politeOneOf.appendChild(Item.class);
        politeItem.setOptional();
        politeItem.addText("please");
        rule.addText("press ");
        final OneOf oneOf = rule.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("press 1");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("press 2");
        Assert.assertTrue(result2.getUtterance() + " should be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("press 3");
        Assert.assertTrue(result3.getUtterance() + " should be accepted",
                impl.accepts(result3));
        final DummyRecognitionResult result4 = new DummyRecognitionResult();
        result4.setUtterance("press 4");
        Assert.assertFalse(result4.getUtterance() + " should not be accepted",
                impl.accepts(result4));
        final DummyRecognitionResult result5 = new DummyRecognitionResult();
        result5.setUtterance("please press 1");
        Assert.assertTrue(result5.getUtterance() + " should be accepted",
                impl.accepts(result5));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(org.jvoicexml.RecognitionResult.RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testAcceptsReference() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule ruleDigit = grammar.appendChild(Rule.class);
        ruleDigit.setId("digit");
        final OneOf oneOf = ruleDigit.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final Ruleref ref1 = rule.appendChild(Ruleref.class);
        ref1.setUri("#digit");
        rule.addText("or");
        final Ruleref ref2 = rule.appendChild(Ruleref.class);
        ref2.setUri("#digit");
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("2 or 3");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("1 or 3");
        Assert.assertTrue(result2.getUtterance() + " should be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("3 or 1");
        Assert.assertTrue(result3.getUtterance() + " should be accepted",
                impl.accepts(result3));
        final DummyRecognitionResult result4 = new DummyRecognitionResult();
        result4.setUtterance("2 or 4");
        Assert.assertFalse(result4.getUtterance() + " should not be accepted",
                impl.accepts(result4));
    }
    
    @Test
    public void test() throws Exception {
        final String grammar = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<grammar root=\"test\">"
                + " <rule id=\"test\" scope=\"public\">"
                + "  <one-of>"
                + "   <item>a</item>"
                + "   <item>"
                + "    <item repeat=\"0-1\">a</item>"
                + "    <item>b</item>"
                + "   </item>"
                + "   <item>help</item>"
                + "  </one-of>"
                + " </rule>"
                + "</grammar>";
        final StringReader reader = new StringReader(grammar);
        final InputSource source = new InputSource(reader);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("a");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("a b");
        Assert.assertTrue(result2.getUtterance() + " should be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("b");
        Assert.assertTrue(result3.getUtterance() + " should be accepted",
                impl.accepts(result3));
        final DummyRecognitionResult result4 = new DummyRecognitionResult();
        result4.setUtterance("help");
        Assert.assertTrue(result4.getUtterance() + " should be accepted",
                impl.accepts(result4));
    }
}
