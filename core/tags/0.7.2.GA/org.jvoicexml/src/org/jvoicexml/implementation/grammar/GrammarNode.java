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
 * Represents a node of a graph.
 *
 * @author David Jos&eacute; Rodrigues
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
    void addNext(final GrammarNode destinationNode);

    /**
     * Gets the destination nodes.
     * @return Destination nodes.
     */
    Collection<GrammarNode> getNextNodes();

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

    /**
     * Retrieves the type of the grammar node.
     * @return type of the grammar node.
     */
    GrammarNodeType getType();
}
