/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.srgs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleParse;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleToken;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link GrammarChecker}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestGrammarChecker {
    private URI writeToFile(SrgsXmlDocument document, final File file)
            throws IOException {
        final FileWriter writer = new FileWriter(file);
        final String xml = document.toString();
        writer.write(xml);
        writer.close();
        return file.toURI();
    }

    @Test
    public void testTokens() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final String[] input = new String[] { "this", "is", "a", "test" };
        final GrammarChecker checker = new GrammarChecker(manager, null);
        final RuleComponent validRule = checker.isValid(ruleGrammar, input);
        Assert.assertTrue(validRule instanceof RuleToken);
        final RuleToken token = (RuleToken) validRule;
        Assert.assertEquals("this is a test", token.getText());
    }

    @Test
    public void testOneOf() throws Exception {
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
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final String[] input1 = new String[] { "please", "press", "1" };
        final GrammarChecker checker = new GrammarChecker(manager, null);
        final RuleComponent validRule1 = checker.isValid(ruleGrammar, input1);
        Assert.assertTrue(validRule1 instanceof RuleSequence);
        final RuleSequence sequence1 = (RuleSequence) validRule1;
        final RuleComponent[] components1 = sequence1.getRuleComponents();
        Assert.assertEquals(
                "<one-of><item><item repeat=\"1\">please</item></item></one-of>",
                components1[0].toString());
        Assert.assertEquals("press", components1[1].toString());
        Assert.assertEquals("<one-of><item>1</item></one-of>",
                components1[2].toString());
        final String[] input2 = new String[] { "please", "press", "2" };
        final RuleComponent validRule2 = checker.isValid(ruleGrammar, input2);
        Assert.assertTrue(validRule2 instanceof RuleSequence);
        final RuleSequence sequence2 = (RuleSequence) validRule2;
        final RuleComponent[] components2 = sequence2.getRuleComponents();
        Assert.assertEquals(
                "<one-of><item><item repeat=\"1\">please</item></item></one-of>",
                components2[0].toString());
        Assert.assertEquals("press", components2[1].toString());
        Assert.assertEquals("<one-of><item>2</item></one-of>",
                components2[2].toString());
        final String[] input3 = new String[] { "please", "press", "3" };
        final RuleComponent validRule3 = checker.isValid(ruleGrammar, input3);
        Assert.assertTrue(validRule3 instanceof RuleSequence);
        final RuleSequence sequence3 = (RuleSequence) validRule3;
        final RuleComponent[] components3 = sequence3.getRuleComponents();
        Assert.assertEquals(
                "<one-of><item><item repeat=\"1\">please</item></item></one-of>",
                components3[0].toString());
        Assert.assertEquals("press", components3[1].toString());
        Assert.assertEquals("<one-of><item>3</item></one-of>",
                components3[2].toString());
        final String[] input4 = new String[] { "please", "press", "4" };
        final RuleComponent validRule4 = checker.isValid(ruleGrammar, input4);
        Assert.assertNull(validRule4);
        final String[] input5 = new String[] { "press", "2" };
        final RuleComponent validRule5 = checker.isValid(ruleGrammar, input5);
        Assert.assertTrue(validRule5 instanceof RuleSequence);
        final RuleSequence sequence5 = (RuleSequence) validRule5;
        final RuleComponent[] components5 = sequence5.getRuleComponents();
        Assert.assertEquals(
                "<one-of><item><item repeat=\"0\"></item></item></one-of>",
                components5[0].toString());
        Assert.assertEquals("press", components5[1].toString());
        Assert.assertEquals("<one-of><item>2</item></one-of>",
                components5[2].toString());
    }

    @Test
    public void testReference() throws Exception {
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
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final String[] input1 = new String[] { "2", "or", "3" };
        final GrammarChecker checker = new GrammarChecker(manager, null);
        final RuleComponent validRule1 = checker.isValid(ruleGrammar, input1);
        Assert.assertTrue(validRule1 instanceof RuleSequence);
        final RuleSequence sequence1 = (RuleSequence) validRule1;
        final RuleComponent[] components1 = sequence1.getRuleComponents();
        final RuleParse parse11 = (RuleParse) components1[0];
        final RuleReference reference11 = parse11.getRuleReference();
        Assert.assertEquals("digit", reference11.getRuleName());
        Assert.assertEquals("<one-of><item>2</item></one-of>", parse11
                .getParse().toString());
        Assert.assertEquals("or", components1[1].toString());
        final RuleParse parse12 = (RuleParse) components1[2];
        final RuleReference reference12 = parse12.getRuleReference();
        Assert.assertEquals("digit", reference12.getRuleName());
        Assert.assertEquals("<one-of><item>3</item></one-of>", parse12
                .getParse().toString());
        final String[] input2 = new String[] { "1", "or", "3" };
        final RuleComponent validRule2 = checker.isValid(ruleGrammar, input2);
        Assert.assertTrue(validRule2 instanceof RuleSequence);
        final RuleSequence sequence2 = (RuleSequence) validRule2;
        final RuleComponent[] components2 = sequence2.getRuleComponents();
        final RuleParse parse21 = (RuleParse) components2[0];
        final RuleReference reference21 = parse21.getRuleReference();
        Assert.assertEquals("digit", reference21.getRuleName());
        Assert.assertEquals("<one-of><item>1</item></one-of>", parse21
                .getParse().toString());
        Assert.assertEquals("or", components2[1].toString());
        final RuleParse parse22 = (RuleParse) components2[2];
        final RuleReference reference22 = parse22.getRuleReference();
        Assert.assertEquals("digit", reference22.getRuleName());
        Assert.assertEquals("<one-of><item>3</item></one-of>", parse22
                .getParse().toString());
        final String[] input3 = new String[] { "3", "or", "1" };
        final RuleComponent validRule3 = checker.isValid(ruleGrammar, input3);
        Assert.assertTrue(validRule3 instanceof RuleSequence);
        final RuleSequence sequence3 = (RuleSequence) validRule2;
        final RuleComponent[] components3 = sequence3.getRuleComponents();
        final RuleParse parse31 = (RuleParse) components3[0];
        final RuleReference reference31 = parse31.getRuleReference();
        Assert.assertEquals("digit", reference31.getRuleName());
        Assert.assertEquals("<one-of><item>1</item></one-of>", parse31
                .getParse().toString());
        Assert.assertEquals("or", components3[1].toString());
        final RuleParse parse32 = (RuleParse) components3[2];
        final RuleReference reference32 = parse32.getRuleReference();
        Assert.assertEquals("digit", reference32.getRuleName());
        Assert.assertEquals("<one-of><item>3</item></one-of>", parse32
                .getParse().toString());
        final String[] input4 = new String[] { "1", "or", "4" };
        final RuleComponent validRule4 = checker.isValid(ruleGrammar, input4);
        Assert.assertNull(validRule4);
    }

    @Test
    public void testReferenceSequence() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setMode(ModeType.DTMF);
        grammar.setRoot("pin");
        final Rule digit = grammar.appendChild(Rule.class);
        digit.setId("digit");
        final OneOf oneOf = digit.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final Item item4 = oneOf.appendChild(Item.class);
        item4.addText("4");
        final Rule pin = grammar.appendChild(Rule.class);
        pin.setId("pin");
        pin.makePublic();
        final Item item = pin.appendChild(Item.class);
        item.setRepeat(2, 4);
        final Ruleref ref = item.appendChild(Ruleref.class);
        ref.setUri(digit);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final GrammarChecker checker = new GrammarChecker(manager, null);
        final String[] words1 = new String[] { "1", "2" };
        final RuleComponent validRule1 = checker.isValid(ruleGrammar, words1);
        Assert.assertTrue(validRule1 instanceof RuleCount);
        final RuleCount count1 = (RuleCount) validRule1;
        final RuleSequence sequence1 = (RuleSequence) count1.getRuleComponent();
        final RuleComponent[] components1 = sequence1.getRuleComponents();
        final RuleParse parse11 = (RuleParse) components1[0];
        Assert.assertEquals(
                "<one-of><item>1</item></one-of>", parse11.getParse().toString());
        final RuleParse parse12 = (RuleParse) components1[1];
        Assert.assertEquals(
                "<one-of><item>2</item></one-of>", parse12.getParse().toString());
        final String[] words2 = new String[] { "1", "2", "3", "4" };
        final RuleComponent validRule2 = checker.isValid(ruleGrammar, words2);
        Assert.assertTrue(validRule2 instanceof RuleCount);
        final RuleCount count2 = (RuleCount) validRule2;
        final RuleSequence sequence2 = (RuleSequence) count2.getRuleComponent();
        final RuleComponent[] components2 = sequence2.getRuleComponents();
        final RuleParse parse21 = (RuleParse) components2[0];
        Assert.assertEquals(
                "<one-of><item>1</item></one-of>", parse21.getParse().toString());
        final RuleParse parse22 = (RuleParse) components2[1];
        Assert.assertEquals(
                "<one-of><item>2</item></one-of>", parse22.getParse().toString());
        final RuleParse parse23 = (RuleParse) components2[2];
        Assert.assertEquals(
                "<one-of><item>3</item></one-of>", parse23.getParse().toString());
        final RuleParse parse24 = (RuleParse) components2[3];
        Assert.assertEquals(
                "<one-of><item>4</item></one-of>", parse24.getParse().toString());
        final String[] words3 = new String[] { "1", "2", "3", "4", "1" };
        final RuleComponent validRule3 = checker.isValid(ruleGrammar, words3);
        Assert.assertNull("12341 should be invalid", validRule3);
        final String[] words4 = new String[] { "1" };
        final RuleComponent validRule4 = checker.isValid(ruleGrammar, words4);
        Assert.assertNull("1 should be valid", validRule4);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.grammar.GrammarChecker#getInterpretation()}
     * .
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetInterpretation() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("drink");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("drink");
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item cokeItem = oneof.appendChild(Item.class);
        cokeItem.addText("coke");
        final Item pepsiItem = oneof.appendChild(Item.class);
        pepsiItem.addText("pepsi");
        final Item cocaColaItem = oneof.appendChild(Item.class);
        cocaColaItem.addText("coca cola");
        final Tag tag = cocaColaItem.appendChild(Tag.class);
        tag.addText("coke");

        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(null, graph);
        String[] words = new String[] { "coca", "cola" };
        Assert.assertTrue(checker.isValid(words));
        final String[] tags = checker.getInterpretation();
        Assert.assertEquals(1, tags.length);
        Assert.assertEquals("coke", tags[0]);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.grammar.GrammarChecker#getInterpretation()}
     * .
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetInterpretationCompoundObject() throws Exception {
        final InputStream in = TestGrammarChecker.class
                .getResourceAsStream("pizza.srgs");
        final InputSource source = new InputSource(in);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);

        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(null, graph);
        String[] words = new String[] { "a", "small", "pizza", "with", "ham" };
        Assert.assertTrue(checker.isValid(words));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.grammar.GrammarChecker#getInterpretation()}
     * .
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testMaxRepeatWithLessTokens() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule digit = grammar.appendChild(Rule.class);
        digit.setId("digit");
        final OneOf oneOf = digit.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final Rule digits = grammar.appendChild(Rule.class);
        digits.setId("digits");
        grammar.setRoot(digits);
        final Item digitsItem = digits.appendChild(Item.class);
        digitsItem.setRepeat(1, 10);
        final Ruleref ref = digitsItem.appendChild(Ruleref.class);
        ref.setUri(digit);

        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(null, graph);
        String[] words = new String[] { "1" };
        Assert.assertTrue(checker.isValid(words));
    }

    @Test
    public void testIsValidPizza() throws Exception {
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final URL url = TestJVoiceXmlGrammarManager.class
                .getResource("pizza-de.xml");
        final URI uri = url.toURI();
        final RuleGrammar grammar = (RuleGrammar) manager.loadGrammar(uri);
        final GrammarChecker checker = new GrammarChecker(manager, null);
        final String[] input = new String[] { "ich", "möchte", "eine",
                "kleine", "Pizza", "mit", "Salami" };
        final RuleComponent validRule = checker.isValid(grammar, input);
        System.out.println(validRule);
    }

    private void dump(final GrammarNode node, int indent) {
        if (indent > 30) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(' ');
        }
        System.out.print(node.getType() + "\tmin: " + node.getMinRepeat()
                + "\tmax: " + node.getMaxRepeat());
        if (node instanceof RuleNode) {
            RuleNode ruleNode = (RuleNode) node;
            System.out.print("\tid: " + ruleNode.getId());
        }
        if (node instanceof TokenGrammarNode) {
            TokenGrammarNode token = (TokenGrammarNode) node;
            System.out.print("\t'" + token.getToken() + "'");
        }
        if (node instanceof TagGrammarNode) {
            TagGrammarNode tag = (TagGrammarNode) node;
            System.out.print("\t'" + tag.getTag() + "'");
        }
        System.out.println("");
        if (node instanceof GrammarGraph) {
            GrammarGraph graph = (GrammarGraph) node;
            dump(graph.getStartNode(), indent);
        } else {
            Collection<GrammarNode> arcs = node.getNextNodes();
            for (GrammarNode current : arcs) {
                dump(current, indent + 2);
            }
        }
    }
}
