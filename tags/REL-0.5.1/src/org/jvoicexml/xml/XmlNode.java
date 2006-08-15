/*
 * File:    $RCSfile: XmlNode.java,v $
 * Version: $Revision: 1.13 $
 * Date:    $Date: 2006/07/10 08:42:22 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml;

import java.util.Collection;

import org.w3c.dom.Node;

/**
 * Base interface for all nodes in an XML document.
 *
 * @see org.jvoicexml.xml.XmlDocument
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.13 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
public interface XmlNode
        extends XmlWritable, Node {
    /**
     * Retrieves the encapsulated node.
     *
     * @return The encapsulated node.
     */
    Node getNode();

    /**
     * Retrieves the name of the tag for the derived node.
     *
     * @return The name of the tag.
     */
    String getTagName();

    /**
     * Create a new instance for the given node.
     *
     * <p>
     * Each <code>XmlNode</code> can serve as a prototype in a
     * <code>XmlNodeFactory</code> to produce a collection of
     * child nodes. Factories can then use the prototype pattern to
     * produce new nodes.
     * </p>
     *
     * @param n
     *        The node to encapsulate.
     * @return The new instance.
     *
     * @see XmlNodeFactory
     */
    XmlNode newInstance(final Node n);

    /**
     * Adds an instance of the specified child class to this node. This causes a
     * new node to be created and appended to this node. The type of the node to
     * add must be a subclass of the XmlNode class.
     *
     * @param tagName
     *        The tag name of the node to add.
     * @return Newly created and appended node or null if the child is not
     *         allowed on this node.
     */
    XmlNode addChild(final String tagName);

    /**
     * Convenient method to get the value of an attribute.
     *
     * @param attribute
     *        Name of the attribute.
     * @return Value of theattribute, <code>null</code> if the value of the
     *         attribute cannot be retrieved.
     */
    String getAttribute(final String attribute);

    /**
     * Convenient method to set the value of an attribute. If the assigned value
     * is <code>null</code> then the attribute is removed.
     *
     * @param name
     *        Name of the attribute.
     * @param value
     *        New value of the attribute or <code>null</code> to remove the
     *        attribute.
     */
    void setAttribute(final String name, final String value);

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     * @since 0.3.1
     */
    Collection<String> getAttributeNames();

    /**
     * Return a collection of child nodes with the specified tag class.
     *
     * @param <T>
     *        Type of the child nodes.
     * @param tagClass
     *        Class of child node to return.
     * @return A collection of child nodes of the specified type. If this node
     *         does not contain any child nodes of the specified type then an
     *         empty collection is returned.
     * @since 0.5
     */
    <T extends XmlNode> Collection<T> getChildNodes(final Class<T> tagClass);
}
