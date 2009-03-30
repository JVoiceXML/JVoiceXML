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


/**
 * Represents a node of a graph with an associated token.
 *
 * @author David José Rodrigues
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision$
 */
public final class TokenGrammarNode extends EmptyGrammarNode {
    /** the token associated with this node. */
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
