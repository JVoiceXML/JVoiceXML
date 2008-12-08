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

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Parses an SRGS XML grammar.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class SrgsXmlGrammarParser
    implements GrammarParser<SrgsXmlGrammarImplementation> {
    /**
     * Constructs a new object.
     */
    public SrgsXmlGrammarParser() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarGraph parse(final SrgsXmlGrammarImplementation grammar)
        throws SemanticError {
        final SrgsXmlDocument document = grammar.getGrammar();
        if (document == null) {
            return null;
        }
        final Grammar rootGrammar = document.getGrammar();
        final Rule root = rootGrammar.getRootRule();
        if (root == null) {
            return null;
        }
        final GrammarNode start =
            new EmptyGrammarNode(GrammarNode.START_SEQUENCE);
        final GrammarGraph graph = (GrammarGraph) parse(start, root);
        if (graph == null) {
            return null;
        }
        final GrammarNode end = graph.getEndNode();
        end.setFinalNode(true);
        return graph;
    }

    /**
     * Parses the current node.
     * @param lastNode the last parsed node.
     * @param node the current node to parse.
     * @return the corresponding graph.
     */
    private GrammarNode parse(final GrammarNode lastNode, final XmlNode node) {
        final Collection<XmlNode> nodes = node.getChildren();
        GrammarNode parsedNode = lastNode;
        for (XmlNode current : nodes) {
            if (current instanceof Text) {
                final Text text = (Text) current;
                parsedNode = parse(parsedNode, text);
            } else if (current instanceof OneOf) {
                final OneOf oneOf = (OneOf) current;
                parsedNode = parse(lastNode, oneOf);
            }
        }
        return new GrammarGraph(lastNode, parsedNode);
    }

    /**
     * Parses an alternative.
     * @param lastNode the last parsed node
     * @param oneOf the alternative.
     * @return the parsed alternative
     */
    private GrammarNode parse(final GrammarNode lastNode, final OneOf oneOf) {
        final Collection<Item> items = oneOf.getChildNodes(Item.class);
        for (Item item : items) {
        }
        return lastNode;
    }

    /**
     * Convenience method to convert a {@link Text} into a {@link GrammarNode}.
     * @param lastNode the last parsed node.
     * @param text the current text node
     * @return the grammar node, <code>lastNode</code> if the node can be
     * ignored.
     */
    private GrammarNode parse(final GrammarNode lastNode, final Text text) {
        final String value = text.getTextContent().trim();
        if (value.length() == 0) {
            // Ignore whitespace.
            return lastNode;
        }
        final GrammarNode node = new TokenGrammarNode(GrammarNode.TOKEN, value);
        lastNode.addArc(node);
        return node;
    }
}
