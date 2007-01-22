/*
 * File:    $RCSfile: SsmlNodeFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ssml;

import java.util.Map;

import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Factory for VoiceXmlNodes.
 *
 * @author Dirk Schnelle
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
final class SsmlNodeFactory
        implements XmlNodeFactory<SsmlNode> {
    /**
     * Known nodes. <br>
     * Each nodecan be retrieved via it's tag name.
     */
    private static final Map<String, SsmlNode> NODES;

    static {
        NODES = new java.util.HashMap<String, SsmlNode>();

        NODES.put(Speak.TAG_NAME, new Speak());
        NODES.put(Audio.TAG_NAME, new Audio());
        NODES.put(P.TAG_NAME, new P());
        NODES.put(S.TAG_NAME, new S());
        NODES.put(Voice.TAG_NAME, new Voice());
        NODES.put(Prosody.TAG_NAME, new Prosody());
        NODES.put(Desc.TAG_NAME, new Desc());
        NODES.put(Emphasis.TAG_NAME, new Emphasis());
        NODES.put(SayAs.TAG_NAME, new SayAs());
        NODES.put(Sub.TAG_NAME, new Sub());
        NODES.put(Phoneme.TAG_NAME, new Phoneme());
        NODES.put(Break.TAG_NAME, new Break());
        NODES.put(Mark.TAG_NAME, new Mark());
        NODES.put(Lexicon.TAG_NAME, new Lexicon());
    }

    /**
     * Constructs a new object.
     */
    SsmlNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the righttype.
        if (node instanceof SsmlNode) {
            return (SsmlNode) node;
        }

        final String name = node.getNodeName();
        final SsmlNode ssmlNode = NODES.get(name);
        if (ssmlNode == null) {
            System.err.println("cannot resolve node with name '" + name + "'");

            return ssmlNode;
        }

        return (SsmlNode) ssmlNode.newInstance(node);
    }
}
