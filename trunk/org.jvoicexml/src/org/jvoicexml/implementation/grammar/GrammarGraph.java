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

/**
 * Represents a graph, or a sub-graph.
 * It only contains a start node and an end node.
 *
 * @author David José Rodrigues
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class GrammarGraph implements GrammarNode {
    /** Minimal number of repetitions. */
    private int minRepetitions;

    /** Maximal number of repetitions. */
    private int maxRepetitions;

    /** the start node of the graph. */
    private GrammarNode startNode;

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
     * Gets the starting node.
     * @return the starting node for the graph
     */
    public GrammarNode getStartNode() {
        return startNode;
    }


    /**
     * Gets the ending node.
     * @return the ending node for the graph
     */
    public GrammarNode getEndNode() {
        return endNode;
    }

    /**
     * Sets the ending node.
     * @param node GrammarNode the new end node
     */
    public void setEndNode(final GrammarNode node) {
        endNode = node;
    }

    /**
     * {@inheritDoc}
     */
    public void addNext(final GrammarNode destinationNode) {
        endNode.addNext(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarNode> getNextNodes() {
        return endNode.getNextNodes();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFinalNode() {
        return endNode.isFinalNode();
    }

    /**
     * {@inheritDoc}
     */
    public void setFinalNode(final boolean finalNode) {
        endNode.setFinalNode(finalNode);
    }

    /**
     * {@inheritDoc}
     */
    public int getMinRepeat() {
        return minRepetitions;
    }

    /**
     * {@inheritDoc}
     */
    public void setMinRepeat(final int min) {
        minRepetitions = min;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxRepeat(final int max) {
        maxRepetitions = max;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxRepeat() {
        return maxRepetitions;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarNodeType getType() {
        return type;
    }
}
