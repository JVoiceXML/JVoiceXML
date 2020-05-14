/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
 * @author Dirk Schnelle-Walka
 */
public abstract class AbstractXmlNode
        implements XmlNode {
    /** The encapsulated node. */
    private final Node node;

    /** The node factory. */
    private final XmlNodeFactory<? extends XmlNode> factory;

    /**
     * Constructs a new XmlNode.
     *
     * @param n
     *        The encapsulated node.
     * @param nodeFactory
     *        Node factory to create node lists.
     */
    protected AbstractXmlNode(final Node n,
                              final XmlNodeFactory<? extends XmlNode>
                              nodeFactory) {
        Node current = n;
        while (current instanceof XmlNode) {
            final XmlNode xmlnode = (XmlNode) current;
            current = xmlnode.getNode();
            if (current == null) {
                current = xmlnode;
                break;
            }
        }
        node = current;
        factory = nodeFactory;
    }

    /**
     * A {@link NodeList} that contains all children of this node.
     *
     * @return NodeList
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final NodeList getChildNodes() {
        return new XmlNodeList(factory, node.getChildNodes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node getNode() {
        Node current = node;
        while (current instanceof XmlNode) {
            final XmlNode xmlnode = (XmlNode) current;
            current = xmlnode.getNode();
            if (current == null) {
                return xmlnode;
            }
        }
        return current;
    }

    /**
     * Retrieves the factory to create node lists.
     * @return Factory to create node lists.
     *
     * @since 0.5
     */
    public final XmlNodeFactory<? extends XmlNode> getNodeFactory() {
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * Checks, if the attribute with the given name is present.
     * @param attribute name of the attribute to check for
     * @return <code>true</code> if there is an attribute with the given name
     * @since 0.7.6
     */
    public final boolean hasAttribute(final String attribute) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return false;
        }
        final Node item = attributes.getNamedItem(attribute);
        return item != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setAttribute(final String name, final String value) {
        final NamedNodeMap attributes = node.getAttributes();

        if (attributes == null) {
            return;
        }

        if (value == null) {
            // Remove the attribute if no value was specified.
            if (attributes.getNamedItem(name) != null) {
                attributes.removeNamedItem(name);
            }
        } else {
            // Remove a possibly existing attribute
            if (attributes.getNamedItem(name) != null) {
                attributes.removeNamedItem(name);
            }
            // Create a new attribute.
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
     */
    public final Node appendChild(final Node newChild) {
        return node.appendChild(getRawNode(newChild));
    }

    /**
     * Appends a deep clone of the given node to the children of this node.
     * @param origin the node to clone
     * @return the cloned node
     * @since 0.7.5
     */
    public AbstractXmlNode appendDeepClone(final AbstractXmlNode origin) {
        final String tag = origin.getNodeName();
        final AbstractXmlNode clone = (AbstractXmlNode)addChild(tag);
        final NamedNodeMap attributes = origin.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node attribute = attributes.item(i);

            String name = attribute.getNodeName();
            final String value = attribute.getNodeValue();
            clone.setAttribute(name, value);
        }
        final Collection<AbstractXmlNode> children = origin.getChildren();
        for (AbstractXmlNode child : children) {
            clone.appendDeepClone(child);
        }
        return clone;
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
     * @return String
     */
    public final String getNodeValue() {
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
     * Retrieves the {@link XmlDocument} containing this node.
     * @param <T> type of the owner document.
     * @param documentClass owner document's class.
     * @return document containing this class.
     * @since 0.6
     */
    public final <T extends XmlDocument> T getOwnerXmlDocument(
            final Class<T> documentClass) {
        final Document doc = node.getOwnerDocument();
        Constructor<T> constructor;
        try {
            constructor = documentClass.getConstructor(Document.class);
            return constructor.newInstance(doc);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
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
     */
    public final Node insertBefore(final Node newChild, final Node refChild) {
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
     */
    public final Node removeChild(final Node oldChild) {
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
     */
    public final Node replaceChild(final Node newChild, final Node oldChild) {
        return node.replaceChild(getRawNode(newChild), getRawNode(oldChild));
    }

    /**
     * The value of this node, depending on its type; see the table above.
     *
     * @param nodeValue
     *        String
     */
    public final void setNodeValue(final String nodeValue) {
        node.setNodeValue(nodeValue);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @param prefix
     *        String
     */
    public final void setPrefix(final String prefix) {
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
    public final Object setUserData(final String key, final Object data,
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
        final Node lastChild = node.getLastChild();
        return factory.getXmlNode(lastChild);
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
     * The absolute base URI of this node or <code>null</code> if the
     * implementation wasn't able to obtain an absolute URI.
     *
     * @return String
     */
    public final String getBaseURI() {
        return node.getBaseURI();
    }

    /**
     * Compares the reference node, i.e.
     *
     * @param other
     *        The node to compare against the reference node.
     * @return Returns how the node is positioned relatively to the reference
     *         node.
     */
    public final short compareDocumentPosition(final Node other) {
        return node.compareDocumentPosition(getRawNode(other));
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @return String
     */
    public final String getTextContent() {
        return node.getTextContent();
    }

    /**
     * Returns the text contents of this node, similar to
     * {@link #getTextContent()} but without recursion.
     *
     * @return text content.
     * @since 0.6
     */
    public final String getFirstLevelTextContent() {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            return node.getNodeValue();
        }

        StringBuilder str = new StringBuilder();
        Node child = node.getFirstChild();
        while (child != null) {
            final short type = child.getNodeType();
            if ((type == Node.TEXT_NODE) || (type == Node.CDATA_SECTION_NODE)) {
                str.append(child.getNodeValue());
            }
            child = child.getNextSibling();
        }

        return str.toString();
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @param textContent
     *        String
     */
    public final void setTextContent(final String textContent) {
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
    public final boolean isSameNode(final Node other) {
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
    public final String lookupPrefix(final String namespaceURI) {
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
    public final boolean isDefaultNamespace(final String namespaceURI) {
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
    public final String lookupNamespaceURI(final String prefix) {
        return node.lookupNamespaceURI(prefix);
    }

    /**
     * Tests whether two nodes are equal.
     *
     * @param other
     *        The node to compare equality with.
     * @return Returns <code>true</code> if the nodes are equal,
     *         <code>false</code> otherwise.
     */
    public final boolean isEqualNode(final Node other) {
        return node.isEqualNode(getRawNode(other));
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
    public final Object getFeature(final String feature, final String version) {
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
     * @since 0.6
     */
    public final <T extends XmlNode> T addChild(final Class<T> tagClass) {
        try {
            final T tempTag = tagClass.newInstance();
            final String tagName = tempTag.getTagName();

            if (canContainChild(tagName)) {
                final Document document = getOwnerDocument();
                final Node newNode = document.createElement(tagName);

                final T newTag =
                    tagClass.cast(tempTag.newInstance(newNode, factory));

                return newTag;
            } else {
                throw new IllegalArgumentException("<" + getTagName()
                        + "> must not contain <" + tagName + ">");
            }
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds an instance of the specified child class to this node and appends
     * it to the child nodes of this node.
     *
     * @param <T>
     *        Node type to load.
     * @param tagClass
     *        The class type of the node to add.
     * @return Newly created and appended node or null if the child is not
     *         allowed on this node.
     */
    public final <T extends XmlNode> T appendChild(final Class<T> tagClass) {
        final T newTag = addChild(tagClass);
        appendChild(newTag);
        return newTag;
    }

    /**
     * {@inheritDoc}
     */
    public final XmlNode addChild(final String tagName) {
        if (tagName == null) {
            throw new IllegalArgumentException("tag name must not be null!");
        }
        final String tag = tagName.trim();
        if (tag.indexOf(' ') >= 0) {
            throw new IllegalArgumentException(
                    "tag name must not contain attributes!");
        }
        int dotPos = tag.indexOf(':');
        if (canContainChild(tag) || dotPos >= 0) {
            final Document document = getOwnerDocument();
            final Node newNode = document.createElement(tag);

            /** @todo This does not work for text nodes. */
            final XmlNode newTag = factory.getXmlNode(newNode);
            appendChild(newTag);

            return newTag;
        } else {
            throw new IllegalArgumentException("<" + getTagName()
                    + "> must not contain <" + tagName + ">");
        }
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
    public final <T extends XmlNode> Collection<T> getChildNodes(
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
                    final String xmlNodeTagName = xmlNode.getNodeName();
                    final String localName = xmlNode.getLocalName();
                    if (tagName.equals(xmlNodeTagName)
                            || tagName.equals(localName)) {
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
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <T extends XmlNode> Collection<T> getChildren() {
        final Collection<T> nodes = new java.util.ArrayList<T>();

        final NodeList list = getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node n = list.item(i);
            if (n instanceof XmlNode) {
                final T xmlNode = (T) factory.getXmlNode(n);
                nodes.add(xmlNode);
            }
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            final Source source = new DOMSource(node);
            transformer.transform(source, result);
            return out.toString(encoding);
        } catch (TransformerException e) {
            return super.toString();
        } catch (UnsupportedEncodingException e) {
            return super.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return new ArrayList<String>();
    }

    /**
     * Retrieves a list of all attributes defined in this node.
     * @return list of all defined attributes
     * @since 0.7.5
     */
    public final Collection<String> getDefinedAttributeNames() {
        final Collection<String> attributes = new java.util.ArrayList<String>();
        final NamedNodeMap nodes = getAttributes();
        int index = 0;
        for (Node current = nodes.item(index); index < nodes.getLength();
            index++) {
            final String name = current.getNodeName();
            attributes.add(name);
        }
        return attributes;
    }

    /**
     * {@inheritDoc}
     * @since 0.7
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractXmlNode)) {
            return false;
        }
        final AbstractXmlNode other = (AbstractXmlNode) obj;

        if (node == other.node) {
            return true;
        }
        if (node == null) {
            return false;
        }
        return node.isEqualNode(other.node);
    }

    /**
     * {@inheritDoc}
     * @since 0.7
     */
    @Override
    public final int hashCode() {
        if (node == null) {
            return super.hashCode();
        }
        return node.hashCode();
    }
}
