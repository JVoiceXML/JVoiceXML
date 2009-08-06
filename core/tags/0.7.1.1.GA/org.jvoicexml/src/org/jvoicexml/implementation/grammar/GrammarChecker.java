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
import java.util.Stack;

import org.jvoicexml.SemanticInterpretation;

/**
 * This class provides a means to perform evaluations on a parsed grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class GrammarChecker {
    /** The graph to analyze. */
    private final GrammarGraph graph;

    /** Matched tokens. */
    private final Stack<GrammarNode> matchedTokens;

    /**
     * Constructs a new object.
     * @param grammarGraph the graph to analyze.
     */
    public GrammarChecker(final GrammarGraph grammarGraph) {
        matchedTokens = new Stack<GrammarNode>();
        graph = grammarGraph;
    }
    /**
     * Checks if the given tokens can be represented using the given
     * graph.
     * @param tokens the tokens
     * @return <code>true</code> if the tokens are valid.
     */
    public boolean isValid(final String[] tokens) {
        matchedTokens.clear();
        final GrammarNode start = graph.getStartNode();
        return isValid(start, tokens)
            && (matchedTokens.size() == tokens.length);
    }

    /**
     * Retrieves the result of the grammar check process. This may differ from
     * the parsed tokens of the original utterance.
     * <p>
     * A call to this method is only valid after a call to
     * {@link #isValid(String[])} until the next validation check.
     * </p>
     * @return interpreteration result
     * TODO This is just a first attempt to go into the direction of semantic
     * interpretation and may change.
     */
    public SemanticInterpretation getInterpretation() {
        Collection<String> result = new java.util.ArrayList<String>();
        for (GrammarNode node : matchedTokens) {
            final GrammarNodeType type = node.getType();
            if (type == GrammarNodeType.TOKEN) {
                final TokenGrammarNode tokenNode = (TokenGrammarNode) node;
                final String token = tokenNode.getToken();
                result.add(token);
            }
        }
        return null;
    }

    /**
     * Checks if the given node is on the path.
     * @param node the node
     * @param tokens the tokens to analyze.
     * @return <code>true</code> if the node is on the path.
     */
    private boolean isValid(final GrammarNode node, final String[] tokens) {
        final GrammarNodeType type = node.getType();
        if ((type == GrammarNodeType.GRAPH) || (type == GrammarNodeType.RULE)) {
            final GrammarGraph currentGraph = (GrammarGraph) node;
            if (currentGraph.getMinRepeat() == 0) {
                final GrammarNode end = currentGraph.getEndNode();
                if (isValid(end, tokens)) {
                    return true;
                }
            }
            final GrammarNode start = currentGraph.getStartNode();
            return isValid(start, tokens);
        }

        boolean pushedNode = false;
        if (type == GrammarNodeType.TOKEN) {
            if (matchedTokens.size() >= tokens.length) {
                return false;
            }
            final TokenGrammarNode tokenNode = (TokenGrammarNode) node;
            final String token = tokenNode.getToken();
            if (token.equals(tokens[matchedTokens.size()])) {
                matchedTokens.push(node);
                pushedNode = true;
                if (matchedTokens.size() >= tokens.length) {
                    return isFinalNode(node);
                }
            } else {
                if (tokenNode.getMinRepeat() > 0) {
                    return false;
                }
            }
        }

        for (GrammarNode destination : node.getNextNodes()) {
            if (isValid(destination, tokens)) {
                return true;
            }
        }
        if (pushedNode && (matchedTokens.size() > 0)) {
            matchedTokens.pop();
        }
        return false;
    }

    /**
     * Checks if the given node is a final node. This is the case if either the
     * given node is a final node itself or if there is a way through pure
     * {@link EmptyGrammarNode}s to a final node.
     * @param node the node to inspect.
     * @return <code>true</code> if the given node is a final node.
     */
    private boolean isFinalNode(final GrammarNode node) {
        if (node.isFinalNode()) {
            return true;
        }
        for (GrammarNode destination : node.getNextNodes()) {
            final GrammarNodeType type = destination.getType();
            if (type == GrammarNodeType.GRAPH) {
                final GrammarGraph currentGraph = (GrammarGraph) destination;
                final GrammarNode start = currentGraph.getStartNode();
                if (isFinalNode(start)) {
                    return true;
                }
            } else if (type == GrammarNodeType.TOKEN) {
                return false;
            } else {
                if (isFinalNode(destination)) {
                    return true;
                }
            }
        }
        return false;
    }
}
