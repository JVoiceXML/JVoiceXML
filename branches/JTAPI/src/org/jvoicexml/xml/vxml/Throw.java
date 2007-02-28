/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
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

import org.jvoicexml.xml.VoiceXmlNode;
import org.w3c.dom.Node;

/**
 * Throw an event.
 * <p>
 * The <code>&lt;throw&gt;</code> element throws an event. These can be the
 * pre-defined ones:
 * </p>
 * <p>
 * <code>
 * &lt;throw event="nomatch"/&gt;<br>
 * &lt;throw event="connection.disconnect.hangup"/&gt;
 * </code>
 * </p>
 * <p>
 * or application-defined events:
 * </p>
 * <p>
 * <code>
 * &lt;throw event="com.att.portal.machine"/&gt;
 * </code>
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Catch
 * @see org.jvoicexml.xml.vxml.Help
 * @see org.jvoicexml.xml.vxml.Error
 * @see org.jvoicexml.xml.vxml.Noinput
 * @see org.jvoicexml.xml.vxml.Nomatch
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Throw
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "throw";

    /**
     * The event being thrown.
     */
    public static final String ATTRIBUTE_EVENT = "event";

    /**
     * An ECMAScript expression evaluating to the name of the event being
     * thrown.
     */
    public static final String ATTRIBUTE_EVENTEXPR = "eventexpr";

    /**
     * A message string providing additional context about the event being
     * thrown. For the pre-defined events thrown by the platform, the value of
     * the message is platform-dependent.
     */
    public static final String ATTRIBUTE_MESSAGE = "message";

    /**
     * An ECMAScript expression evaluating to the message string.
     */
    public static final String ATTRIBUTE_MESSAGEEXPR = "messageexpr";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MESSAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MESSAGEEXPR);
    }

    /**
     * Construct a new throw object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Throw() {
        super(null);
    }

    /**
     * Construct a new throw object.
     * @param node The encapsulated node.
     */
    Throw(final Node node) {
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
    public VoiceXmlNode newInstance(final Node n) {
        return new Throw(n);
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
     * Retrieve the eventexpr attribute.
     * @return Value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public String getEventexpr() {
        return getAttribute(ATTRIBUTE_EVENTEXPR);
    }

    /**
     * Set the eventexpr attribute.
     * @param eventexpr Value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public void setEventexpr(final String eventexpr) {
        setAttribute(ATTRIBUTE_EVENTEXPR, eventexpr);
    }

    /**
     * Retrieve the message attribute.
     * @return Value of the message attribute.
     * @see #ATTRIBUTE_MESSAGE
     */
    public String getMessage() {
        return getAttribute(ATTRIBUTE_MESSAGE);
    }

    /**
     * Set the message attribute.
     * @param message Value of the message attribute.
     * @see #ATTRIBUTE_MESSAGE
     */
    public void setMessage(final String message) {
        setAttribute(ATTRIBUTE_MESSAGE, message);
    }

    /**
     * Retrieve the messageexpr attribute.
     * @return Value of the messageexpr attribute.
     * @see #ATTRIBUTE_MESSAGEEXPR
     */
    public String getMessageexpr() {
        return getAttribute(ATTRIBUTE_MESSAGEEXPR);
    }

    /**
     * Set the messageexpr attribute.
     * @param messageexpr Value of the messageexpr attribute.
     * @see #ATTRIBUTE_MESSAGEEXPR
     */
    public void setMessageexpr(final String messageexpr) {
        setAttribute(ATTRIBUTE_MESSAGEEXPR, messageexpr);
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
