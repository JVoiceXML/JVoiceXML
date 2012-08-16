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
 * <code>&lt;send&gt;</code> s used to send events and data to external systems,
 * including external SCXML Interpreters, or to raise events in the current
 * SCXML session.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Send
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "send";

    /**
     * A string indicating the name of message being generated.
     */
    private static final String ATTRIBUTE_EVENT = "event";

    /**
     * A dynamic alternative to <code>event</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;send&gt;</code>> element is evaluated and treat the result as
     * if it had been entered as the value of <code>event</code>.
     */
    private static final String ATTRIBUTE_EVENTEXPR = "eventexpr";

    /**
     * The unique identifier of the message target that the platform should send
     * the event to.
     */
    private static final String ATTRIBUTE_TARGET = "target";

    /**
     * A dynamic alternative to <code>target</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;send&gt;</code> element is evaluated and treat the result as if
     * it had been entered as the value of <code>target</code>.
     */
    private static final String ATTRIBUTE_TARGETEXPR = "targetexpr";

    /**
     * The URI that identifies the transport mechanism for the message.
     */
    private static final String ATTRIBUTE_TYPE = "type";

    /**
     * A dynamic alternative to <code>type</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;send&gt;</code> element is evaluated and treat the result as if
     * it had been entered as the value of <code>type</code>.
     */
    private static final String ATTRIBUTE_TYPEEXPR = "typeexpr";

    /**
     * A string literal to be used as the identifier for this instance of
     * <code>&lt;send&gt;</code>.
     */
    private static final String ATTRIBUTE_ID = "id";

    /**
     * Any location expression evaluating to a data model location in which a
     * system-generated id can be stored.
     */
    private static final String ATTRIBUTE_IDLOCATION = "idlocation";

    /**
     * Indicates how long the processor should wait before dispatching the
     * message.
     */
    private static final String ATTRIBUTE_DELAY = "delay";

    /**
     * A dynamic alternative to <code>delay</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;send&gt;</code> element is evaluated and treat the result as if
     * it had been entered as the value of <code>delay</code>.
     */
    private static final String ATTRIBUTE_DELAYEXPR = "delayexpr";

    /**
     * A space-separated list of one or more data model locations to be included
     * with the message.
     */
    private static final String ATTRIBUTE_NAMELIST = "namelist";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EVENTEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TARGET);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TARGETEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPEEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_IDLOCATION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DELAY);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DELAYEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
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

        CHILD_TAGS.add(Content.TAG_NAME);
        CHILD_TAGS.add(Param.TAG_NAME);
    }

    /**
     * Construct a new send object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Send() {
        super(null);
    }

    /**
     * Construct a new send object.
     * @param node The encapsulated node.
     */
    Send(final Node node) {
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
    private Send(final Node n,
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
        return new Send(n, factory);
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
     * Retrieves the eventexpr attribute.
     *
     * @return value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public String getEventExpr() {
        return getAttribute(ATTRIBUTE_EVENTEXPR);
    }

    /**
     * Sets the eventexpr attribute.
     *
     * @param eventexpr Value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public void setEventExpr(final String eventexpr) {
        setAttribute(ATTRIBUTE_EVENTEXPR, eventexpr);
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
     * Retrieves the targetexpr attribute.
     *
     * @return value of the targetexpr attribute.
     * @see #ATTRIBUTE_TARGETEXPR
     */
    public String getTargetexpr() {
        return getAttribute(ATTRIBUTE_TARGETEXPR);
    }

    /**
     * Sets the targetexpr attribute.
     *
     * @param targetexpr Value of the targetexpr attribute.
     * @see #ATTRIBUTE_TARGETEXPR
     */
    public void setTargetexpr(final String targetexpr) {
        setAttribute(ATTRIBUTE_TARGETEXPR, targetexpr);
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
     * @param type Value of the target attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * Retrieves the typeexpr attribute.
     *
     * @return value of the typeexpr attribute.
     * @see #ATTRIBUTE_TYPEEXPR
     */
    public String getTypeexpr() {
        return getAttribute(ATTRIBUTE_TYPEEXPR);
    }

    /**
     * Sets the typeexpr attribute.
     *
     * @param typeexpr Value of the target attribute.
     * @see #ATTRIBUTE_TYPEEXPR
     */
    public void setTypeexpr(final String typeexpr) {
        setAttribute(ATTRIBUTE_TYPEEXPR, typeexpr);
    }

    /**
     * Retrieves the id attribute.
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
     * Retrieves the idlocation attribute.
     *
     * @return value of the idlocation attribute.
     * @see #ATTRIBUTE_IDLOCATION
     */
    public String getIdlocation() {
        return getAttribute(ATTRIBUTE_IDLOCATION);
    }

    /**
     * Sets the idlocation attribute.
     *
     * @param idlocation Value of the idlocation attribute.
     * @see #ATTRIBUTE_IDLOCATION
     */
    public void setIdlocation(final String idlocation) {
        setAttribute(ATTRIBUTE_IDLOCATION, idlocation);
    }

    /**
     * Retrieves the delay attribute.
     *
     * @return value of the delay attribute.
     * @see #ATTRIBUTE_DELAY
     */
    public String getDelay() {
        return getAttribute(ATTRIBUTE_DELAY);
    }

    /**
     * Sets the delay attribute.
     *
     * @param delay Value of the delay attribute.
     * @see #ATTRIBUTE_DELAY
     */
    public void setDelay(final String delay) {
        setAttribute(ATTRIBUTE_DELAY, delay);
    }

    /**
     * Retrieves the delayexpr attribute.
     *
     * @return value of the delayexpr attribute.
     * @see #ATTRIBUTE_DELAYEXPR
     */
    public String getDelayexpr() {
        return getAttribute(ATTRIBUTE_DELAYEXPR);
    }

    /**
     * Sets the delayexpr attribute.
     *
     * @param delayexpr Value of the delayexpr attribute.
     * @see #ATTRIBUTE_DELAYEXPR
     */
    public void setDelayexpr(final String delayexpr) {
        setAttribute(ATTRIBUTE_DELAYEXPR, delayexpr);
    }

    /**
     * Retrieves the namelist attribute.
     *
     * @return value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Sets the namelist attribute.
     *
     * @param namelist Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
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
