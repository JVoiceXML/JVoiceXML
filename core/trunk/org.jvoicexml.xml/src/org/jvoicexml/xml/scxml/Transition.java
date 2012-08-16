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
 * Transitions between states are triggered by events and conditionalized via
 * guard conditions. They may contain executable content, which is executed
 * when the transition is taken.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Transition
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "transition";

    /**
     * A list of designators of events that trigger this transition.
     */
    private static final String ATTRIBUTE_EVENT = "event";

    /**
     * The guard condition for this transition.
     */
    private static final String ATTRIBUTE_COND = "cond";

    /**
     * The identifier(s) of the state or parallel region to transition to.
     */
    private static final String ATTRIBUTE_TARGET = "target";

    /**
     * Determines whether the source state is exited in transitions whose target
     * state is a descendant of the source state. 
     */
    private static final String ATTRIBUTE_TYPE = "type";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_EVENT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TARGET);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
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
     * Construct a new transition object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Transition() {
        super(null);
    }

    /**
     * Construct a new transition object.
     * @param node The encapsulated node.
     */
    Transition(final Node node) {
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
    private Transition(final Node n,
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
        return new Transition(n, factory);
    }

    /**
     * Retrieves the event attribute.
     *
     * @return value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public String getEvent() {
        return getAttribute(ATTRIBUTE_EVENT);
    }

    /**
     * Sets the event attribute.
     *
     * @param event Value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public void setEvent(final String event) {
        setAttribute(ATTRIBUTE_EVENT, event);
    }

    /**
     * Retrieves the cond attribute.
     *
     * @return value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public String getCond() {
        return getAttribute(ATTRIBUTE_COND);
    }

    /**
     * Sets the cond attribute.
     *
     * @param cond Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
    }

    /**
     * Retrieves the target attribute.
     *
     * @return value of the target attribute.
     * @see #ATTRIBUTE_TARGET
     */
    public String getTarget() {
        return getAttribute(ATTRIBUTE_TARGET);
    }

    /**
     * Sets the target attribute.
     *
     * @param target Value of the target attribute.
     * @see #ATTRIBUTE_TARGET
     */
    public void setTarget(final String target) {
        setAttribute(ATTRIBUTE_TARGET, target);
    }

    /**
     * Retrieves the type attribute.
     *
     * @return value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
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
