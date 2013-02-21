/*
 * File:    $RCSfile: Transition.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/05/16 07:26:22 $
 * Author:  $Author: schnelle $
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
import org.w3c.dom.Node;

/**
 * The content of a <code>&lt;transition&gt;</code> specifies the actions to
 * be taken when it is selected. The <code>&lt;transition&gt;</code> are
 * examined by the EHIA in document order.
 *
 * @author Steve Doyle
 * @version $Revision: 1.5 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Transition
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "transition";

    /**
     * An ECMAScript expression which can be evaluated to true or false. If this
     * attribute is present, it must evaluate to true for the transition to be
     * selected. Note: You can not access the value of a the variable listed in
     * the name attribute as it will not do the assign until the transition has
     * been selected.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * This is a pattern that indicates a matching event type. Event types are
     * dot-separated strings of arbitrary length. The character * is a
     * wild-card, and will match zero or more characters of the processed
     * event's type name.
     */
    public static final String ATTRIBUTE_EVENT = "event";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will be set to the received
     * event object. The event object can only be referenced by this name while
     * executing the transition .
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * This indicates the current possible state(s) of the eventprocessor . More
     * than one state name may be specified, separated by white space.
     */
    public static final String ATTRIBUTE_STATE = "state";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EVENT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_STATE);
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

        CHILD_TAGS.add(Accept.TAG_NAME);
        CHILD_TAGS.add(Assign.TAG_NAME);
        CHILD_TAGS.add(Cancel.TAG_NAME);
        CHILD_TAGS.add(Createcall.TAG_NAME);
        CHILD_TAGS.add(Createccxml.TAG_NAME);
        CHILD_TAGS.add(Createconference.TAG_NAME);
        CHILD_TAGS.add(Destroyconference.TAG_NAME);
        CHILD_TAGS.add(Dialogprepare.TAG_NAME);
        CHILD_TAGS.add(Dialogstart.TAG_NAME);
        CHILD_TAGS.add(Dialogterminate.TAG_NAME);
        CHILD_TAGS.add(Disconnect.TAG_NAME);
        CHILD_TAGS.add(Exit.TAG_NAME);
        CHILD_TAGS.add(Fetch.TAG_NAME);
        CHILD_TAGS.add(Goto.TAG_NAME);
        CHILD_TAGS.add(If.TAG_NAME);
        CHILD_TAGS.add(Join.TAG_NAME);
        CHILD_TAGS.add(Log.TAG_NAME);
        CHILD_TAGS.add(Merge.TAG_NAME);
        CHILD_TAGS.add(Move.TAG_NAME);
        CHILD_TAGS.add(Redirect.TAG_NAME);
        CHILD_TAGS.add(Reject.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Send.TAG_NAME);
        CHILD_TAGS.add(Unjoin.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
    }

    /**
     * Construct a new transition object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
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
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * Create a new instance for the given node.
     * @param n The node to encapsulate.
     * @return The new instance.
     */
    public XmlNode newInstance(final Node n) {
        return new Transition(n);
    }

    /**
     * Retrieve the cond attribute.
     * @return Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public String getCond() {
        return getAttribute(ATTRIBUTE_COND);
    }

    /**
     * Set the cond attribute.
     * @param cond Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
    }

    /**
     * Retrieve the event attribute.
     * @return Value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public String getEvent() {
        return getAttribute(ATTRIBUTE_EVENT);
    }

    /**
     * Set the event attribute.
     * @param event Value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public void setEvent(final String event) {
        setAttribute(ATTRIBUTE_EVENT, event);
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
     * Retrieve the state attribute.
     * @return Value of the state attribute.
     * @see #ATTRIBUTE_STATE
     */
    public String getState() {
        return getAttribute(ATTRIBUTE_STATE);
    }

    /**
     * Set the state attribute.
     * @param state Value of the state attribute.
     * @see #ATTRIBUTE_STATE
     */
    public void setState(final String state) {
        setAttribute(ATTRIBUTE_STATE, state);
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
