/*
 * File:    $RCSfile: XmlDocument.java,v $
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An XML Document abstract base class.
 *
 * <p>
 * VoiceXML is designed for creating audio dialogs that feature synthesized
 * speech, digitized audio, regognition of spoken and DTMF key input, recording
 * of spoken input, telephony and mixed initiative conversations. Its major goal
 * is to bring the advantages of web-based development and content delivery to
 * interactive voiceresponse applications.
 * </p>
 *
 * <p>
 * Objects of this class can create such VoiceXML documents or parse them.
 * </p>
 *
 * @author Steve Doyle
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public abstract class XmlDocument
        implements XmlWritable, Document {

    /** The encapsulated document. */
    private final Document document;

    /**
     * Create an empty XML document.
     *
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     */
    public XmlDocument()
            throws ParserConfigurationException {
        final DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Configure the factory to ignore comments
        factory.setIgnoringComments(true);

        final DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        if (document != null) {
            appendChild(createRootNode());
        }
    }

    /**
     * Constructs a new XML document from the given input source.
     *
     * @param source
     *        Input source for a single XML document.
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     * @throws SAXException
     *         Error parsing the input source.
     * @throws IOException
     *         Error reading the input source.
     */
    public XmlDocument(final InputSource source)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Configure the factory to ignore comments
        factory.setIgnoringComments(true);

        final DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(source);
    }

    /**
     * Construct a new XML document with the given document.
     *
     * @param doc
     *        Encapsulated document.
     */
    public XmlDocument(final Document doc) {
        document = doc;
    }

    /**
     * Retrieves the encapsualted document.
     * @return The encapsualted document.
     */
    protected final Document getDocument() {
        return document;
    }

    /**
     * Retrieves the node factory for child node lists.
     * @return Node factory for child node lists.
     *
     * @since 0.5
     */
    protected abstract XmlNodeFactory getXmlNodefactory();

    /**
     * Create the root node of the document.
     *
     * @return Root node
     */
    protected abstract Node createRootNode();

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
        final Node insertChild;
        if (newChild instanceof XmlNode) {
            final XmlNode xmlNode = (XmlNode) newChild;
            insertChild = xmlNode.getNode();
        } else {
            insertChild = newChild;
        }

        return document.appendChild(insertChild);
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
        return document.cloneNode(deep);
    }

    /**
     * Creates an <code>Attr</code> of the given name.
     *
     * @param name
     *        The name of the attribute.
     * @return A new <code>Attr</code> object with the <code>nodeName</code>
     *         attribute set to <code>name</code>, and <code>localName</code>,
     *         <code>prefix</code>, and <code>namespaceURI</code> set to
     *         <code>null</code>. The value of the attribute is the empty
     *         string.
     */
    public final Attr createAttribute(final String name) {
        return document.createAttribute(name);
    }

    /**
     * Creates an attribute of the given qualified name and namespace URI.
     *
     * @param namespaceURI
     *        The namespace URI of the attribute to create.
     * @param qualifiedName
     *        The qualified name of the attribute to instantiate.
     * @return A new <code>Attr</code> object with the following attributes:
     *         <table border='1' summary="Description of attributes and values
     *         for the new Attr object">
     *         <tr>
     *         <th> Attribute</th>
     *         <th>Value</th>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.nodeName</code></td>
     *         <td valign='top'>qualifiedName</td>
     *         </tr>
     *         <tr>
     *         <td valign='top'> <code>Node.namespaceURI</code></td>
     *         <td valign='top'><code>namespaceURI</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'> <code>Node.prefix</code></td>
     *         <td valign='top'>prefix, extracted from
     *         <code>qualifiedName</code>, or <code>null</code> if there is
     *         no prefix</td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.localName</code></td>
     *         <td valign='top'>local name, extracted from
     *         <code>qualifiedName</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Attr.name</code></td>
     *         <td valign='top'> <code>qualifiedName</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.nodeValue</code></td>
     *         <td valign='top'>the empty string</td>
     *         </tr>
     *         </table>
     */
    public final Attr createAttributeNS(final String namespaceURI,
                                  final String qualifiedName) {
        return document.createAttributeNS(namespaceURI, qualifiedName);
    }

    /**
     * Creates a <code>CDATASection</code> node whose value is the specified
     * string.
     *
     * @param data
     *        The data for the <code>CDATASection</code> contents.
     * @return The new <code>CDATASection</code> object.
     */
    public final CDATASection createCDATASection(final String data) {
        return document.createCDATASection(data);
    }

    /**
     * Creates a <code>Comment</code> node given the specified string.
     *
     * @param data
     *        The data for the node.
     * @return The new <code>Comment</code> object.
     */
    public final Comment createComment(final String data) {
        return document.createComment(data);
    }

    /**
     * Creates an empty <code>DocumentFragment</code> object.
     *
     * @return A new <code>DocumentFragment</code>.
     */
    public final DocumentFragment createDocumentFragment() {
        return document.createDocumentFragment();
    }

    /**
     * Creates an element of the type specified.
     *
     * @param tagName
     *        The name of the element type to instantiate. For XML, this is
     *        case-sensitive. For HTML, the <code>tagName</code> parameter may
     *        be provided in any case, but it must be mapped to the canonical
     *        uppercase form by the DOM implementation.
     * @return A new <code>Element</code> object with the
     *         <code>nodeName</code> attribute set to <code>tagName</code>,
     *         and <code>localName</code>, <code>prefix</code>, and
     *         <code>namespaceURI</code> set to <code>null</code>.
     */
    public final Element createElement(final String tagName) {
        return document.createElement(tagName);
    }

    /**
     * Creates an element of the given qualified name and namespace URI.
     *
     * @param namespaceURI
     *        The namespace URI of the element to create.
     * @param qualifiedName
     *        The qualified name of the element type to instantiate.
     * @return A new <code>Element</code> object with the following
     *         attributes: <table border='1' summary="Description of attributes
     *         and values for the new Element object">
     *         <tr>
     *         <th>Attribute</th>
     *         <th>Value</th>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.nodeName</code></td>
     *         <td valign='top'> <code>qualifiedName</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.namespaceURI</code></td>
     *         <td valign='top'> <code>namespaceURI</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.prefix</code></td>
     *         <td valign='top'>prefix, extracted from
     *         <code>qualifiedName</code>, or <code>null</code> if there is
     *         no prefix</td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Node.localName</code></td>
     *         <td valign='top'>local name, extracted from
     *         <code>qualifiedName</code></td>
     *         </tr>
     *         <tr>
     *         <td valign='top'><code>Element.tagName</code></td>
     *         <td valign='top'> <code>qualifiedName</code></td>
     *         </tr>
     *         </table>
     */
    public final Element createElementNS(final String namespaceURI,
                                   final String qualifiedName) {
        return document.createElementNS(namespaceURI, qualifiedName);
    }

    /**
     * Creates an <code>EntityReference</code> object.
     *
     * @param name
     *        The name of the entity to reference.
     * @return The new <code>EntityReference</code> object.
     */
    public EntityReference createEntityReference(final String name) {
        return document.createEntityReference(name);
    }

    /**
     * Creates a <code>ProcessingInstruction</code> node given the specified
     * name and data strings.
     *
     * @param target
     *        The target part of the processing instruction.
     * @param data
     *        The data for the node.
     * @return The new <code>ProcessingInstruction</code> object.
     */
    public final ProcessingInstruction createProcessingInstruction(
            final String target, final String data) {
        return document.createProcessingInstruction(target, data);
    }

    /**
     * Creates a <code>Text</code> node given the specified string.
     *
     * @param data
     *        The data for the node.
     * @return The new <code>Text</code> object.
     */
    public final Text createTextNode(final String data) {
        return document.createTextNode(data);
    }

    /**
     * A <code>NamedNodeMap</code> containing the attributes of this node (if
     * it is an <code>Element</code>) or <code>null</code> otherwise.
     *
     * @return NamedNodeMap
     */
    public final NamedNodeMap getAttributes() {
        return document.getAttributes();
    }

    /**
     * A <code>NodeList</code> that contains all children of this node.
     *
     * @return NodeList
     */
    public final NodeList getChildNodes() {
        final NodeList children = document.getChildNodes();

        return getXmlNodeList(children);
    }

    /**
     * The Document Type Declaration (see <code>DocumentType</code>)
     * associated with this document.
     *
     * @return DocumentType
     */
    public DocumentType getDoctype() {
        return document.getDoctype();
    }

    /**
     * This is a convenience attribute that allows direct access to the child
     * node that is the root element of the document.
     *
     * @return Element
     */
    public final Element getDocumentElement() {
        return document.getDocumentElement();
    }

    /**
     * Returns the <code>Element</code> whose <code>ID</code> is given by
     * <code>elementId</code>.
     *
     * @param elementId
     *        The unique <code>id</code> value for an element.
     * @return The matching element.
     */
    public final Element getElementById(final String elementId) {
        return document.getElementById(elementId);
    }

    /**
     * Returns a <code>NodeList</code> of all the <code>Elements</code> with
     * a given tag name in the order in which they are encountered in a preorder
     * traversal of the <code>Document</code> tree.
     *
     * @param tagname
     *        The name of the tag to match on. The special value "*" matches all
     *        tags.
     * @return A new <code>NodeList</code> object containing all the matched
     *         <code>Elements</code>.
     */
    public final NodeList getElementsByTagName(final String tagname) {
        return getXmlNodeList(document.getElementsByTagName(tagname));
    }

    /**
     * Returns a <code>NodeList</code> of all the <code>Elements</code> with
     * a given local name and namespace URI in the order in which they are
     * encountered in a preorder traversal of the <code>Document</code> tree.
     *
     * @param namespaceURI
     *        The namespace URI of the elements to match on. The special value
     *        "*" matches all namespaces.
     * @param localName
     *        The local name of the elements to match on. The special value "*"
     *        matches all local names.
     * @return A new <code>NodeList</code> object containing all the matched
     *         <code>Elements</code>.
     */
    public final NodeList getElementsByTagNameNS(final String namespaceURI,
                                           final String localName) {
        return getXmlNodeList(document.getElementsByTagNameNS(namespaceURI,
                localName));
    }

    /**
     * Get the XmlNode object corresponding to the node.
     *
     * @param node -
     *        Node to convert to an XmlNode
     * @return XmlNode representing the node.
     */
    protected final Node getXmlNode(final Node node) {
        final XmlNodeFactory factory = getXmlNodefactory();

        return factory.getXmlNode(node);
    }

    /**
     * Get the XmlNodeList object corresponding to the nodelist.
     *
     * @param nodeList -
     *        Node to convert to an XmlNodeList
     * @return XmlNodeList representing the node.
     */
    protected abstract NodeList getXmlNodeList(final NodeList nodeList);

    /**
     * The first child of this node.
     *
     * @return Node
     */
    public final Node getFirstChild() {
        return getXmlNode(document.getFirstChild());
    }

    /**
     * The <code>DOMImplementation</code> object that handles this document.
     *
     * @return DOMImplementation
     */
    public final DOMImplementation getImplementation() {
        return document.getImplementation();
    }

    /**
     * The last child of this node.
     *
     * @return Node
     */
    public final Node getLastChild() {
        return getXmlNode(document.getLastChild());
    }

    /**
     * Returns the local part of the qualified name of this node.
     *
     * @return String
     */
    public final String getLocalName() {
        return document.getLocalName();
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @return String
     */
    public final String getNamespaceURI() {
        return document.getNamespaceURI();
    }

    /**
     * The node immediately following this node.
     *
     * @return Node
     */
    public final Node getNextSibling() {
        return getXmlNode(document.getNextSibling());
    }

    /**
     * The name of this node, depending on its type; see the table above.
     *
     * @return String
     */
    public final String getNodeName() {
        return document.getNodeName();
    }

    /**
     * A code representing the type of the underlying object, as defined above.
     *
     * @return short
     */
    public final short getNodeType() {
        return document.getNodeType();
    }

    /**
     * The value of this node, depending on its type; see the table above.
     *
     * @return String
     */
    public final String getNodeValue() {
        return document.getNodeValue();
    }

    /**
     * The <code>Document</code> object associated with this node.
     *
     * @return Document
     */
    public final Document getOwnerDocument() {
        return document.getOwnerDocument();
    }

    /**
     * The parent of this node.
     *
     * @return Node
     */
    public final Node getParentNode() {
        return getXmlNode(document.getParentNode());
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @return String
     */
    public final String getPrefix() {
        return document.getPrefix();
    }

    /**
     * The node immediately preceding this node.
     *
     * @return Node
     */
    public final Node getPreviousSibling() {
        return getXmlNode(document.getPreviousSibling());
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     *
     * @return <code>true</code> if this node has any attributes,
     *         <code>false</code> otherwise.
     */
    public final boolean hasAttributes() {
        return document.hasAttributes();
    }

    /**
     * Returns whether this node has any children.
     *
     * @return <code>true</code> if this node has any children,
     *         <code>false</code> otherwise.
     */
    public final boolean hasChildNodes() {
        return document.hasChildNodes();
    }

    /**
     * Imports a node from another document to this document.
     *
     * @param importedNode
     *        The node to import.
     * @param deep
     *        If <code>true</code>, recursively import the subtree under the
     *        specified node; if <code>false</code>, import only the node
     *        itself, as explained above. This has no effect on
     *        <code>Attr</code> , <code>EntityReference</code>, and
     *        <code>Notation</code> nodes.
     * @return The imported node that belongs to this <code>Document</code>.
     */
    public final Node importNode(final Node importedNode, final boolean deep) {
        return document.importNode(importedNode, deep);
    }

    /**
     * An attribute specifying the encoding used for this document at the time
     * of the parsing.
     *
     * @return String
     */
    public final String getInputEncoding() {
        return document.getInputEncoding();
    }

    /**
     * An attribute specifying, as part of the <a
     * href='http://www.w3.org/TR/2005/REC-xml-20050204#NT-XMLDecl'>XML
     * declaration</a>, the encoding of this document.
     *
     * @return String
     */
    public final String getXmlEncoding() {
        return document.getXmlEncoding();
    }

    /**
     * An attribute specifying, as part of the <a
     * href='http://www.w3.org/TR/2005/REC-xml-20050204#NT-XMLDecl'>XML
     * declaration</a>, whether this document is standalone.
     *
     * @return boolean
     */
    public final boolean getXmlStandalone() {
        return document.getXmlStandalone();
    }

    /**
     * An attribute specifying, as part of the <a
     * href='http://www.w3.org/TR/2005/REC-xml-20050204#NT-XMLDecl'>XML
     * declaration</a>, whether this document is standalone.
     *
     * @param xmlStandalone
     *        boolean
     */
    public final void setXmlStandalone(final boolean xmlStandalone) {
        document.setXmlStandalone(xmlStandalone);
    }

    /**
     * An attribute specifying, as part of the <a
     * href='http://www.w3.org/TR/2005/REC-xml-20050204#NT-XMLDecl'>XML
     * declaration</a>, the version number of this document.
     *
     * @return String
     */
    public final String getXmlVersion() {
        return document.getXmlVersion();
    }

    /**
     * An attribute specifying, as part of the <a
     * href='http://www.w3.org/TR/2005/REC-xml-20050204#NT-XMLDecl'>XML
     * declaration</a>, the version number of this document.
     *
     * @param xmlVersion
     *        String
     */
    public final void setXmlVersion(final String xmlVersion) {
        document.setXmlVersion(xmlVersion);
    }

    /**
     * An attribute specifying whether error checking is enforced or not.
     *
     * @return boolean
     */
    public final boolean getStrictErrorChecking() {
        return document.getStrictErrorChecking();
    }

    /**
     * An attribute specifying whether error checking is enforced or not.
     *
     * @param strictErrorChecking
     *        boolean
     */
    public final void setStrictErrorChecking(final boolean strictErrorChecking) {
        document.setStrictErrorChecking(strictErrorChecking);
    }

    /**
     * The location of the document or <code>null</code> if undefined or if
     * the <code>Document</code> was created using
     * <code>DOMImplementation.createDocument</code>.
     *
     * @return String
     */
    public final String getDocumentURI() {
        return document.getDocumentURI();
    }

    /**
     * The location of the document or <code>null</code> if undefined or if
     * the <code>Document</code> was created using
     * <code>DOMImplementation.createDocument</code>.
     *
     * @param documentURI
     *        String
     */
    public final void setDocumentURI(final String documentURI) {
        document.setDocumentURI(documentURI);
    }

    /**
     * Attempts to adopt a node from another document to this document.
     *
     * @param source
     *        The node to move into this document.
     * @return The adopted node, or <code>null</code> if this operation fails,
     *         such as when the source node comes from a different
     *         implementation.
     */
    public final Node adoptNode(final Node source) {
        return document.adoptNode(getRawNode(source));
    }

    /**
     * The configuration used when <code>Document.normalizeDocument()</code>
     * is invoked.
     *
     * @return DOMConfiguration
     */
    public final DOMConfiguration getDomConfig() {
        return document.getDomConfig();
    }

    /**
     * This method acts as if the document was going through a save and load
     * cycle, putting the document in a "normal" form.
     */
    public final void normalizeDocument() {
        document.normalizeDocument();
    }

    /**
     * Rename an existing node of type <code>ELEMENT_NODE</code> or
     * <code>ATTRIBUTE_NODE</code>.
     *
     * @param n
     *        The node to rename.
     * @param namespaceURI
     *        The new namespace URI.
     * @param qualifiedName
     *        The new qualified name.
     * @return The renamed node. This is either the specified node or the new
     *         node that was created to replace the specified node.
     */
    public final Node renameNode(final Node n, final String namespaceURI,
                           final String qualifiedName) {
        return document.renameNode(getRawNode(n), namespaceURI, qualifiedName);
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
        return document
                .insertBefore(getRawNode(newChild), getRawNode(refChild));
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
        return document.isSupported(feature, version);
    }

    /**
     * Puts all <code>Text</code> nodes in the full depth of the sub-tree
     * underneath this <code>Node</code>, including attribute nodes, into a
     * "normal" form where only structure (e.g., elements, comments, processing
     * instructions, CDATA sections, and entity references) separates
     * <code>Text</code> nodes, i.e., there are neither adjacent
     * <code>Text</code> nodes nor empty <code>Text</code> nodes.
     *
     */
    public final void normalize() {
        document.normalize();
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
        return document.removeChild(getRawNode(oldChild));
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
        return document
                .replaceChild(getRawNode(newChild), getRawNode(oldChild));
    }

    /**
     * The value of this node, depending on its type; see the table above.
     *
     * @param nodeValue
     *        String
     */
    public final void setNodeValue(final String nodeValue) {
        document.setNodeValue(nodeValue);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     *
     * @param prefix
     *        String
     */
    public final void setPrefix(final String prefix) {
        document.setPrefix(prefix);
    }

    /**
     * The absolute base URI of this node or <code>null</code> if the
     * implementation wasn't able to obtain an absolute URI.
     *
     * @return String
     */
    public final String getBaseURI() {
        return document.getBaseURI();
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
        return document.compareDocumentPosition(getRawNode(other));
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @return String
     */
    public final String getTextContent() {
        return document.getTextContent();
    }

    /**
     * This attribute returns the text content of this node and its descendants.
     *
     * @param textContent
     *        String
     */
    public final void setTextContent(final String textContent) {
        document.setTextContent(textContent);
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
        return document.isSameNode(getRawNode(other));
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
        return document.lookupPrefix(namespaceURI);
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
        return document.isDefaultNamespace(namespaceURI);
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
        return document.lookupNamespaceURI(prefix);
    }

    /**
     * Tests whether two nodes are equal.
     *
     * @param arg
     *        The node to compare equality with.
     * @return Returns <code>true</code> if the nodes are equal,
     *         <code>false</code> otherwise.
     */
    public final boolean isEqualNode(final Node arg) {
        return document.isEqualNode(arg);
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
        return document.getFeature(feature, version);
    }

    /**
     * Associate an object to a key on this node.
     *
     * @param key
     *        The key to associate the object to.
     * @param data
     *        The object to associate to the given key, or <code>null</code>
     *        to remove any existing association to that key.
     * @param handler
     *        The handler to associate to that key, or <code>null</code>.
     * @return Returns the <code>DOMUserData</code> previously associated to
     *         the given key on this node, or <code>null</code> if there was
     *         none.
     */
    public final Object setUserData(final String key, final Object data,
                              final UserDataHandler handler) {
        return document.setUserData(key, data, handler);
    }

    /**
     * Retrieves the object associated to a key on a this node.
     *
     * @param key
     *        The key the object is associated to.
     * @return Returns the <code>DOMUserData</code> associated to the given
     *         key on this node, or <code>null</code> if there was none.
     */
    public final Object getUserData(final String key) {
        return document.getUserData(key);
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
    public final void writeXml(final XmlWriter writer)
            throws IOException {
        writeChildrenXml(writer);
    }

    /**
     * Used to write any children of a node.
     *
     * @param writer
     *        XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    public final void writeChildrenXml(final XmlWriter writer)
            throws IOException {
        final NodeList children = getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if (node instanceof XmlWritable) {
                final XmlWritable writable = (XmlWritable) node;
                writable.writeXml(writer);
            }
        }
    }

    /**
     * Returns the contents of this object as an XML formatted string.
     *
     * @return XML representation of this object.
     *
     * @exception IOException
     *            Error writing to the writer.
     */
    public final String toXml()
            throws IOException {
        final XmlStringWriter writer = new XmlStringWriter(
                XmlWriter.DEFAULT_BLOCK_INDENT);

        writer.writeHeader();
        final DocumentType doctype = getDoctype();
        if (doctype != null) {
            writer.write(doctype.toString());
            writer.printIndent();
        }

        writeXml(writer);

        return writer.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Creates a representation as an XML string. If this is not possible
     * for some reason, the conventional <code>toString</code> creation
     * is used.
     *
     * @since 0.3
     */
    @Override
    public final String toString() {
        try {
            return toXml();
        } catch (java.io.IOException ioe) {
            return super.toString();
        }
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
        if (arg instanceof VoiceXmlNode) {
            final VoiceXmlNode voiceXmlNode = (VoiceXmlNode) arg;
            rawNode = voiceXmlNode.getNode();
        } else {
            rawNode = arg;
        }
        return rawNode;
    }
}
