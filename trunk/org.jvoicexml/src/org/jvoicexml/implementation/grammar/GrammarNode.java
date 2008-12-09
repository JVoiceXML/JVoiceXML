package org.jvoicexml.implementation.grammar;

import java.util.Collection;

/**
 * Represents a node of a graph.
 *
 * @author David José Rodrigues
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public interface GrammarNode {
    /**
     * Marks this node as a final node.
     * @param finalNode <code>true</code> if the node is final
     */
    void setFinalNode(final boolean finalNode);

    /**
     * Checks if this node is a final node.
     * @return <code>true</code> if this is a final node.
     */
    boolean isFinalNode();

    /**
     * Adds an arc, from this node to the destinationNode.
     * @param destinationNode the destination node
     */
    void addArc(final GrammarNode destinationNode);

    /**
     * Gets the destination nodes.
     * @return Destination nodes.
     */
    Collection<GrammarNode> getArcList();

    /**
     * Sets the minimal number of repetitions.
     * @param min minimal number of repetitions
     */
    void setMinRepeat(final int min);

    /**
     * Retrieves the minimal number of repetitions, default value is
     * <code>1</code>.
     * @return minimal number of repetitions.
     */
    int getMinRepeat();

    /**
     * Sets the maximal number of repetitions.
     * @param max maximal number of repetitions
     */
    void setMaxRepeat(final int max);

    /**
     * Retrieves the maximal number of repetitions, default value is
     * <code>1</code>.
     * @return maximal number of repetitions.
     */
    int getMaxRepeat();
}
