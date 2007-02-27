/*
 * File:    $RCSfile: AbstractXmlNode.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * Abstract base class for all nodes in an XML document.
 *
 * @see org.jvoicexml.xml.XmlDocument
 *
 * @author Steve Doyle
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public abstract class AbstractXmlNode
        implements XmlNode {
    /** The encapsulated node. */
    private final Node node;

    /** The node factory. */
    private final XmlNodeFactory<? extends XmlNode> factory;

    /**
     * Construct a new XmlNode.
     *
     * @param n
     *        The encapsulated node.
     * @param nodeFactory
     *        Node factory to create node lists.
     */
    protected AbstractXmlNode(final Node n,
                              final XmlNodeFactory<? extends XmlNode>
                              nodeFactory) {
        node = n;
        factory = nodeFactory;
    }

    /**
     * {@inheritDoc}
     */
    public final Node getNode() {
        return node;
    }

    /**
     * Retrieves the factory to create node lists.
     * @return Factory to create node lists.
     *
     * @since 0.5
     */
    protected final XmlNodeFactory<? extends XmlNode> getNodeFactory() {
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    public final String getAttribute(final String attribute) {
        final NamedNodeMap attributes = node.getAttributes();

        if (attributes == null) {
            return null;
        }

        final Node item = attributes.getNamedItem(attribute);
        if (item == null) {
            return null;
        }

        return item.getNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    public final void setAttribute(final String name, final String value) {
        final NamedNodeMap attributes = node.getAttributes();

        if (attributes == null) {
            return;
        }

        if (value == null) {
            attributes.removeNamedItem(name);
        } else {
            final Document owner = node.getOwnerDocument();
            final Node item = owner.createAttribute(name);
            item.setNodeValue(value);
            attributes.setNamedItem(item);
        }
    }

    /**
     * Adds the node <code>newChild</code> to the end of the list of children
     * of this node.
     *
     * @param newChild
     *        The node to add.If it is a <code>DocumentFragment</code> object,
     *        the entire contents of the document fragment are moved into the
     *        child list of this node
     * @return The node added.
     * @throws DOMException
     *         HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does
     *         not allow children of the type of the <code>newChild</code>
     *         node, or if the node to append is one of this node's ancestors or
     *         this node itself. <br>
     *         WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *         from a different document than the one that created this node.
     *         <br>
     *         NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
     *         if the previous parent of the node being inserted is readonly.
     */
    public final Node appendChild(final Node newChild)
            throws DOMException {
        return node.appendChild(getRawNode(newChild));
    }

    /**
     * Returns a duplicate of this node, i.e., serves as a generic copy
     * constructor for nodes.
     *
     * @param deep
     *        If <code>true</code>, recursively clone the subtree under the
     *        specified node; if <code>false</code>, clone only the node
     *        itself (and its attributes, if it is an <code>Element</code>).
     * @return The duplicate node.
     */
    public final Node cloneNode(final boolean deep) {
        return node.cloneNode(deep);
    }

    /**
     * A <code>NamedNodeMap</code> containing the attributes of this node (if
     * it is an <code>Element</code>) or <code>null</code> otherwise.
     *
     * @return NamedNodeMap
     */
    public final NamedNodeMap getAttributes() {
        return node.getAttributes();
    }

    /**
     * Returns the local part of the qualified name of this node.
     *
     * @return String
     */
    public final String getLocalName() {
        return node.getLocalName();
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @return String
     */
    public final String getNamespaceURI() {
        return node.getNamespaceURI();
    }

    /**
     * The name of this node, depending on its type; see the table above.
     *
     * @return String
     */
    public final String getNodeName() {
        return node.getNodeName();
    }

    /**
     * A code representing the type of the underlying object, as defined above.
     *
     * @return short
     */
    public final short getNodeType() {
        return node.getNodeType();
    }

    /**
     * The value of this node, depending on its type; see the table above.
     *
     * @throws DOMException
     *         DOMSTRING_SIZE_ERR: Raised when it would return more characters
     *         than fit in a <code>DOMString</code> variable on the
     *         implementation platform.
     * @return String
     */
    public final String getNodeValue()
            throws DOMException {
        return node.getNodeValue();
    }

    /**
     * The <code>Document</code> object associated with this node.
     *
     * @return Document
     */
    public final Document getOwnerDocument() {
        return node.getOwnerDocument();
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @return String
     */
    public final String getPrefix() {
        return node.getPrefix();
    }

    /**
     * Retrieves the object associated to a key on a this node. The object must
     * first have been set to this node by calling setUserData with the same
     * key.
     *
     * @param key
     *        The key the object is associated to.
     * @return Returns the DOMUserData associated to the given key on this node,
     *         or <code>null</code> if there was none.
     */
    public final Object getUserData(final String key) {
        return node.getUserData(key);
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     *
     * @return <code>true</code> if this node has any attributes,
     *         <code>false</code> otherwise.
     */
    public final boolean hasAttributes() {
        return node.hasAttributes();
    }

    /**
     * Returns whether this node has any children.
     *
     * @return <code>true</code> if this node has any children,
     *         <code>false</code> otherwise.
     */
    public final boolean hasChildNodes() {
        return node.hasChildNodes();
    }

    /**
     * Inserts the node <code>newChild</code> before the existing child node
     * <code>refChild</code>.
     *
     * @param newChild
     *        The node to insert.
     * @param refChild
     *        The reference node, i.e., the node before which the new node must
     *        be inserted.
     * @return The node being inserted.
     * @throws DOMException
     *         HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does
     *         not allow children of the type of the <code>newChild</code>
     *         node, or if the node to insert is one of this node's ancestors or
     *         this node itself. <br>
     *         WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *         from a different document than the one that created this node.
     *         <br>
     *         NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
     *         if the parent of the node being inserted is readonly. <br>
     *         NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child
     *         of this node.
     */
    public final Node insertBefore(final Node newChild, final Node refChild)
            throws DOMException {
        return node.insertBefore(getRawNode(newChild), getRawNode(refChild));
    }

    /**
     * Tests whether the DOM implementation implements a specific feature and
     * that feature is supported by this node.
     *
     * @param feature
     *        The name of the feature to test. This is the same name which can
     *        be passed to the method <code>hasFeature</code> on
     *        <code>DOMImplementation</code>.
     * @param version
     *        This is the version number of the feature to test. In Level 2,
     *        version 1, this is the string "2.0". If the version is not
     *        specified, supporting any version of the feature will cause the
     *        method to return <code>true</code>.
     * @return Returns <code>true</code> if the specified feature is supported
     *         on this node, <code>false</code> otherwise.
     */
    public final boolean isSupported(final String feature,
                                     final String version) {
        return node.isSupported(feature, version);
    }

    /**
     * Puts all <code>Text</code> nodes in the full depth of the sub-tree
     * underneath this <code>Node</code>, including attribute nodes, into a
     * "normal" form where only structure (e.g., elements, comments, processing
     * instructions, CDATA sections, and entity references) separates
     * <code>Text</code> nodes, i.e., there are neither adjacent
     * <code>Text</code> nodes nor empty <code>Text</code> nodes.
     */
    public final void normalize() {
        node.normalize();
    }

    /**
     * Removes the child node indicated by <code>oldChild</code> from the list
     * of children, and returns it.
     *
     * @param oldChild
     *        The node being removed.
     * @return The node removed.
     * @throws DOMException
     *         NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *         <br>
     *         NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child
     *         of this node.
     */
    public final Node removeChild(final Node oldChild)
            throws DOMException {
        return node.removeChild(getRawNode(oldChild));
    }

    /**
     * Replaces the child node <code>oldChild</code> with
     * <code>newChild</code> in the list of children, and returns the
     * <code>oldChild</code> node.
     *
     * @param newChild
     *        The new node to put in the child list.
     * @param oldChild
     *        The node being replaced in the list.
     * @return The node replaced.
     * @throws DOMException
     *         HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does
     *         not allow children of the type of the <code>newChild</code>
     *         node, or if the node to put in is one of this node's ancestors or
     *         this node itself. <br>
     *         WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *         from a different document than the one that created this node.
     *         <br>
     *         NO_MODIFICATION_ALLOWED_ERR: Raised if this node or the parent of
     *         the new node is readonly. <br>
     *         NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child
     *         of this node.
     */
    public final Node replaceChild(final Node newChild, final Node oldChild)
            throws DOMException {
        return node.replaceChild(getRawNode(newChild), getRawNode(oldChild));
    }

    /**
     * The value of this node, depending on its type; see the table above.
     *
     * @throws DOMException
     *         DOMSTRING_SIZE_ERR: Raised when it would return more characters
     *         than fit in a <code>DOMString</code> variable on the
     *         implementation platform.
     * @param nodeValue
     *        String
     */
    public final void setNodeValue(final String nodeValue)
            throws DOMException {
        node.setNodeValue(nodeValue);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @throws DOMException
     *         INVALID_CHARACTER_ERR: Raised if the specified prefix contains an
     *         illegal character, per the XML 1.0 specification . <br>
     *         NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *         <br>
     *         NAMESPACE_ERR: Raised if the specified <code>prefix</code> is
     *         malformed per the Namespaces in XML specification, if the
     *         <code>namespaceURI</code> of this node is <code>null</code>,
     *         if the specified prefix is "xml" and the
     *         <code>namespaceURI</code> of this node is different from
     *         "http://www.w3.org/XML/1998/namespace", if this node is an
     *         attribute and the specified prefix is "xmlns" and the
     *         <code>namespaceURI</code> of this node is different from "
     *         http://www.w3.org/2000/xmlns/", or if this node is an attribute
     *         and the <code>qualifiedName</code> of this node is "xmlns" .
     * @param prefix
     *        String
     */
    public final void setPrefix(final String prefix)
            throws DOMException {
        node.setPrefix(prefix);
    }

    /**
     * Associate an object to a key on this node. The object can later be
     * retrieved from this node by calling getUserData with the same key.
     *
     * @param key
     *        The key to associate the object to.
     * @param data
     *        The object to associate to the given key, or <code>null</code>
     *        to remove any existing association to that key.
     * @param handler
     *        The handler to associate to that key, or <code>null</code>
     * @return Returns the <code>DOMUserData</code> previously associated to
     *         the given key on this node, or <code>null</code> if there was
     *         none.
     */
    public Object setUserData(final String key, final Object data,
                              final UserDataHandler handler) {
        return node.setUserData(key, data, handler);
    }

    /**
     * The first child of this node.
     *
     * @return Node
     */
    public final Node getFirstChild() {
        return factory.getXmlNode(node.getFirstChild());
    }

    /**
     * The last child of this node.
     *
     * @return Node
     */
    public final Node getLastChild() {
        return factory.getXmlNode(node.getLastChild());
    }

    /**
     * The node immediately following this node.
     *
     * @return Node
     */
    public final Node getNextSibling() {
        return factory.getXmlNode(node.getNextSibling());
    }

    /**
     * The node immediately preceding this node.
     *
     * @return Node
     */
    public final Node getPreviousSibling() {
        return factory.getXmlNode(node.getPreviousSibling());
    }

    /**
     * The parent of this node.
     *
     * @return Node
     */
    public final Node getParentNode() {
        return factory.getXmlNode(node.getParentNode());
    }

    /**
     * This is the primary method used to write an object and its children as
     * XML text. Implementations with children should use writeChildrenXml to
     * write those children, to allow selective overriding.
     *
     * @param writer
     *        XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    public void writeXml(final XmlWriter writer)
            throws IOException {
        writer.printIndent();
        writer.write("<");
        writer.write(getTagName());

        if (hasAttributes()) {
            final NamedNodeMap attributes = getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                final Node attribute = attributes.item(i);
                writer.writeAttribute(attribute.getNodeName(), attribute
                                      .getNodeValue());
            }
        }

        if (hasChildNodes()) {
            writer.write('>');

            writer.incIndentLevel();

            writeChildrenXml(writer);

            writer.decIndentLevel();
            writer.printIndent();

            writer.write("</");
            writer.write(getTagName());
            writer.write('>');
        } else {
            writer.write("/>");
        }
    }

    /**
     * Used to write any children of a node.
     *
     * @param writer
     *        XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    public void writeChildrenXml(final XmlWriter writer)
            throws IOException {
        final NodeList children = getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof XmlWritable) {
                final XmlWritable writable = (XmlWritable) child;
                writable.writeXml(writer);
            }
        }
    }

    /**
     * The absolute base URI of this node or <code>null</code> if the
     * implementation wasn't able to obtain an absolute URI.
     *
     * @return String
     */
    public String getBaseURI() {
        return node.getBaseURI();
    }

    /**
     * Compares the reference node, i.e.
     *
     * @param other
     *        The node to compare against the reference node.
     * @return Returns how the node is positioned relatively to the reference
     *         node.
     * @throws DOMException
     *         NOT_SUPPORTED_ERR: when the compared nodes are from different DOM
     *         implementations that do not coordinate to return consistent
     *         implementation-specific results.
     */
    public short compareDocumentPosition(final Node other)
            throws DOMException {
        return node.compareDocumentPosition(getRawNode(other));
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @throws DOMException
     *         DOMSTRING_SIZE_ERR: Raised when it would return more characters
     *         than fit in a <code>DOMString</code> variable on the
     *         implementation platform.
     * @return String
     */
    public String getTextContent()
            throws DOMException {
        return node.getTextContent();
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @throws DOMException
     *         NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @param textContent
     *        String
     */
    public void setTextContent(final String textContent)
            throws DOMException {
        node.setTextContent(textContent);
    }

    /**
     * Returns whether this node is the same node as the given one.
     *
     * @param other
     *        The node to test against.
     * @return Returns <code>true</code> if the nodes are the same,
     *         <code>false</code> otherwise.
     */
    public boolean isSameNode(final Node other) {
        return node.isSameNode(getRawNode(other));
    }

    /**
     * Look up the prefix associated to the given namespace URI, starting from
     * this node.
     *
     * @param namespaceURI
     *        The namespace URI to look for.
     * @return Returns an associated namespace prefix if found or
     *         <code>null</code> if none is found. If more than one prefix are
     *         associated to the namespace prefix, the returned namespace prefix
     *         is implementation dependent.
     */
    public String lookupPrefix(final String namespaceURI) {
        return node.lookupPrefix(namespaceURI);
    }

    /**
     * This method checks if the specified <code>namespaceURI</code> is the
     * default namespace or not.
     *
     * @param namespaceURI
     *        The namespace URI to look for.
     * @return Returns <code>true</code> if the specified
     *         <code>namespaceURI</code> is the default namespace,
     *         <code>false</code> otherwise.
     */
    public boolean isDefaultNamespace(final String namespaceURI) {
        return node.isDefaultNamespace(namespaceURI);
    }

    /**
     * Look up the namespace URI associated to the given prefix, starting from
     * this node.
     *
     * @param prefix
     *        The prefix to look for. If this parameter is <code>null</code>,
     *        the method will return the default namespace URI if any.
     * @return Returns the associated namespace URI or <code>null</code> if
     *         none is found.
     */
    public String lookupNamespaceURI(final String prefix) {
        return node.lookupNamespaceURI(prefix);
    }

    /**
     * Tests whether two nodes are equal.
     *
     * @param arg
     *        The node to compare equality with.
     * @return Returns <code>true</code> if the nodes are equal,
     *         <code>false</code> otherwise.
     */
    public boolean isEqualNode(final Node arg) {
        return node.isEqualNode(getRawNode(arg));
    }

    /**
     * This method returns a specialized object which implements the specialized
     * APIs of the specified feature and version, as specified in .
     *
     * @param feature
     *        The name of the feature requested. Note that any plus sign "+"
     *        prepended to the name of the feature will be ignored since it is
     *        not significant in the context of this method.
     * @param version
     *        This is the version number of the feature to test.
     * @return Returns an object which implements the specialized APIs of the
     *         specified feature and version, if any, or <code>null</code> if
     *         there is no object which implements interfaces associated with
     *         that feature. If the <code>DOMObject</code> returned by this
     *         method implements the <code>Node</code> interface, it must
     *         delegate to the primary core <code>Node</code> and not return
     *         results inconsistent with the primary core <code>Node</code>
     *         such as attributes, childNodes, etc.
     */
    public Object getFeature(final String feature, final String version) {
        return node.getFeature(feature, version);
    }

    /**
     * Get the raw node encapsulated by the specified node. If the specified
     * node is a raw node then it is returned.
     *
     * @param arg
     *        The node that may be wrapping a raw node.
     * @return The raw node.
     */
    private Node getRawNode(final Node arg) {
        final Node rawNode;
        if (arg instanceof XmlNode) {
            final XmlNode xmlNode = (XmlNode) arg;
            rawNode = xmlNode.getNode();
        } else {
            rawNode = arg;
        }
        return rawNode;
    }

    /**
     * Adds an instance of the specified child class to this node. This causes a
     * new node to be created and appended to this node. The type of the node to
     * add must be a subclass of the XmlNode class.
     *
     * @param <T>
     *        Node type to load.
     * @param tagClass
     *        The class type of the node to add.
     * @return Newly created and appended node or null if the child is not
     *         allowed on this node.
     */
    public <T extends XmlNode> T addChild(final Class<T> tagClass) {
        try {
            final T tempTag = tagClass.newInstance();
            final String tagName = tempTag.getTagName();

            if (canContainChild(tagName)) {
                final Document document = getOwnerDocument();
                final Node newNode = document.createElement(tagName);

                T newTag = tagClass.cast(tempTag.newInstance(newNode));
                appendChild(newTag);

                return newTag;
            } else {
                System.err.println("<" + getTagName() + "> must not contain <"
                            + tagName + ">");
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode addChild(final String tagName) {
        if (canContainChild(tagName)) {
            final Document document = getOwnerDocument();
            final Node newNode = document.createElement(tagName);

            /** @todo This does not work for text nodes. */
            final XmlNode newTag = newInstance(newNode);
            appendChild(newTag);

            return newTag;
        } else {
            System.err.println("<" + getTagName() + "> must not contain <"
                    + tagName + ">");
        }

        return null;
    }


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
     */
    public <T extends XmlNode> Collection<T> getChildNodes(
            final Class<T> tagClass) {
        final Collection<T> nodes = new java.util.ArrayList<T>();

        try {
            final T newInstance = tagClass.newInstance();
            final String tagName = newInstance.getTagName();

            final NodeList list = getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                final Node n = list.item(i);
                if (n instanceof XmlNode) {
                    final XmlNode xmlNode = (XmlNode) n;
                    final String xmlNodeTagName = xmlNode.getTagName();
                    if (xmlNodeTagName.compareTo(tagName) == 0) {
                        nodes.add(tagClass.cast(xmlNode));
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return nodes;
    }

    /**
     * Can the specified sub-tag be contained within this node?
     *
     * @param childName
     *        Name of child.
     * @return True if the sub-tag is allowed on this node.
     */
    protected abstract boolean canContainChild(final String childName);

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        final XmlStringWriter writer = new XmlStringWriter(
                XmlWriter.DEFAULT_BLOCK_INDENT);

        try {
            writer.writeHeader();
            writeXml(writer);
        } catch (IOException ioe) {
            return super.toString();
        }

        return writer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return new ArrayList<String>();
    }

}
