/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/XmlNodeList.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.xml;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A list containing all <code>XmlNode</code>s of a parent node.
 *
 * <p>
 * This wrapper class is needed to ensure that all child nodes that
 * are accessed via the <code>Node.getChildNodes</code> method are of the
 * requested type.
 * </p>
 *
 * @see org.jvoicexml.xml.XmlNode
 *
 * @author Dirk Schnelle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @param <T> Type of the nodes in the list.
 */
public final class XmlNodeList<T extends XmlNode>
        implements NodeList {
    /** The list with the nodes. */
    private final List<T> list;

    /**
     * Constructs a new object.
     */
    public XmlNodeList() {
        list = new java.util.ArrayList<T>();
    }

    /**
     * Construct a new node list.
     *
     * @param factory
     *        The factory to resolve real <code>T</code>'s.
     * @param nodeList
     *        The nodelist to encapsulate.
     */
    public XmlNodeList(final XmlNodeFactory<T> factory,
            final NodeList nodeList) {
        this();

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node child = nodeList.item(i);
            final T node = factory.getXmlNode(child);
            list.add(node);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getLength() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    public Node item(final int index) {
        return list.get(index);
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>
     * <b>Note:</b> This has no effect on the document.
     * </p>
     *
     * @param node
     *        <code>XmlNode</code> to be appended to this list.
     * @return <code>true</code> (as per the general contract of the
     *         <code>Collection.add</code> method).
     */
    public boolean add(final T node) {
        return list.add(node);
    }
}
