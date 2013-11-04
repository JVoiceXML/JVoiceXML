/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 3556 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
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
 * Holds the representation of a state.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3556 $
 * @since 0.7.6
 */
public final class State
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "state";

    /**
     * The identifier for this state.
     */
    private static final String ATTRIBUTE_ID = "id";

    /**
     * The id of the default initial state (or states) for this state.
     */
    private static final String ATTRIBUTE_INITIAL = "inital";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_INITIAL);
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
        
        CHILD_TAGS.add(Datamodel.TAG_NAME);
        CHILD_TAGS.add(Final.TAG_NAME);
        CHILD_TAGS.add(History.TAG_NAME);
        CHILD_TAGS.add(Initial.TAG_NAME);
        CHILD_TAGS.add(Invoke.TAG_NAME);
        CHILD_TAGS.add(Onentry.TAG_NAME);
        CHILD_TAGS.add(Onexit.TAG_NAME);
        CHILD_TAGS.add(Parallel.TAG_NAME);
        CHILD_TAGS.add(State.TAG_NAME);
        CHILD_TAGS.add(Transition.TAG_NAME);
    }

    /**
     * Construct a new state object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public State() {
        super(null);
    }

    /**
     * Construct a new state object.
     * @param node The encapsulated node.
     */
    State(final Node node) {
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
    private State(final Node n,
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
        return new State(n, factory);
    }

    /**
     * Retrieve the initial attribute.
     *
     * @return value of the inital attribute.
     * @see #ATTRIBUTE_INITIAL
     */
    public String getInitial() {
        return getAttribute(ATTRIBUTE_INITIAL);
    }

    /**
     * Set the initial attribute.
     *
     * @param initial value of the initial attribute.
     * @see #ATTRIBUTE_INITIAL
     */
    public void setInitial(final String initial) {
        setAttribute(ATTRIBUTE_INITIAL, initial);
    }

    /**
     * Retrieve the id attribute.
     *
     * @return value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public String getId() {
        return getAttribute(ATTRIBUTE_ID);
    }

    /**
     * Sets the id attribute.
     *
     * @param id Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public void setId(final String id) {
        setAttribute(ATTRIBUTE_ID, id);
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
