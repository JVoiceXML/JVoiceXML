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

    /** The type of the grammar node. */
    private final GrammarNodeType type;

    /**
     * Create a grammar node, without a rule component associated.
     * @param nodeType type of the grammar node
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
    public final void addNext(final GrammarNode destinationNode) {
        destinationNodes.add(destinationNode);
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<GrammarNode> getNextNodes() {
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

    /**
     * {@inheritDoc}
     */
    public final GrammarNodeType getType() {
        return type;
    }
}
