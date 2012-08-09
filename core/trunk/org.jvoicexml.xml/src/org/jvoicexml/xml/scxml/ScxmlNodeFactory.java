/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.xml.scxml;

import java.util.Map;
import java.util.logging.Logger;

import org.jvoicexml.xml.ScxmlNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Factory for SCXML nodes.
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 */
final class ScxmlNodeFactory
        implements XmlNodeFactory<ScxmlNode> {
    /** Logger instance for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(ScxmlNodeFactory.class.getCanonicalName());

    /**
     * Known nodes. <br>
     * Each node can be retrieved via it's tag name.
     */
    private static final Map<String, ScxmlNode> NODES;

    static {
        NODES = new java.util.HashMap<String, ScxmlNode>();
    }

    /**
     * Constructs a new object.
     */
    ScxmlNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
        
        NODES.put(Final.TAG_NAME, new Final());
        NODES.put(History.TAG_NAME, new History());
        NODES.put(Initial.TAG_NAME, new Initial());
        NODES.put(Onentry.TAG_NAME, new Onentry());
        NODES.put(Onexit.TAG_NAME, new Onexit());
        NODES.put(Parallel.TAG_NAME, new Parallel());
        NODES.put(Raise.TAG_NAME, new Raise());
        NODES.put(Scxml.TAG_NAME, new Scxml());
        NODES.put(State.TAG_NAME, new State());
        NODES.put(Transition.TAG_NAME, new Transition());
    }

    /**
     * {@inheritDoc}
     */
    public ScxmlNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the right type.
        if (node instanceof ScxmlNode) {
            return (ScxmlNode) node;
        }

        final String name = node.getNodeName();
        final ScxmlNode scxmlNode = NODES.get(name);
        if (scxmlNode == null) {
            LOGGER.warning("cannot resolve node with name '" + name + "'");

            return new GenericScxmlNode(node);
        }

        return (ScxmlNode) scxmlNode.newInstance(node, this);
    }
}
