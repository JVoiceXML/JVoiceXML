/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/GrammarGraph.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.processor.srgs;

import java.util.Collection;

/**
 * Implementation independent representation of a grammar.
 *
 * <p>
 * Represents a graph, or a sub-graph.
 * It only contains a start node and an end node.
 * </p>
 *
 * @author David Jos&eacute; Rodrigues
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision: 2129 $
 */
public class GrammarGraph implements GrammarNode {
    /** Minimal number of repetitions. */
    private int minRepetitions;

    /** Maximal number of repetitions. */
    private int maxRepetitions;

    /** the start node of the graph. */
    private final GrammarNode startNode;

    /** the end node of the graph. */
    private GrammarNode endNode;

    /** Type of the grammar node. */
    private final GrammarNodeType type;

    /**
     * Creates a grammar graph with the given nodes.
     *
     * @param start the staring node of the graph
     * @param end the ending node of the graph
     */
    public GrammarGraph(final GrammarNode start, final GrammarNode end) {
        type = GrammarNodeType.GRAPH;
        startNode = start;
        endNode = end;
        minRepetitions = 1;
        maxRepetitions = 1;
    }

    /**
     * Creates a grammar graph with the given nodes.
     *
     * @param grammarType type of the grammar graph
     * @param start the staring node of the graph
     * @param end the ending node of the graph
     */
    public GrammarGraph(final GrammarNodeType grammarType,
            final GrammarNode start, final GrammarNode end) {
        type = grammarType;
        startNode = start;
        endNode = end;
        minRepetitions = 1;
        maxRepetitions = 1;
    }

    /**
     * Gets the starting node.
     * @return the starting node for the graph
     */
    public final GrammarNode getStartNode() {
        return startNode;
    }


    /**
     * Gets the ending node.
     * @return the ending node for the graph
     */
    public final GrammarNode getEndNode() {
        return endNode;
    }

    /**
     * Sets the ending node.
     * @param node GrammarNode the new end node
     */
    public final void setEndNode(final GrammarNode node) {
        endNode = node;
    }

    /**
     * {@inheritDoc}
     */
    public final void addNext(final GrammarNode destinationNode) {
        endNode.addNext(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<GrammarNode> getNextNodes() {
        return endNode.getNextNodes();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isFinalNode() {
        return endNode.isFinalNode();
    }

    /**
     * {@inheritDoc}
     */
    public final void setFinalNode(final boolean finalNode) {
        endNode.setFinalNode(finalNode);
    }

    /**
     * {@inheritDoc}
     */
    public final int getMinRepeat() {
        return minRepetitions;
    }

    /**
     * {@inheritDoc}
     */
    public final void setMinRepeat(final int min) {
        minRepetitions = min;
    }

    /**
     * {@inheritDoc}
     */
    public final void setMaxRepeat(final int max) {
        maxRepetitions = max;
    }

    /**
     * {@inheritDoc}
     */
    public final int getMaxRepeat() {
        return maxRepetitions;
    }

    /**
     * {@inheritDoc}
     */
    public final GrammarNodeType getType() {
        return type;
    }
}
