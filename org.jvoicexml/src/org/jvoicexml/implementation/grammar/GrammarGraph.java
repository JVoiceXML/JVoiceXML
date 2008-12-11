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
    public void addArc(final GrammarNode destinationNode) {
        endNode.addArc(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarNode> getArcList() {
        return endNode.getArcList();
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

    public GrammarNodeType getType() {
        return type;
    }
}
