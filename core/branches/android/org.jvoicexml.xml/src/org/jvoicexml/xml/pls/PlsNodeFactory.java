/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/pls/PlsNodeFactory.java $
 * Version: $LastChangedRevision: 2810 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.xml.pls;

import java.util.Map;
import java.util.logging.Logger;

import org.jvoicexml.xml.PlsNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Factory for PlsNodes.
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision: 2810 $
 * @since 0.6
 */
final class PlsNodeFactory
        implements XmlNodeFactory<PlsNode> {
    /** Logger instance for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(PlsNodeFactory.class.getCanonicalName());

    /**
     * Known nodes. <br>
     * Each nodecan be retrieved via it's tag name.
     */
    private static final Map<String, PlsNode> NODES;

    static {
        NODES = new java.util.HashMap<String, PlsNode>();

        NODES.put(Lexicon.TAG_NAME, new Lexicon());
        NODES.put(Meta.TAG_NAME, new Meta());
        NODES.put(Metadata.TAG_NAME, new Metadata());
        NODES.put(Lexeme.TAG_NAME, new Lexeme());
        NODES.put(Grapheme.TAG_NAME, new Grapheme());
        NODES.put(Phoneme.TAG_NAME, new Phoneme());
        NODES.put(Example.TAG_NAME, new Example());
        NODES.put(Alias.TAG_NAME, new Alias());
    }

    /**
     * Constructs a new object.
     */
    PlsNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
    }

    /**
     * {@inheritDoc}
     */
    public PlsNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the right type.
        if (node instanceof PlsNode) {
            return (PlsNode) node;
        }

        final String name = node.getNodeName();
        final PlsNode plsXmlNode = NODES.get(name);
        if (plsXmlNode == null) {
            LOGGER.warning("cannot resolve node with name '" + name + "'");

            return new GenericPlsNode(node);
        }

        return (PlsNode) plsXmlNode.newInstance(node, this);
    }
}
