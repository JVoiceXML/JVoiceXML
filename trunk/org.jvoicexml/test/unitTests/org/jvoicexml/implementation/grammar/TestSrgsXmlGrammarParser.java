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
        GrammarNode node = graph.getStartNode();
        Assert.assertEquals("this", node.getToken());
        Collection<GrammarNode> arcs = node.getArcList();
        Assert.assertEquals(1, arcs.size());
        node = arcs.iterator().next();
        Assert.assertEquals("is", node.getToken());
        arcs = node.getArcList();
        Assert.assertEquals(1, arcs.size());
        node = arcs.iterator().next();
        Assert.assertEquals("a", node.getToken());
        arcs = node.getArcList();
        Assert.assertEquals(1, arcs.size());
        node = arcs.iterator().next();
        Assert.assertEquals("test", node.getToken());
        Assert.assertTrue("expected a final node", node.isFinalNode());
    }

}
