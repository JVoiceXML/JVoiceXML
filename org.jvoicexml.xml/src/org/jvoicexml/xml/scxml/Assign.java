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

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * the <code>&lt;assign&gt></code> element is used to modify the data model.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Assign
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "assign";

    /**
     * The location in the data model into which to insert the new value.
     */
    private static final String ATTRIBUTE_LOCATION = "location";

    /**
     * An expression returning the value to be assigned.
     */
    private static final String ATTRIBUTE_EXPR = "expr";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
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

        CHILD_TAGS.add(Text.TAG_NAME);
    }

    /**
     * Construct a new assign object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Assign() {
        super(null);
    }

    /**
     * Construct a new assign object.
     * @param node The encapsulated node.
     */
    Assign(final Node node) {
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
    private Assign(final Node n,
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
        return new Assign(n, factory);
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
     * Retrieves the expr attribute.
     *
     * @return value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public String getExpr() {
        return getAttribute(ATTRIBUTE_EXPR);
    }

    /**
     * Sets the expr attribute.
     *
     * @param expr Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
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
