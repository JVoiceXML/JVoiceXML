/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/implementation/grammar/TestGrammarChecker.java $
 * Version: $LastChangedRevision: 2153 $
 * Date:    $Date: 2010-04-14 09:25:59 +0200 (Mi, 14 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;

/**
 * Test cases for {@link GrammarChecker}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2153 $
 * @since 0.7
 */
public final class TestGrammarChecker {
    /**
     * Test method for {@link org.jvoicexml.implementation.grammar.GrammarChecker#getInterpretation()}.
     * @exception Exception test failed
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
        System.out.println(document);

        final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
        final GrammarGraph graph = parser.parse(document);
        dump(graph, 2);
        final GrammarChecker checker = new GrammarChecker(graph);
        String[] words = new String[] {"coca", "cola"};
        Assert.assertTrue(checker.isValid(words));
        final String[] tags = checker.getInterpretation();
        Assert.assertEquals(1, tags.length);
        Assert.assertEquals("coke", tags[0]);
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
