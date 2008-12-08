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

    /** Represent an end alternative node. */
    int END_ALTERNATIVE = 1;
    /** Represent an end count node. */
    int END_COUNT = 2;
    /** Represent an end reference node. */
    int END_REFERENCE = 3;
    /** Represent an end sequence node. */
    int END_SEQUENCE = 4;
    /** Represent a start alternative node. */
    int START_ALTERNATIVE = 5;
    /** Represent a start count node. */
    int START_COUNT = 6;
    /** Represent a start reference node. */
    int START_REFERENCE = 7;
    /** Represent a start sequence node. */
    int START_SEQUENCE = 8;
    /** Represent a tag node. */
    int TAG = 9;
    /** Represent a token node. */
    int TOKEN = 10;

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
     * Gets the node type.
     * @return the node type.
     */
    int getNodeType();

    /**
     * Gets the destination nodes.
     * @return Destination nodes.
     */
    Collection<GrammarNode> getArcList();
}
