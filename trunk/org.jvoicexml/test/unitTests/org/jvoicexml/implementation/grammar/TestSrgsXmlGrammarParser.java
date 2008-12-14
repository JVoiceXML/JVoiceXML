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
import java.util.Iterator;

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
        rule.addText("this");
        rule.addText(" is");
        rule.addText(" a");
        rule.addText(" test");
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(impl);
        EmptyGrammarNode start = (EmptyGrammarNode) graph.getStartNode();
        TokenGrammarNode node = getNextTokenNode(start);
        Assert.assertEquals("this", node.getToken());
        node = getNextTokenNode(node);
        Assert.assertEquals("is", node.getToken());
        node = getNextTokenNode(node);
        Assert.assertEquals("a", node.getToken());
        node = getNextTokenNode(node);
        Assert.assertEquals("test", node.getToken());
        Assert.assertTrue("expected a final node", node.isFinalNode());
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
        GrammarNode node = getNextNode(start);
        dump(graph, 0);
//        Assert.assertEquals("please", node.getToken());
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
    /**
     * Convenience method to retrieve the next node.
     * @param node the current node.
     * @return next token node.
     */
    private GrammarNode getNextNode(final GrammarNode node) {
        final Collection<GrammarNode> destinations = node.getNextNodes();
        if (destinations.size() == 0) {
            return null;
        }
        final Iterator<GrammarNode> iterator = destinations.iterator();
        return iterator.next();
    }

    /**
     * Convenience method to retrieve the next token node.
     * @param node the current node.
     * @return next token node.
     */
    private TokenGrammarNode getNextTokenNode(final GrammarNode node) {
        GrammarNode current = null;
        do {
            final Collection<GrammarNode> destinations = node.getNextNodes();
            if (destinations.size() > 0) {
                final Iterator<GrammarNode> iterator = destinations.iterator();
                current = iterator.next();
                if (current instanceof GrammarGraph) {
                    final GrammarGraph graph = (GrammarGraph) current;
                    current = graph.getStartNode();
                }
                if (current instanceof TokenGrammarNode) {
                    return (TokenGrammarNode) current;
                }
            }
        } while (current != null);
        return null;
    }
}
