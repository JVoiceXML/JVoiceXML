/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/xml/ssml/GenericSsmlNode.java $
 * Version: $LastChangedRevision: 466 $
 * Date:    $Date: 2007-10-05 09:23:20 +0200 (Fri, 05 Oct 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.ccxml;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * A generic CCXML node that is returned by the CCXML node factory if the
 * node is unknown.
 *
 * @author Dirk Schnelle
 * @version $Revision: 466 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
class GenericCcxmlNode
        extends AbstractCcxmlNode {
    /**
     * Constructs a new object.
     * @param n The encapsulated node.
     */
    GenericCcxmlNode(final Node n) {
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
    private GenericCcxmlNode(final Node n,
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
        return new GenericCcxmlNode(n, factory);
    }
}
