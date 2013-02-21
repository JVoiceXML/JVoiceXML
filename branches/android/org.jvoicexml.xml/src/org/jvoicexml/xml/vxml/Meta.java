/*
 * File:    $RCSfile: Meta.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Define a metadata item as a name/value pair.
 * <p>
 * The <code>&lt;meta&gt;</code> element specifies meta information. There are
 * two types of <code>&lt;meta&gt;</code>.
 * </p>
 * <p>
 * The first type specifies a metadata property of the document as a whole and
 * is expressed by the pair of attributes, name and content.
 * </p>
 * <p>
 * The second type of <code>&lt;meta&gt;</code> specifies HTTP response
 * headers and is expressed by the pair of attributes http-equiv and content.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Metadata
 *
 * @author Steve Doyle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Meta
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "meta";

    /**
     * The name of the metadata property.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The value of the metadata property.
     */
    public static final String ATTRIBUTE_CONTENT = "content";

    /**
     * The name of an HTTP response header.
     */
    public static final String ATTRIBUTE_HTTP_EQUIV = "http-equiv";

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
     * Retrieve the http-equiv attribute.
     * @return Value of the http-equiv attribute.
     * @see #ATTRIBUTE_HTTP_EQUIV
     */
    public String getHttpEquiv() {
        return getAttribute(ATTRIBUTE_HTTP_EQUIV);
    }

    /**
     * Set the http-equiv attribute.
     * @param httpEquiv Value of the http-equiv attribute.
     * @see #ATTRIBUTE_HTTP_EQUIV
     */
    public void setHttpEquiv(final String httpEquiv) {
        setAttribute(ATTRIBUTE_HTTP_EQUIV, httpEquiv);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
