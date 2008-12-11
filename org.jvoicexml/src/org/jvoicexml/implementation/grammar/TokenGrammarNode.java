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
public final class TokenGrammarNode extends EmptyGrammarNode {
    /** the rule component associated with this node. */
    private String token;

    /**
     * Creates a grammar node without a rule component associated.
     * @param grammarToken the token of the grammar
     */
    protected TokenGrammarNode(final String grammarToken) {
        super(GrammarNodeType.TOKEN);
        token = grammarToken;
    }

    /**
     * Gets the token associated with this node.
     * @return token
     */
    public String getToken() {
        return token;
    }
}
