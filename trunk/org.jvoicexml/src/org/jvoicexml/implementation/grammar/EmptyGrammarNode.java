package org.jvoicexml.implementation.grammar;

import java.util.Collection;

/**
 * Represents an empty node of a graph.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class EmptyGrammarNode implements GrammarNode {
    /** <code>true</code> if this node is a final node of the graph. */
    private boolean isFinal;

    /** the arcs to the successors nodes. */
    private final Collection<GrammarNode> destinationNodes;

    /** the type of this node. */
    private final int type;

    /**
     * Create a grammar node, without a rule component associated.
     * @param nodeType the node type
     */
    protected EmptyGrammarNode(final int nodeType) {
        destinationNodes = new java.util.ArrayList<GrammarNode>();
        type = nodeType;
    }

    /**
     * {@inheritDoc}
     */
    public void setFinalNode(final boolean finalNode) {
        isFinal = finalNode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFinalNode() {
        return isFinal;
    }

    /**
     * {@inheritDoc}
     */
    public void addArc(final GrammarNode destinationNode) {
        destinationNodes.add(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public int getNodeType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarNode> getArcList() {
        return destinationNodes;
    }
}
