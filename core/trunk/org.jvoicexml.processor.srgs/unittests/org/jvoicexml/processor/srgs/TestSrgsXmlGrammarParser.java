/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/implementation/grammar/TestSrgsXmlGrammarParser.java $
 * Version: $LastChangedRevision: 2153 $
 * Date:    $Date: 2010-04-14 09:25:59 +0200 (Mi, 14 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;

/**
 * Test cases for {@link SrgsXmlGrammarParser}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2153 $
 * @since 0.7
 */
public final class TestSrgsXmlGrammarParser {
    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.SrgsXmlGrammarParser#parse(org.jvoicexml.implementation.SrgsXmlGrammarImplementation)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testParse() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words = new String[] {"this", "is", "a", "test"};
        Assert.assertTrue(checker.isValid(words));
    }

    
    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.SrgsXmlGrammarParser#parse(SrgsXmlGrammarImplementation)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testParseOneOf() throws Exception {
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
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words1 = new String[] {"please", "press", "1"};
        Assert.assertTrue("please press 1 should be valid",
                checker.isValid(words1));
        final String[] words2 = new String[] {"please", "press", "2"};
        Assert.assertTrue("please press 2 should be valid",
                checker.isValid(words2));
        final String[] words3 = new String[] {"please", "press", "3"};
        Assert.assertTrue("please press 3 should be valid",
                checker.isValid(words3));
        final String[] words4 = new String[] {"please", "press", "4"};
        Assert.assertFalse("please press 4 should be invalid",
                checker.isValid(words4));
        final String[] words5 = new String[] {"press", "2"};
        Assert.assertTrue("press 2 should be valid",
                checker.isValid(words5));
        final String[] words6 = new String[] {"please", "2"};
        Assert.assertFalse("please 2 should be invalid",
                checker.isValid(words6));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.SrgsXmlGrammarParser#parse(SrgsXmlGrammarImplementation)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testParseOneOfTag() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf politeOneOf = rule.appendChild(OneOf.class);
        final Item politeItem = politeOneOf.appendChild(Item.class);
        politeItem.setOptional();
        politeItem.addText("please");
        rule.addText("say ");
        final OneOf oneOf = rule.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("one");
        final Tag tag1 = item1.appendChild(Tag.class);
        tag1.addText("'1'");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("two");
        final Tag tag2 = item2.appendChild(Tag.class);
        tag2.addText("'2'");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("three");
        final Tag tag3 = item3.appendChild(Tag.class);
        tag3.addText("'3'");

        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words1 = new String[] {"please", "say", "one"};
        Assert.assertTrue("please say one should be valid",
                checker.isValid(words1));
        final String[] words2 = new String[] {"please", "say", "two"};
        Assert.assertTrue("please say two should be valid",
                checker.isValid(words2));
        final String[] words3 = new String[] {"please", "say", "three"};
        Assert.assertTrue("please say three should be valid",
                checker.isValid(words3));
        final String[] words4 = new String[] {"please", "say", "four"};
        Assert.assertFalse("please say four should be invalid",
                checker.isValid(words4));
        final String[] words5 = new String[] {"say", "two"};
        Assert.assertTrue("say two should be valid",
                checker.isValid(words5));
        final String[] words6 = new String[] {"please", "two"};
        Assert.assertFalse("please two should be invalid",
                checker.isValid(words6));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testParseReference() throws Exception {
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
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words = new String[] {"2", "or", "3"};
        Assert.assertTrue("2 or 3 should be valid", checker.isValid(words));
        final String[] words2 = new String[] {"1", "or", "3"};
        Assert.assertTrue("1 or 3 should be valid", checker.isValid(words2));
        final String[] words3 = new String[] {"3", "or", "1"};
        Assert.assertTrue("3 or 1 should be valid", checker.isValid(words3));
        final String[] words4 = new String[] {"2", "or", "4"};
        Assert.assertFalse("2 or 4 should be invalid", checker.isValid(words4));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testParseRefereceSequence() throws Exception {
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
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        System.out.println("-----------");
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words1 = new String[] {"1", "2"};
        Assert.assertTrue("12 should be valid", checker.isValid(words1));
        final String[] words2 = new String[] {"1", "2", "3", "4"};
        Assert.assertTrue("123 should be valid", checker.isValid(words2));
        final String[] words3 = new String[] {"1", "2", "3", "4"};
        Assert.assertTrue("1234 should be valid", checker.isValid(words3));
        final String[] words4 = new String[] {"1", "2", "3", "4", "1"};
        Assert.assertFalse("12341 should be invalid", checker.isValid(words4));
        final String[] words5 = new String[] {"1"};
        Assert.assertFalse("1 should be invalid", checker.isValid(words5));
    }

    private void dump(final GrammarNode node, int indent) {
        if (indent > 30) {
            return;
        }
        for (int i = 0; i<indent; i++) {
            System.out.print(' ');
        }
        System.out.print(node.getType() + "\tmin: " + node.getMinRepeat()
                + "\tmax: " + node.getMaxRepeat());
        if (node instanceof RuleNode) {
            final RuleNode ruleNode = (RuleNode) node;
            System.out.print("\tid: " + ruleNode.getId());
        }
        if (node instanceof TokenGrammarNode) {
            final TokenGrammarNode token = (TokenGrammarNode) node;
            System.out.print("\t'" + token.getToken() + "'");
        }
        if (node instanceof TagGrammarNode) {
            final TagGrammarNode tag = (TagGrammarNode) node;
            System.out.print("\t'" + tag.getTag() + "'");
        }
        System.out.println("");
        if (node instanceof GrammarGraph) {
            final GrammarGraph graph = (GrammarGraph) node;
            final GrammarNode start = graph.getStartNode();
            dump(start, indent);
        } else {
            final Collection<GrammarNode> arcs = node.getNextNodes();
            for (GrammarNode current : arcs) {
                dump(current, indent + 2);
            }
        }
    }
}
