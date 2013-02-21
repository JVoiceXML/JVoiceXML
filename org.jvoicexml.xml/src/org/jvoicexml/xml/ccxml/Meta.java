/*
 * File:    $RCSfile: Meta.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
 * The <code>&lt;metadata&gt;</code> and <code>&lt;meta&gt;</code> are
 * containers in which information about the document can be placed. The
 * <code>&lt;metadata&gt;</code> provides more general and powerful treatment
 * of metadata information than <code>&lt;meta&gt;</code> by using a metadata
 * schema.
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
public final class Meta
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "meta";

    /**
     * The value of the metadata property.
     */
    public static final String ATTRIBUTE_CONTENT = "content";

    /**
     * The NAME of an HTTP response header. This attribute has special
     * significance when documents are retrieved via HTTP. The http-equiv
     * content may be used in situations where the CCXML document author is
     * unable to configure HTTP header fields associated with their document on
     * the origin server. Either the name or the http-equiv attribute has to be
     * specified. If neither of them is specified, or if both are specified, an
     * error.fetch event will be thrown.
     */
    public static final String ATTRIBUTE_HTTP_EQUIV = "httpEquiv";

    /**
     * The NAME of the metadata property. The seeAlso property is used to
     * specify a resource that might provide additional metadata information
     * about the content. Either the name or the http-equiv attribute has to be
     * specified. If neither of them is specified, or if both are specified, an
     * error.fetch event will be thrown.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONTENT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HTTP_EQUIV);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
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

    }

    /**
     * Construct a new meta object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Meta() {
        super(null);
    }

    /**
     * Construct a new meta object.
     * @param node The encapsulated node.
     */
    Meta(final Node node) {
        super(node);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Meta(final Node n,
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
        return new Meta(n, factory);
    }

    /**
     * Retrieve the content attribute.
     * @return Value of the content attribute.
     * @see #ATTRIBUTE_CONTENT
     */
    public String getContent() {
        return getAttribute(ATTRIBUTE_CONTENT);
    }

    /**
     * Set the content attribute.
     * @param content Value of the content attribute.
     * @see #ATTRIBUTE_CONTENT
     */
    public void setContent(final String content) {
        setAttribute(ATTRIBUTE_CONTENT, content);
    }

    /**
     * Retrieve the httpEquiv attribute.
     * @return Value of the httpEquiv attribute.
     * @see #ATTRIBUTE_HTTP_EQUIV
     */
    public String getHttpEquiv() {
        return getAttribute(ATTRIBUTE_HTTP_EQUIV);
    }

    /**
     * Set the httpEquiv attribute.
     * @param httpEquiv Value of the httpEquiv attribute.
     * @see #ATTRIBUTE_HTTP_EQUIV
     */
    public void setHttpEquiv(final String httpEquiv) {
        setAttribute(ATTRIBUTE_HTTP_EQUIV, httpEquiv);
    }

    /**
     * Retrieve the name attribute.
     * @return Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public String getName() {
        return getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Set the name attribute.
     * @param name Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public void setName(final String name) {
        setAttribute(ATTRIBUTE_NAME, name);
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
