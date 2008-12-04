package org.jvoicexml.implementation.grammar;

/**
 * Represents a graph, or a sub-graph.
 * It only contains a start node and an end node.
 *
 * @author David José Rodrigues
 * @authr Dirk Schnelle-Walka
 */

public final class GrammarGraph {
    /** the start node of the graph. */
    private GrammarNode startNode;

    /** the end node of the graph. */
    private GrammarNode endNode;

    /**
     * Creates a grammar graph with the given nodes.
     *
     * @param start the staring node of the graph
     * @param end the ending node of the graph
     */
    public GrammarGraph(final GrammarNode start, final GrammarNode end) {
        startNode = start;
        endNode = end;
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
}
