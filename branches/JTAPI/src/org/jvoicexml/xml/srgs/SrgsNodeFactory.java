/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import java.util.Map;

import org.jvoicexml.xml.SrgsNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Factory for SrgSNodes.
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
final class SrgsNodeFactory
        implements XmlNodeFactory<SrgsNode> {
    /**
     * Known nodes. <br>
     * Each nodecan be retrieved via it's tag name.
     */
    private static final Map<String, SrgsNode> NODES;

    static {
        NODES = new java.util.HashMap<String, SrgsNode>();

        NODES.put(Grammar.TAG_NAME, new Grammar());
        NODES.put(Example.TAG_NAME, new Example());
        NODES.put(Item.TAG_NAME, new Item());
        NODES.put(OneOf.TAG_NAME, new OneOf());
        NODES.put(Rule.TAG_NAME, new Rule());
        NODES.put(Ruleref.TAG_NAME, new Ruleref());
        NODES.put(Tag.TAG_NAME, new Tag());
        NODES.put(Token.TAG_NAME, new Token());
    }

    /**
     * Constructs a new object.
     */
    SrgsNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
    }

    /**
     * {@inheritDoc}
     */
    public SrgsNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the righttype.
        if (node instanceof SrgsNode) {
            return (SrgsNode) node;
        }

        final String name = node.getNodeName();
        final SrgsNode voiceXmlNode = NODES.get(name);
        if (voiceXmlNode == null) {
            System.err.println("cannot resolve node with name '" + name + "'");

            return voiceXmlNode;
        }

        return (SrgsNode) voiceXmlNode.newInstance(node);
    }
}
