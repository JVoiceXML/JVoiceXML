/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/scxml/GenericScxmlNode.java $
 * Version: $LastChangedRevision: 3202 $
 * Date:    $Date: 2012-08-09 09:25:40 +0200 (Thu, 09 Aug 2012) $
 * Author:  $LastChangedBy: schnelle $
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

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * A generic SCXML node that is returned by the SCXML node factory if the
 * node is unknown.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3202 $
 * @since 0.7.6
 */
class GenericScxmlNode
        extends AbstractScxmlNode {
    /**
     * Constructs a new object.
     * @param n The encapsulated node.
     */
    GenericScxmlNode(final Node n) {
        super(n);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private GenericScxmlNode(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String childName) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getTagName() {
        final Node node = getNode();
        return node.getLocalName();
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new GenericScxmlNode(n, factory);
    }
}
