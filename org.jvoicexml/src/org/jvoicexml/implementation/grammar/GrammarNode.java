package org.jvoicexml.implementation.grammar;

import java.util.Collection;

/**
 * Represents a node of a graph.
 *
 * @author David José Rodrigues
 * @author Dirk Schnelle-Walka
 */
public final class GrammarNode {

    /** Represent an end alternative node. */
    public static final int END_ALTERNATIVE = 1;
    /** Represent an end count node. */
    public static final int END_COUNT = 2;
    /** Represent an end reference node. */
    public static final int END_REFERENCE = 3;
    /** Represent an end sequence node. */
    public static final int END_SEQUENCE = 4;
    /** Represent a start alternative node. */
    public static final int START_ALTERNATIVE = 5;
    /** Represent a start count node. */
    public static final int START_COUNT = 6;
    /** Represent a start reference node. */
    public static final int START_REFERENCE = 7;
    /** Represent a start sequence node. */
    public static final int START_SEQUENCE = 8;
    /** Represent a tag node. */
    public static final int TAG = 9;
    /** Represent a token node. */
    public static final int TOKEN = 10;

    /** <code>true</code> if this node is a final node of the graph. */
    private boolean isFinal;

    /** the arcs to the successors nodes. */
    private final Collection<GrammarNode> destinationNodes;

    /** the type of this node. */
    private final int type;

    /** the rule component associated with this node. */
    private String token;

    /**
     * Creates a grammar node without a rule component associated.
     * @param nodeType the node type
     * @param grammarToken the token of the grammar
     */
    protected GrammarNode(final int nodeType,
                          final String grammarToken) {
        destinationNodes = new java.util.ArrayList<GrammarNode>();
        type = nodeType;
        token = grammarToken;
    }

    /**
     * Create a grammar node, without a rule component associated.
     * @param nodeType the node type
     */
    protected GrammarNode(final int nodeType) {
        this(nodeType, null);
    }

    /**
     * Marks this node as a final node.
     * @param finalNode <code>true</code> if the node is final
     */
    public void setFinalNode(final boolean finalNode) {
        isFinal = finalNode;
    }

    /**
     * Checks if this node is a final node.
     * @return <code>true</code> if this is a final node.
     */
    public boolean isFinalNode() {
        return isFinal;
    }

    /**
     * Adds an arc, from this node to the destinationNode.
     * @param destinationNode the destination node
     */
    public void addArc(final GrammarNode destinationNode) {
        destinationNodes.add(destinationNode);
    }

    /**
     * Gets the node type.
     * @return the node type.
     */
    public int getNodeType() {
        return type;
    }

    /**
     * Gets the destination nodes.
     * @return Destination nodes.
     */
    public Collection<GrammarNode> getArcList() {
        return destinationNodes;
    }

    /**
     * Gets the rule component associated with this node.
     * @return RuleComponent
     */
    public String getToken() {
        return token;
    }
}
