/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.scxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The <code>&lt;validate&gt></code> element causes the datamodel to be
 * validated.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Validate
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "validate";

    /**
     * The location of the subtree to validate. If it is not present, the entire
     * datamodel is validated.
     */
    private static final String ATTRIBUTE_LOCATION = "location";

    /**
     * The location of the schema to use for validation. If this attribute is
     * not present, the schema specified in the top-level
     * <code>&lt;datamodel&gt;</code> is used.
     */
    private static final String ATTRIBUTE_SCHEMA = "schema";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_LOCATION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SCHEMA);
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
     * Construct a new validate object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Validate() {
        super(null);
    }

    /**
     * Construct a new validate object.
     * @param node The encapsulated node.
     */
    Validate(final Node node) {
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
    private Validate(final Node n,
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
        return new Validate(n, factory);
    }

    /**
     * Retrieves the location attribute.
     *
     * @return value of the location attribute.
     * @see #ATTRIBUTE_LOCATION
     */
    public String getLocation() {
        return getAttribute(ATTRIBUTE_LOCATION);
    }

    /**
     * Sets the location attribute.
     *
     * @param location Value of the location attribute.
     * @see #ATTRIBUTE_LOCATION
     */
    public void setLocation(final String location) {
        setAttribute(ATTRIBUTE_LOCATION, location);
    }

    /**
     * Retrieves the schema attribute.
     *
     * @return value of the schema attribute.
     * @see #ATTRIBUTE_SCHEMA
     */
    public String getSchema() {
        return getAttribute(ATTRIBUTE_SCHEMA);
    }

    /**
     * Sets the schema attribute.
     *
     * @param expr Value of the schema attribute.
     * @see #ATTRIBUTE_SCHEMA
     */
    public void setSchema(final String expr) {
        setAttribute(ATTRIBUTE_SCHEMA, expr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
