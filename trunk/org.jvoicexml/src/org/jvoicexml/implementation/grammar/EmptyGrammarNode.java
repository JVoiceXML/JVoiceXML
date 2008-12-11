package org.jvoicexml.implementation.grammar;

import java.util.Collection;

/**
 * Represents an empty node of a graph.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public class EmptyGrammarNode implements GrammarNode {
    /** <code>true</code> if this node is a final node of the graph. */
    private boolean isFinal;

    /** the arcs to the successors nodes. */
    private final Collection<GrammarNode> destinationNodes;

    /** Minimal number of repetitions. */
    private int minRepetitions;

    /** Maximal number of repetitions. */
    private int maxRepetitions;

    private final GrammarNodeType type;

    /**
     * Create a grammar node, without a rule component associated.
     */
    protected EmptyGrammarNode(final GrammarNodeType nodeType) {
        destinationNodes = new java.util.ArrayList<GrammarNode>();
        minRepetitions = 1;
        maxRepetitions = 1;
        type = nodeType;
    }

    /**
     * {@inheritDoc}
     */
    public final void setFinalNode(final boolean finalNode) {
        isFinal = finalNode;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isFinalNode() {
        return isFinal;
    }

    /**
     * {@inheritDoc}
     */
    public final void addArc(final GrammarNode destinationNode) {
        destinationNodes.add(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<GrammarNode> getArcList() {
        return destinationNodes;
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

    public GrammarNodeType getType() {
        return type;
    }
}
