/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/GrammarChecker.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
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
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * This class provides a means to perform evaluations on a parsed grammar.
 * @author Dirk Schnelle-Walka
 * @author Brian Pendell
 * @version $Revision: 2129 $
 * @since 0.7
 */
public final class GrammarChecker {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(GrammarChecker.class);

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
        boolean retval; 
        matchedTokens.clear();
        final GrammarNode start = graph.getStartNode();
        retval = isValid(start, tokens);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isValid matched tokens size = "
                    + matchedTokens.size());
            LOGGER.trace("isValid tokens length = " + tokens.length);
            LOGGER.trace("isValid retval = " + retval);  
            for (int i = 0; i < tokens.length; i++) {
                LOGGER.trace("isValid token at position " + i + ":"
                        + tokens[i]);
            }
        }
        return retval
            && (matchedTokens.size() == tokens.length);
    }

    /**
     * Retrieves the result of the grammar check process. This may differ from
     * the parsed tokens of the original utterance.
     * <p>
     * A call to this method is only valid after a call to
     * {@link #isValid(String[])} until the next validation check.
     * </p>
     * <p>
     * <b>NOTE:</b>
     * This is just a first attempt to go into the direction of semantic
     * interpretation and may change.
     * </p>
     * @return interpreteration result
     */
    public String[] getInterpretation() {
        Collection<String> result = new java.util.ArrayList<String>();
        for (GrammarNode node : matchedTokens) {
            final GrammarNodeType type = node.getType();

            if (type == GrammarNodeType.TOKEN) {
                final TokenGrammarNode tokenNode = (TokenGrammarNode) node;
                 
                Collection<GrammarNode> nextNodes = tokenNode.getNextNodes();
                for (int i = 0; i < nextNodes.size(); i++) {
                    GrammarNode  nextNode =
                        (GrammarNode) nextNodes.toArray()[i];
   
                  if (nextNode.getType() == GrammarNodeType.TAG) {
                    final TagGrammarNode tagNode = (TagGrammarNode) nextNode;
                    final String tag = tagNode.getTag();
                    result.add(tag);
                  }

                  if (nextNode.getType() == GrammarNodeType.SEQUENCE_END) {
                    nextNode = (GrammarNode) nextNode.getNextNodes()
                        .toArray()[0];
                    if (nextNode.getType() == GrammarNodeType.TAG) {
                        final TagGrammarNode tagNode = 
                             (TagGrammarNode) nextNode;
                        final String tag = tagNode.getTag();
                        result.add(tag);
                    }
                  } 
               }
            }
        }

        String [] finalResult = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            finalResult[i] = (String) result.toArray()[i];
        }

        return finalResult;
    
    }
    
    /**
     * Prints out information about a node, including the type 
     * of node and tag or token information, if available.
     * @param node The node to be described. 
     */
    private void printNode(final GrammarNode node) {
        String typeString = "UNDEFINED";
        String additionalString = "";
        final GrammarNodeType currentType = node.getType();

        if (currentType == GrammarNodeType.START) {
            typeString = "START";
        } else if (currentType == GrammarNodeType.ALTERNATIVE_START) {
            typeString = "ALTERNATIVE_START";
        } else if (currentType == GrammarNodeType.ALTERNATIVE_END) {
            typeString = "ALTERNATIVE_END";
        } else if (currentType == GrammarNodeType.SEQUENCE_START) {
            typeString = "SEQUENCE_START";
        } else if (currentType == GrammarNodeType.SEQUENCE_END) {
            typeString = "SEQUENCE_END";
        } else if (currentType == GrammarNodeType.TOKEN) {
            typeString = "TOKEN";
            TokenGrammarNode tokenNode = (TokenGrammarNode) node;
            additionalString = "token body = '" + tokenNode.getToken() + "'";
        } else if (currentType == GrammarNodeType.TAG) {
            typeString = "TAG";
            TagGrammarNode tagNode = (TagGrammarNode) node;
            additionalString = "tag body = '" + tagNode.getTag() + "'";
        } else if (currentType == GrammarNodeType.GRAPH) {
            typeString = "GRAPH";
        } else if (currentType == GrammarNodeType.RULE) {
            typeString = "RULE";
            RuleNode ruleNode = (RuleNode) node;
            additionalString = " ID = " + ruleNode.getId();
        }

        LOGGER.debug("Node Type:" + typeString + " min repetitions: "
                + node.getMinRepeat() + ", max repetitions: "
                + node.getMaxRepeat() + " " + additionalString);
    }

    /**
     * Checks if the given node is on the path. This is a default version which
     * assumes that the node has no repetitions and that it is looking to match
     * as many tokens as existed as input to the grammar. 
     * @param node the node
     * @param tokens the tokens to analyze.
     * @return <code>true</code> if the node is on the path.
     */
    private boolean isValid(final GrammarNode node, final String[] tokens) {
        return isValid(node, tokens, tokens.length, false);
    }

    /**
     * Checks if the given node is on the path. This is an expanded version
     * which handles accounting differently if the default version which
     * assumes that the node has no repetitions and that it is looking to match
     * as many tokens as existed as input to the grammar. 
     * @param node the node
     * @param tokens the tokens to analyze.
     * @param targetTokenCount  This is the number of tokens that should 
     *  be matched to return true. While this is typically equal to the number
     *  of input tokens, it can be different if the grammar contains a 
     *  <code>&lt;,repeat=&gt;</code> tag.  
     * @param isRepetition boolean indicating whether we are processing 
     * a <code>&lt;repeat&gt;</code> tag. Accounting is handled differently in
     * that special case.
     * 
     * @return <code>true</code> if the node is on the path.
     */
    
    private boolean isValid(final GrammarNode node, final String[] tokens,
            final int targetTokenCount, final boolean isRepetition) {
        if (LOGGER.isDebugEnabled()) {
            printNode(node);
        }
        int i = 0;
        for (GrammarNode destination : node.getNextNodes()) {
            i++;
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Child Node " + i + ":");
                printNode(destination);
            }
        }
        if (LOGGER.isTraceEnabled() && (i == 0)) {
            LOGGER.trace("Child Node: No child nodes");
        }
        final GrammarNodeType type = node.getType();
        if ((type == GrammarNodeType.GRAPH) || (type == GrammarNodeType.RULE)) {
            if (LOGGER.isTraceEnabled()) {
                if ((type == GrammarNodeType.GRAPH)) {
                    LOGGER.trace("isValid Entering Graph");
                }
                if ((type == GrammarNodeType.RULE)) {
                    LOGGER.trace("isValid Entering Rule");
                }
            }
            final GrammarGraph currentGraph = (GrammarGraph) node;
            if (currentGraph.getMinRepeat() == 0) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("isValid Graph zero repeats");
                }
                final GrammarNode end = currentGraph.getEndNode();
                if (isValid(end, tokens, targetTokenCount, isRepetition)) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("isValid Graph return true zero repeats");
                    }
                    return true;
                } else {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("isValid Graph return false zero repeats");
                    }
                }
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isValid Graph recursion >0 repeats");
            }
            final GrammarNode start = currentGraph.getStartNode();
            final int newTargetTokenCount = (int) Math.floor(targetTokenCount
                    / currentGraph.getMaxRepeat());
            int validReps = 0;
            boolean currentResponse = false;
            for (int j = 1; j <= currentGraph.getMaxRepeat(); j++) {
                currentResponse = isValid(start, tokens, newTargetTokenCount,
                        true);
                if (currentResponse) {
                    validReps++;
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("isValid complete repetition validReps = "
                        + validReps
                        + "mininum reps = " + currentGraph.getMinRepeat()
                        + "maximum reps = " + currentGraph.getMaxRepeat());
            }
            return validReps >= currentGraph.getMinRepeat()
                    && (validReps <= currentGraph.getMaxRepeat());
        }

        boolean pushedNode = false;
        if (type == GrammarNodeType.TOKEN) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isValid Token");
            }
            if ((matchedTokens.size() >= tokens.length)
                    || ((matchedTokens.size() >= targetTokenCount)
                    && !isRepetition)) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("isValid Token too many matched tokens return"
                            + "false");
                }
                return false;
            }            
            final TokenGrammarNode tokenNode = (TokenGrammarNode) node;
            final String token = tokenNode.getToken();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("isValid Token processing token '" + token
                        + "'equal to '" + tokens[matchedTokens.size()] + "'");
            }
            if (token.equalsIgnoreCase(tokens[matchedTokens.size()])) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("isValid Token Token match and push");
                }
                matchedTokens.push(node);
                pushedNode = true;
                if (matchedTokens.size() >= targetTokenCount) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("isValid Token Token match return final"
                                + " node " + isFinalNode(node));
                    }
                    return isFinalNode(node);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("isValid Token Token match not enough "
                                + "tokens have " + matchedTokens.size()
                                + " need " + targetTokenCount + "so continue.");
                    }
                }
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("isValid Token Token no match");
                }
                if (tokenNode.getMinRepeat() > 0) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace(
                                "isValid Token Token no match return false");
                    }
                    return false;
                }
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isValid Entering For Loop");
        }
        for (GrammarNode destination : node.getNextNodes()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isValid For Loop process, targetTokenCount = "
                        + targetTokenCount);
            }
            if (isValid(destination, tokens, targetTokenCount, isRepetition)) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("isValid For Loop");
                }
                return true;
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isValid Entering For Loop Complete");
        }
        if (pushedNode && (matchedTokens.size() > 0)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isValid Pop Token");
            }
            matchedTokens.pop();
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isValid final return false");
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
