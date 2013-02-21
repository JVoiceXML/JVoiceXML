/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;

/**
 * Parses an SRGS XML grammar into a {@link GrammarGraph}.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class SrgsXmlGrammarParser
    implements GrammarParser<SrgsXmlGrammarImplementation> {
    /** The grammar to parse. */
    private Grammar grammar;

    /**
     * Constructs a new object.
     */
    public SrgsXmlGrammarParser() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarGraph parse(final SrgsXmlGrammarImplementation impl)
        throws SemanticError {
        final SrgsXmlDocument document = impl.getGrammar();
        if (document == null) {
            return null;
        }
        grammar = document.getGrammar();
        final Rule root = grammar.getRootRule();
        if (root == null) {
            return null;
        }
        final GrammarNode start = new EmptyGrammarNode(GrammarNodeType.START);
        final GrammarNode node = parse(start, root);
        final GrammarGraph graph;
        if (node instanceof GrammarGraph) {
            graph = (GrammarGraph) node;
        } else {
            graph = new GrammarGraph(node, node);
        }
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
        final GrammarNode start =
            new EmptyGrammarNode(GrammarNodeType.SEQUENCE_START);
        GrammarNode parsedNode = start;
        int count = 0;
        for (XmlNode current : nodes) {
            GrammarNode result = start;
            if (current instanceof Text) {
                final Text text = (Text) current;
                result = parse(parsedNode, text);
            } else if (current instanceof Tag) {
                final Tag tag = (Tag) current;
                result = parse(parsedNode, tag);
            } else if (current instanceof Ruleref) {
                final Ruleref ref = (Ruleref) current;
                result = parse(parsedNode, ref);
            } else if (current instanceof OneOf) {
                final OneOf oneOf = (OneOf) current;
                result = parse(parsedNode, oneOf);
            } else if (current instanceof Item) {
                final Item item = (Item) current;
                result = parse(parsedNode, item);
            }

            if (result != parsedNode) {
                parsedNode.addNext(result);
                parsedNode = result;
                ++count;
            }
        }
        if (count == 0) {
            return lastNode;
        } else if (count == 1) {
            return parsedNode;
        } else {
            GrammarNode end =
                new EmptyGrammarNode(GrammarNodeType.SEQUENCE_END);
            parsedNode.addNext(end);
            return new GrammarGraph(start, end);
        }
    }

    /**
     * Parses an item.
     * @param lastNode the last parsed node
     * @param item the current node
     * @return the parsed alternative
     */
    private GrammarNode parse(final GrammarNode lastNode, final Item item) {
        final GrammarNode node = parse(lastNode, (XmlNode) item);
        if (node == lastNode) {
            return lastNode;
        }
        final int min = item.getMinRepeat();
        node.setMinRepeat(min);
        final int max = item.getMaxRepeat();
        node.setMaxRepeat(max);
        return node;
    }

    /**
     * Parses an alternative.
     * @param lastNode the last parsed node
     * @param oneOf the alternative.
     * @return the parsed alternative
     */
    private GrammarNode parse(final GrammarNode lastNode, final OneOf oneOf) {
        final Collection<Item> items = oneOf.getChildNodes(Item.class);
        final GrammarNode start =
            new EmptyGrammarNode(GrammarNodeType.ALTERNATIVE_START);
        final GrammarNode end =
            new EmptyGrammarNode(GrammarNodeType.ALTERNATIVE_END);
        for (Item item : items) {
            final GrammarNode parsedNode = parse(start, item);
            if (parsedNode != start) {
                start.addNext(parsedNode);
                parsedNode.addNext(end);
            }
        }
        return new GrammarGraph(start, end);
    }

    /**
     * Parses a rule node.
     * @param lastNode the last parsed node
     * @param rule the the rule.
     * @return the parsed node
     */
    private GrammarNode parse(final GrammarNode lastNode, final Rule rule) {
        GrammarNode parsedNode = parse(lastNode, (XmlNode) rule);
        final GrammarNode end;
        if (parsedNode instanceof GrammarGraph) {
            final GrammarGraph graph = (GrammarGraph) parsedNode;
            end = graph.getEndNode();
        } else {
            end = parsedNode;
        }
        final String id = rule.getId();
        return new RuleNode(id, parsedNode, end);
    }

    /**
     * Parses a rule reference.
     * @param lastNode the last parsed node
     * @param ref the the reference.
     * @return the parsed node
     */
    private GrammarNode parse(final GrammarNode lastNode, final Ruleref ref) {
        /** @todo Implement VOID-handling */
        // NULL and GARBAGE are expected to be handled by the recognizer so
        // we can simply ignore them here.
        if (ref.isSpecialGarbage() || ref.isSpecialNull()) {
            return lastNode;
        }

        final String reference = ref.getUri();
        if (!reference.startsWith("#")) {
            throw new IllegalArgumentException(
                    "external references are currently not supported: "
                    + reference);
        }
        final String localReference = reference.substring(1);
        final Rule rule = grammar.getRule(localReference);
        GrammarNode referencedNode = parse(lastNode, rule);
        lastNode.addNext(referencedNode);
        return referencedNode;
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
        final String[] texts = value.split(" ");
        if (texts.length == 1) {
            return new TokenGrammarNode(value);
        }

        final GrammarNode start =
            new EmptyGrammarNode(GrammarNodeType.SEQUENCE_START);
        GrammarNode addedNode = start;
        for (String current : texts) {
            final TokenGrammarNode node = new TokenGrammarNode(current);
            addedNode.addNext(node);
            addedNode = node;
        }
       final GrammarNode end =
           new EmptyGrammarNode(GrammarNodeType.SEQUENCE_END);
       addedNode.addNext(end);
       return new GrammarGraph(start, end);
    }

    /**
     * Convenience method to convert a {@link Text} into a {@link GrammarNode}.
     * @param lastNode the last parsed node.
     * @param tag the current tag node
     * @return the grammar node, <code>lastNode</code> if the node can be
     * ignored.
     */
    private GrammarNode parse(final GrammarNode lastNode, final Tag tag) {
        final String value = tag.getTextContent().trim();
        if (value.length() == 0) {
            // Ignore whitespace.
            return lastNode;
        }
        return new TagGrammarNode(value);
    }
}
