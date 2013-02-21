/*
 * File:    $RCSfile: AbstractVoiceXmlNode.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/07/17 14:25:37 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;


import org.jvoicexml.xml.AbstractXmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Abstract base class for all nodes in a VoiceXML document.
 * Although this class is an empty class it serves as a base
 * type for all VoiceXML nodes.
 *
 * @see org.jvoicexml.xml.vxml.VoiceXmlDocument
 * @see org.jvoicexml.xml.XmlNode
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
abstract class AbstractVoiceXmlNode
        extends AbstractXmlNode implements VoiceXmlNode {

    /** The <code>XmlNodefactory</code> to use. */
    private static final VoiceXmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new VoiceXmlNodeFactory();
    }

    /**
     * Construct a new VoiceXmlNode.
     * @param n The encapsulated node.
     */
    protected AbstractVoiceXmlNode(final Node n) {
        super(n, NODE_FACTORY);
    }

    /**
     * A <code>NodeList</code> that contains all children of this node.
     *
     * @return NodeList
     */
    public final NodeList getChildNodes() {
        final Node node = getNode();

        return new XmlNodeList<VoiceXmlNode>(NODE_FACTORY, node.getChildNodes());
    }
}
