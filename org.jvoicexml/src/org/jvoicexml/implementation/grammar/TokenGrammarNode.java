package org.jvoicexml.implementation.grammar;

import java.util.Collection;

/**
 * Represents a node of a graph with an associated token.
 *
 * @author David José Rodrigues
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class TokenGrammarNode implements GrammarNode {
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
    protected TokenGrammarNode(final int nodeType,
                          final String grammarToken) {
        destinationNodes = new java.util.ArrayList<GrammarNode>();
        type = nodeType;
        token = grammarToken;
    }

    /**
     * Create a grammar node, without a rule component associated.
     * @param nodeType the node type
     */
    protected TokenGrammarNode(final int nodeType) {
        this(nodeType, null);
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

    /**
     * Gets the token associated with this node.
     * @return token
     */
    public String getToken() {
        return token;
    }
}
