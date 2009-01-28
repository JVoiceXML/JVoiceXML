/*
 * File:    $RCSfile: Send.java,v $
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
import org.w3c.dom.Node;

/**
 * <code>&lt;send&gt;</code> is used to send messages containing events or
 * other information directly to another CCXML Interpreter other external
 * systems using an Event I/O Processor.
 *
 * The event target of <send/> is specified using the target and targettype
 * attributes. These attributes control how the platform should dispatch the
 * event to its final destination.
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
public final class Send
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "send";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the type of event being generated. The event type may include
     * alphanumeric characters and the "." (dot) character. The first character
     * may not be a dot or a digit. Event type names are case-insensitive. If
     * neither the data attribute or inline content is specified, an error.fetch
     * event will be thrown. If used in conjunction with the inline content, an
     * error.fetch will be thrown.
     */
    public static final String ATTRIBUTE_DATA = "data";

    /**
     * The character string returned is interpreted as a time interval. The send
     * tag will return immediately, but the event is not dispatched until the
     * delay interval elapses. Timers are useful for a wide variety of
     * programming tasks, and can be implemented using this attribute. Note: The
     * queue for sending events is maintained locally. Any events waiting to be
     * sent will be purged when the session that issued this request terminates.
     */
    public static final String ATTRIBUTE_DELAY = "delay";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform to configure the event processor. The meaning
     * of these hints is specific to the implementing platform and the event
     * processor.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * A list of zero or more whitespace separated CCXML variable names to be
     * included with the event. When an ECMAscript variable is included with the
     * event, its value is first converted into a string. If the variable is an
     * ECMAScript Object, the mechanism by which it is included is not currently
     * defined. Instead of including ECMAScript Objects directly, the
     * application developer may explicitly include the properties of an Object.
     * e.g. "date.month date.year". If used in conjunction with the inline
     * content, an error.fetch will be thrown.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive an internally
     * generated unique string identifier to be associated with the event being
     * sent. If this attribute is not specified, the event identifier is
     * dropped.
     */
    public static final String ATTRIBUTE_SENDID = "sendid";

    /**
     * An ECMAScript expression returning the target location of the event. The
     * target attribute specifies the unique identifier of the event target that
     * the Event I/O Processor should send the event to.
     */
    public static final String ATTRIBUTE_TARGET = "target";

    /**
     * An ECMAScript expression which returns a character string that specifies
     * the type of the Event I/O Processor that the event should be dispatched
     * to.
     */
    public static final String ATTRIBUTE_TARGETTYPE = "targettype";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_DATA);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DELAY);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SENDID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TARGET);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TARGETTYPE);
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
     * Construct a new send object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
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
        return new Send(n);
    }

    /**
     * Retrieve the data attribute.
     * @return Value of the data attribute.
     * @see #ATTRIBUTE_DATA
     */
    public String getData() {
        return getAttribute(ATTRIBUTE_DATA);
    }

    /**
     * Set the data attribute.
     * @param data Value of the data attribute.
     * @see #ATTRIBUTE_DATA
     */
    public void setData(final String data) {
        setAttribute(ATTRIBUTE_DATA, data);
    }

    /**
     * Retrieve the delay attribute.
     * @return Value of the delay attribute.
     * @see #ATTRIBUTE_DELAY
     */
    public String getDelay() {
        return getAttribute(ATTRIBUTE_DELAY);
    }

    /**
     * Set the delay attribute.
     * @param delay Value of the delay attribute.
     * @see #ATTRIBUTE_DELAY
     */
    public void setDelay(final String delay) {
        setAttribute(ATTRIBUTE_DELAY, delay);
    }

    /**
     * Retrieve the hints attribute.
     * @return Value of the hints attribute.
     * @see #ATTRIBUTE_HINTS
     */
    public String getHints() {
        return getAttribute(ATTRIBUTE_HINTS);
    }

    /**
     * Set the hints attribute.
     * @param hints Value of the hints attribute.
     * @see #ATTRIBUTE_HINTS
     */
    public void setHints(final String hints) {
        setAttribute(ATTRIBUTE_HINTS, hints);
    }

    /**
     * Retrieve the namelist attribute.
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Set the namelist attribute.
     * @param namelist Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieve the sendid attribute.
     * @return Value of the sendid attribute.
     * @see #ATTRIBUTE_SENDID
     */
    public String getSendid() {
        return getAttribute(ATTRIBUTE_SENDID);
    }

    /**
     * Set the sendid attribute.
     * @param sendid Value of the sendid attribute.
     * @see #ATTRIBUTE_SENDID
     */
    public void setSendid(final String sendid) {
        setAttribute(ATTRIBUTE_SENDID, sendid);
    }

    /**
     * Retrieve the target attribute.
     * @return Value of the target attribute.
     * @see #ATTRIBUTE_TARGET
     */
    public String getTarget() {
        return getAttribute(ATTRIBUTE_TARGET);
    }

    /**
     * Set the target attribute.
     * @param target Value of the target attribute.
     * @see #ATTRIBUTE_TARGET
     */
    public void setTarget(final String target) {
        setAttribute(ATTRIBUTE_TARGET, target);
    }

    /**
     * Retrieve the targettype attribute.
     * @return Value of the targettype attribute.
     * @see #ATTRIBUTE_TARGETTYPE
     */
    public String getTargettype() {
        return getAttribute(ATTRIBUTE_TARGETTYPE);
    }

    /**
     * Set the targettype attribute.
     * @param targettype Value of the targettype attribute.
     * @see #ATTRIBUTE_TARGETTYPE
     */
    public void setTargettype(final String targettype) {
        setAttribute(ATTRIBUTE_TARGETTYPE, targettype);
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
