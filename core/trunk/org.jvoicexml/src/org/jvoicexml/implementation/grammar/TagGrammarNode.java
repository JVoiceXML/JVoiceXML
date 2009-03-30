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
public final class TagGrammarNode extends EmptyGrammarNode {
    /** the tag associated with this node. */
    private String tag;

    /**
     * Creates a grammar node without a rule component associated.
     * @param grammarTag the tag of the grammar
     */
    protected TagGrammarNode(final String grammarTag) {
        super(GrammarNodeType.TAG);
        tag = grammarTag;
    }

    /**
     * Gets the tag associated with this node.
     * @return token
     */
    public String getTag() {
        return tag;
    }
}
