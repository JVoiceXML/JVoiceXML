/*
 * File:    $RCSfile: Ccxml.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * This is the parent element of a CCXML document and encloses the entire CCXML
 * script in a document. When a <code>&lt;ccxml&gt;</code> is executed, its
 * child elements are collected logically together at the beginning of the
 * document and executed in document order before the target
 * <code>&lt;eventprocessor&gt;</code>. This is called document
 * initialization.
 *
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Ccxml
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "ccxml";

    /**
     * Default cc xml version number.
     * @see #ATTRIBUTE_VERSION
     */
    public static final String DEFAULT_VERSION = "1.0";

    /**
     * The version of CCXML of this document (required). The current
     * version number is <code>DEFAULT_VERSION</code>.
     * @see #DEFAULT_VERSION
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * Default namespace.
     * @see #ATTRIBUTE_XMLNS
     */
    public static final String DEFAULT_XMLNS =
            "http://www.w3.org/2002/09/ccxml";

    /**
     * The designated namespace for CCXXML (required). The namespace for
     * VoiceXML is defined to be <code>DEFAULT_XMLNS</code>
     */
    public static final String ATTRIBUTE_XMLNS = "xmlns";

    /**
     * The base URI for this document as defined in
     * <a href="http://www.w3.org/TR/2001/REC-xml-20001006"><em>XML Base</em>,
     * J. Marsh, editor, W3C Recommendation, June 2001</a>. As in
     * <a href="http://www.w3.org/TR/1999/REC-html401-19991224">
     * <em>HTML 4.01 Specification</em>, Dave Ragget et. al. W3C
     * Recommendation, December 1999</a>, a URI which all relative references
     * within the docment take as their base.
     */
    public static final String ATTRIBUTE_XML_BASE = "xmlBase";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XMLNS);
    }

    /**
     * Valid child tags for this node.
     */
    private static final Set<String> CHILD_TAGS;

    /**
     * Set the valid child tags for this node.
     */
    static {
        CHILD_TAGS = new java.util.HashSet<String>();

        CHILD_TAGS.add(Assign.TAG_NAME);
        CHILD_TAGS.add(Eventprocessor.TAG_NAME);
        CHILD_TAGS.add(Meta.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
    }

    /**
     * Construct a new ccxml object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Ccxml() {
        super(null);
    }

    /**
     * Construct a new ccxml object.
     * @param node The encapsulated node.
     */
    Ccxml(final Node node) {
        super(node);

        // Set the default attributes.
        setAttribute(ATTRIBUTE_XMLNS, DEFAULT_XMLNS);
        setAttribute(ATTRIBUTE_VERSION, DEFAULT_VERSION);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Ccxml(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new Ccxml(n, factory);
    }

    /**
     * Retrieve the version attribute.
     * @return Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public String getVersion() {
        return getAttribute(ATTRIBUTE_VERSION);
    }

    /**
     * Set the version attribute.
     * @param version Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public void setVersion(final String version) {
        setAttribute(ATTRIBUTE_VERSION, version);
    }

    /**
     * Retrieve the xmlBase attribute.
     * @return Value of the xmlBase attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public String getXmlBase() {
        return getAttribute(ATTRIBUTE_XML_BASE);
    }

    /**
     * Set the xmlBase attribute.
     * @param xmlBase Value of the xmlBase attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
