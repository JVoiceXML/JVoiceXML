/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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


import org.jvoicexml.xml.AbstractXmlNode;
import org.jvoicexml.xml.PlsNode;
import org.jvoicexml.xml.ScxmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;


/**
 * Abstract base class for all nodes in a PLS document.
 * Although this class is an empty class it serves as a base
 * type for all VoiceXML nodes.
 *
 * @see org.jvoicexml.xml.pls.PlsDocument
 * @see org.jvoicexml.xml.XmlNode
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
abstract class AbstractScxmlNode
        extends AbstractXmlNode implements ScxmlNode {
    /** The <code>XmlNodefactory</code> to use. */
    private static final ScxmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new ScxmlNodeFactory();
    }

    /**
     * Constructs a new {@link PlsNode}.
     * @param n The encapsulated node.
     */
    protected AbstractScxmlNode(final Node n) {
        super(n, NODE_FACTORY);
    }

    /**
     * Construct a new {@link PlsNode}.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    protected AbstractScxmlNode(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }
}
