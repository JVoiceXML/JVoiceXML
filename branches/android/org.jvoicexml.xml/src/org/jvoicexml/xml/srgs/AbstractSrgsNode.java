/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/AbstractSrgsNode.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $Date: 2013-07-16 13:01:00 +0200 (Tue, 16 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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


import org.jvoicexml.xml.AbstractXmlNode;
import org.jvoicexml.xml.SrgsNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;


/**
 * Abstract base class for all nodes in a SRGS document.
 * Although this class is an empty class it serves as a base
 * type for all VoiceXML nodes.
 *
 * @see org.jvoicexml.xml.srgs.SrgsXmlDocument
 * @see org.jvoicexml.xml.XmlNode
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3829 $
 * @since 0.5
 */
abstract class AbstractSrgsNode
        extends AbstractXmlNode implements SrgsNode {
    /** The <code>XmlNodefactory</code> to use. */
    private static final SrgsNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new SrgsNodeFactory();
    }

    /**
     * Constructs a new {@link SrgsNode}.
     * @param n The encapsulated node.
     */
    protected AbstractSrgsNode(final Node n) {
        super(n, NODE_FACTORY);
    }

    /**
     * Construct a new {@link SrgsNode}.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    protected AbstractSrgsNode(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }
}
