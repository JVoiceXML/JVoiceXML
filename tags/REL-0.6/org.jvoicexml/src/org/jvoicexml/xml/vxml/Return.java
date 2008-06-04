/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * <code>&lt;return&gt;</code> ends execution of a subdialog and returns
 * control and data to a calling dialog.
 *
 * @see org.jvoicexml.xml.vxml.Exit
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Return
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "return";

    /**
     * Return, then throw this event.
     */
    public static final String ATTRIBUTE_EVENT = "event";

    /**
     * Return, then throw the event to which this ECMAScript expression
     * evaluates.
     */
    public static final String ATTRIBUTE_EVENTEXPR = "eventexpr";

    /**
     * A message string providing additional context about the event being
     * thrown. The message is available as the value of a variable within the
     * scope of the catch element.
     */
    public static final String ATTRIBUTE_MESSAGE = "message";

    /**
     * An ECMAScript expression evaluating to the message string.
     */
    public static final String ATTRIBUTE_MESSAGEEXPR = "messageexpr";

    /**
     * Variable names to be returned to calling dialog. The default is to
     * return no variables; this means the caller will receive an empty
     * ECMAScript object. If an undeclared variable is referenced in the
     * namelist, then an error.semantic is thrown.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
    }

    /**
     * Construct a new return object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Return() {
        super(null);
    }

    /**
     * Construct a new return object.
     * @param node The encapsulated node.
     */
    Return(final Node node) {
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
    private Return(final Node n,
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
        return new Return(n, factory);
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
     * Retrieve the namelist attribute.
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Retrieve the namelist attribute as a list object.
     *
     * @return Value of the namelist attribute as a list.
     *
     * @see #getNamelist()
     *
     * @since 0.3
     */
    public TokenList getNameListObject() {
        final String namelist = getNamelist();

        return new TokenList(namelist);
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
