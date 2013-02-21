/*
 * File:    $RCSfile: AbstractSsmlNode.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.android.exceptionTestProject.library;

import org.jvoicexml.xml.AbstractXmlNode;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Abstract base class for all nodes in a SSML document. Although this class is
 * an empty class it serves as a base type for all VoiceXML nodes.
 *
 * @see org.jvoicexml.xml.srgs.SrgsXmlDocument
 * @see org.jvoicexml.xml.XmlNode
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.5
 */
abstract class AbstractSsmlNode
        extends AbstractXmlNode
        implements SsmlNode {

    /** The <code>XmlNodefactory</code> to use. */
    private static final SsmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new SsmlNodeFactory();
    }

    /**
     * Construct a new {@link SsmlNode}.
     *
     * @param n
     *            The encapsulated node.
     */
    protected AbstractSsmlNode(final Node n) {
        super(n, NODE_FACTORY);
    }

    /**
     * Construct a new {@link SsmlNode}.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    protected AbstractSsmlNode(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }
}
