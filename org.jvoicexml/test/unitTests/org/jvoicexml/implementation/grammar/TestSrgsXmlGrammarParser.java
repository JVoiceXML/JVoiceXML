/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.grammar;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link SrgsXmlGrammarParser}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestSrgsXmlGrammarParser {
    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.SrgsXmlGrammarParser#parse(org.jvoicexml.implementation.SrgsXmlGrammarImplementation)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    @Test
    public void testParse() throws Exception, JVoiceXMLEvent {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(impl);
        final GrammarChecker checker = new GrammarChecker(graph);
        final String[] words = new String[] {"this", "is", "a", "test"};
        Assert.assertTrue(checker.isValid(words));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.SrgsXmlGrammarParser#parse(SrgsXmlGrammarImplementation)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    @Test
    public void testParseOneOf() throws Exception, JVoiceXMLEvent {
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
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(impl);
        EmptyGrammarNode start = (EmptyGrammarNode) graph.getStartNode();
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

    private void dump(final GrammarNode node, int indent) {
        for (int i = 0; i<indent; i++) {
            System.out.print(' ');
        }
        System.out.print(node.getType() + "\tmin: " + node.getMinRepeat()
                + "\tmax: " + node.getMaxRepeat());
        if (node instanceof TokenGrammarNode) {
            TokenGrammarNode token = (TokenGrammarNode) node;
            System.out.print("\t'" + token.getToken() + "'");
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
