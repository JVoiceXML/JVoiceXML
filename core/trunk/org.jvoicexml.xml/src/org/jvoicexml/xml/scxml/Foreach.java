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
 * The <code>&lt;foreach&gt;</code> element allows an SCXML application to
 * iterate through a collection in the data model and to execute the actions
 * contained within it for each item in the collection.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Foreach
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "foreach";

    /**
     * The <code>&lt;foreach&gt;</code> element will iterate over a shallow copy
     * of this collection.
     */
    private static final String ATTRIBUTE_ARRAY = "array";

    /**
     * A variable that stores a different item of the collection in each
     * iteration of the loop.
     */
    private static final String ATTRIBUTE_ITEM = "item";

    /**
     * A variable that stores the current iteration index upon each iteration of
     * the foreach loop.
     */
    private static final String ATTRIBUTE_INDEX = "index";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ARRAY);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ITEM);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_INDEX);
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
        CHILD_TAGS.add(Cancel.TAG_NAME);
        CHILD_TAGS.add(Else.TAG_NAME);
        CHILD_TAGS.add(Elseif.TAG_NAME);
        CHILD_TAGS.add(Foreach.TAG_NAME);
        CHILD_TAGS.add(If.TAG_NAME);
        CHILD_TAGS.add(Log.TAG_NAME);
        CHILD_TAGS.add(Raise.TAG_NAME);
        CHILD_TAGS.add(Send.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Validate.TAG_NAME);
    }

    /**
     * Construct a new foreach object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Foreach() {
        super(null);
    }

    /**
     * Construct a new foreach object.
     * @param node The encapsulated node.
     */
    Foreach(final Node node) {
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
    private Foreach(final Node n,
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
        return new Foreach(n, factory);
    }

    /**
     * Retrieves the array attribute.
     *
     * @return value of the array attribute.
     * @see #ATTRIBUTE_ARRAY
     */
    public String getArray() {
        return getAttribute(ATTRIBUTE_ARRAY);
    }

    /**
     * Sets the cond attribute.
     *
     * @param array value of the array attribute.
     * @see #ATTRIBUTE_ARRAY
     */
    public void setArray(final String array) {
        setAttribute(ATTRIBUTE_ARRAY, array);
    }

    /**
     * Retrieves the item attribute.
     *
     * @return value of the item attribute.
     * @see #ATTRIBUTE_ITEM
     */
    public String getItem() {
        return getAttribute(ATTRIBUTE_ITEM);
    }

    /**
     * Sets the item attribute.
     *
     * @param item value of the item attribute.
     * @see #ATTRIBUTE_ITEM
     */
    public void setItem(final String item) {
        setAttribute(ATTRIBUTE_ITEM, item);
    }

    /**
     * Retrieves the index attribute.
     *
     * @return value of the index attribute.
     * @see #ATTRIBUTE_INDEX
     */
    public String getIndex() {
        return getAttribute(ATTRIBUTE_INDEX);
    }

    /**
     * Sets the item attribute.
     *
     * @param index value of the index attribute.
     * @see #ATTRIBUTE_INDEX
     */
    public void setIndex(final String index) {
        setAttribute(ATTRIBUTE_INDEX, index);
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
