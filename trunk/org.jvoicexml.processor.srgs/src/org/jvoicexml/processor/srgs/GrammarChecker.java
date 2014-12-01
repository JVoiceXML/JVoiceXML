/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleParse;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleTag;
import org.jvoicexml.processor.srgs.grammar.RuleToken;

/**
 * This class provides a means to perform evaluations on a parsed grammar.
 * 
 * @author Dirk Schnelle-Walka
 * @author Brian Pendell
 * @version $Revision$
 * @since 0.7
 */
public final class GrammarChecker {
    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(GrammarChecker.class);

    private final GrammarManager manager;

    /** The graph to analyze. */
    private final GrammarGraph graph;

    /** Matched tokens. */
    private final Stack<GrammarNode> matchedTokens;

    /**
     * Constructs a new object.
     * 
     * @param grammarManager
     *            the grammar manager.
     * @param grammarGraph
     *            the graph to analyze.
     */
    public GrammarChecker(final GrammarManager grammarManager,
            final GrammarGraph grammarGraph) {
        manager = grammarManager;
        matchedTokens = new Stack<GrammarNode>();
        graph = grammarGraph;
    }

    /**
     * Checks if the given tokens can be represented using the given graph.
     * 
     * @param tokens
     *            the tokens
     * @return <code>true</code> if the tokens are valid.
     */
    public boolean isValid(final String[] tokens) {
        matchedTokens.clear();
        final GrammarNode start = graph.getStartNode();
        boolean retval = isValid(start, tokens);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isValid matched tokens size = "
                    + matchedTokens.size());
            LOGGER.trace("isValid tokens length = " + tokens.length);
            LOGGER.trace("isValid retval = " + retval);
            for (int i = 0; i < tokens.length; i++) {
                LOGGER.trace("isValid token at position " + i + ":" + tokens[i]);
            }
        }
        return retval && (matchedTokens.size() == tokens.length);
    }

    /**
     * Checks if the given tokens can be represented using the given graph.
     * 
     * @param grammar
     *            the grammar to check
     * @param input
     *            the tokens
     * @return <code>true</code> if the tokens are valid.
     * @throws GrammarException
     */
    public RuleComponent isValid(final RuleGrammar grammar, final String[] input)
            throws GrammarException {
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        final RuleComponent component = rule.getRuleComponent();
        final RuleWalker walker = new RuleWalker();
        walker.setGrammar(grammar);
        final RuleWalker validWalker = isValid(component, input, walker);
        if (validWalker == null) {
            return null;
        }
        if (validWalker.getPosition() != input.length) {
            return null;
        }
        return validWalker.getComponent();
    }

    private RuleWalker isValid(final RuleComponent component,
            final String[] input, final RuleWalker walker)
            throws GrammarException {
        if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            return isValid(sequence, input, walker);
        } else if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            return isValid(alternatives, input, walker);
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            return isValid(count, input, walker);
        } else if (component instanceof RuleToken) {
            final RuleToken token = (RuleToken) component;
            return isValid(token, input, walker);
        } else if (component instanceof RuleTag) {
            final RuleTag tag = (RuleTag) component;
            return isValid(tag, input, walker);
        } else if (component instanceof RuleReference) {
            final RuleReference reference = (RuleReference) component;
            return isValid(reference, input, walker);
        } else {
            return null;
        }
    }

    private RuleWalker isValid(final RuleReference reference,
            final String[] input, final RuleWalker walker)
            throws GrammarException {
        final RuleGrammar grammar = walker.getGrammar();
        final RuleReference resolvedReference = grammar.resolve(reference);
        final Rule rule = manager.resolve(resolvedReference);
        if (rule == null) {
            return null;
        }
        // TODO need to set the new grammar if it changed
        final RuleComponent component = rule.getRuleComponent();
        final RuleWalker validWalker = isValid(component, input, walker);
        if (validWalker == null) {
            return null;
        }
        final RuleComponent validComponent = validWalker.getComponent();
        final RuleParse parse = new RuleParse(resolvedReference, validComponent);
        return new RuleWalker(validWalker, parse);
    }

    private RuleWalker isValid(final RuleAlternatives alternatives,
            final String[] input, final RuleWalker walker)
            throws GrammarException {
        final RuleComponent[] components = alternatives.getRuleComponents();
        for (RuleComponent current : components) {
            final RuleWalker validWalker = isValid(current, input, walker);
            if (validWalker != null) {
                final RuleComponent[] validComponent = new RuleComponent[] { validWalker
                        .getComponent() };
                final RuleAlternatives validAlternative = new RuleAlternatives(
                        validComponent);
                return new RuleWalker(validWalker, validAlternative);
            }
        }
        return null;
    }

    private RuleWalker isValid(final RuleSequence sequence,
            final String[] input, RuleWalker walker) throws GrammarException {
        final RuleComponent[] components = sequence.getRuleComponents();
        final List<RuleComponent> validComponents = new java.util.ArrayList<RuleComponent>();
        for (RuleComponent current : components) {
            walker = isValid(current, input, walker);
            if (walker == null) {
                return null;
            }
            final RuleComponent validComponent = walker.getComponent();
            if (validComponent != null) {
                validComponents.add(validComponent);
            }
        }

        final RuleWalker validWalker = new RuleWalker(walker);
        final int size = validComponents.size();
        if (size == 1) {
            final RuleComponent validComponent = validComponents.get(0);
            validWalker.setComponent(validComponent);
        } else {
            final RuleComponent[] value = new RuleComponent[validComponents
                    .size()];
            validComponents.toArray(value);
            final RuleSequence validSequence = new RuleSequence(value);
            validWalker.setComponent(validSequence);
        }
        return validWalker;
    }

    private RuleWalker isValid(final RuleCount count, final String[] input,
            RuleWalker walker) throws GrammarException {
        final RuleComponent component = count.getRuleComponent();
        final int min = count.getRepeatMin();
        int max = count.getRepeatMax();
        final int pos = walker.getPosition();
        if (max > input.length - pos) {
            max = input.length - pos;
        }
        final List<RuleComponent> validComponents = new java.util.ArrayList<RuleComponent>();
        int repeat = 0;
        RuleWalker intermediateValidWalker = new RuleWalker(walker);
        while (repeat < min) {
            final RuleWalker current = isValid(component, input,
                    intermediateValidWalker);
            if (current == null) {
                return null;
            } else {
                final RuleComponent validComponent = current.getComponent();
                if (validComponent != null) {
                    validComponents.add(validComponent);
                }
                intermediateValidWalker = current;
            }
            ++repeat;
        }
        int validRepeat = repeat;
        while (repeat < max) {
            ++repeat;
            final RuleWalker current = isValid(component, input,
                    intermediateValidWalker);
            if (current == null) {
                break;
            } else {
                final RuleComponent validComponent = current.getComponent();
                if (validComponent != null) {
                    validComponents.add(validComponent);
                }
                validRepeat = repeat;
                intermediateValidWalker = current;
            }
        }
        final RuleWalker validWalker = new RuleWalker(intermediateValidWalker);
        final RuleComponent[] sequenceComponents = new RuleComponent[validComponents
                .size()];
        validComponents.toArray(sequenceComponents);
        final RuleSequence sequence = new RuleSequence(sequenceComponents);
        final RuleCount validCount = new RuleCount(sequence, validRepeat,
                validRepeat);
        validWalker.setComponent(validCount);
        return validWalker;
    }

    private RuleWalker isValid(final RuleToken token, final String[] input,
            final RuleWalker walker) {
        final int pos = walker.getPosition();
        final String text = token.getText();
        final String[] tokens = text.split(" ");
        int i = 0;
        for (String current : tokens) {
            if (pos + i >= input.length) {
                return null;
            }
            final String currentInput = input[pos + i];
            if (!current.equalsIgnoreCase(currentInput)) {
                return null;
            }
            ++i;
        }
        final RuleWalker validWalker = new RuleWalker(walker);
        validWalker.setComponent(token);
        validWalker.setPosition(pos + i);
        return validWalker;
    }

    private RuleWalker isValid(final RuleTag tag, final String[] input,
            final RuleWalker walker) {
        final RuleWalker validWalker = new RuleWalker(walker);
        validWalker.setComponent(tag);
        return validWalker;
    }

    /**
     * Retrieves the result of the grammar check process. This may differ from
     * the parsed tokens of the original utterance.
     * <p>
     * A call to this method is only valid after a call to
     * {@link #isValid(String[])} until the next validation check.
     * </p>
     * <p>
     * <b>NOTE:</b> This is just a first attempt to go into the direction of
     * semantic interpretation and may change.
     * </p>
     * 
     * @return interpretation result
     */
    public String[] getInterpretation() {
        Collection<String> result = new java.util.ArrayList<String>();
        for (GrammarNode node : matchedTokens) {
            final GrammarNodeType type = node.getType();

            if (type == GrammarNodeType.TOKEN) {
                final TokenGrammarNode tokenNode = (TokenGrammarNode) node;

                Collection<GrammarNode> nextNodes = tokenNode.getNextNodes();
                for (int i = 0; i < nextNodes.size(); i++) {
                    GrammarNode nextNode = (GrammarNode) nextNodes.toArray()[i];

                    if (nextNode.getType() == GrammarNodeType.TAG) {
                        final TagGrammarNode tagNode = (TagGrammarNode) nextNode;
                        final String tag = tagNode.getTag();
                        result.add(tag);
                    }

                    if (nextNode.getType() == GrammarNodeType.SEQUENCE_END) {
                        nextNode = (GrammarNode) nextNode.getNextNodes()
                                .toArray()[0];
                        if (nextNode.getType() == GrammarNodeType.TAG) {
                            final TagGrammarNode tagNode = (TagGrammarNode) nextNode;
                            final String tag = tagNode.getTag();
                            result.add(tag);
                        }
                    }
                }
            }
        }

        String[] finalResult = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            finalResult[i] = (String) result.toArray()[i];
        }

        return finalResult;

    }

    /**
     * Prints out information about a node, including the type of node and tag
     * or token information, if available.
     * 
     * @param node
     *            The node to be described.
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
     * 
     * @param node
     *            the node
     * @param tokens
     *            the tokens to analyze.
     * @return <code>true</code> if the node is on the path.
     */
    private boolean isValid(final GrammarNode node, final String[] tokens) {
        return isValid(node, tokens, tokens.length, false);
    }

    /**
     * Checks if the given node is on the path. This is an expanded version
     * which handles accounting differently if the default version which assumes
     * that the node has no repetitions and that it is looking to match as many
     * tokens as existed as input to the grammar.
     * 
     * @param node
     *            the node
     * @param tokens
     *            the tokens to analyze.
     * @param targetTokenCount
     *            This is the number of tokens that should be matched to return
     *            true. While this is typically equal to the number of input
     *            tokens, it can be different if the grammar contains a
     *            <code>&lt;,repeat=&gt;</code> tag.
     * @param isRepetition
     *            boolean indicating whether we are processing a
     *            <code>&lt;repeat&gt;</code> tag. Accounting is handled
     *            differently in that special case.
     * 
     * @return <code>true</code> if the node is on the path.
     */

    private boolean isValid(final GrammarNode node, final String[] tokens,
            final int targetTokenCount, final boolean isRepetition) {
        if (LOGGER.isTraceEnabled()) {
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
                        + validReps + "mininum reps = "
                        + currentGraph.getMinRepeat() + "maximum reps = "
                        + currentGraph.getMaxRepeat());
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
                    || ((matchedTokens.size() >= targetTokenCount) && !isRepetition)) {
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
                        LOGGER.trace("isValid Token Token no match return false");
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
     * 
     * @param node
     *            the node to inspect.
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
