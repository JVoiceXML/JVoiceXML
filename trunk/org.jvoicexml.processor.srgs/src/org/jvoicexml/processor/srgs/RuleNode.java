/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/RuleNode.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.processor.srgs;


/**
 * Implementation independent representation of a grammar.
 *
 * <p>
 * Represents a graph, or a sub-graph.
 * It only contains a start node and an end node.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $Revision: 2129 $
 */
public final class RuleNode extends GrammarGraph {
    /** The rule id. */
    private final String id;

    /**
     * Creates a rule graph with the given nodes.
     *
     * @param ruleId id of the rule.
     * @param start the staring node of the graph
     * @param end the ending node of the graph
     */
    public RuleNode(final String ruleId, final GrammarNode start,
            final GrammarNode end) {
        super(GrammarNodeType.RULE, start, end);
        id = ruleId;
    }

    /**
     * Retrieves the i of the rule.
     * @return id of the rule.
     */
    public String getId() {
        return id;
    }
}
