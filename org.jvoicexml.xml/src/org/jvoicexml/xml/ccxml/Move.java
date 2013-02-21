/*
 * File:    $RCSfile: Move.java,v $
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
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * <code>&lt;move&gt;</code> is used to move an event source (such as a
 * Connection object) to an executing CCXML session. When an event source is
 * moved to a session, events originating from that source will be delivered to
 * that session's currently executing CCXML document. The event OR the source
 * attribute MUST be specified. If neither attribute is specified or both
 * attributes are specified, an error.fetch event will be thrown.
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
public final class Move
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "move";

    /**
     * The event source from which the event object originated, if any, will be
     * moved to the target session. The event will also be sent to the target
     * session to provide a notification. Either the source or the event
     * attribute has to be specified. If neither of them is specified, or if
     * both are specified, an error.fetch event will be thrown.
     */
    public static final String ATTRIBUTE_EVENT = "event";

    /**
     * An ECMAScript expression that identifies the session to which the event
     * source will be moved.
     */
    public static final String ATTRIBUTE_SESSIONID = "sessionid";

    /**
     * An ECMAScript expression which returns a connectionID or dialogID. The
     * event source associated with this identifier will be moved to the target
     * session. Either the source or the event attribute has to be specified. If
     * neither of them is specified, or if both are specified, an error.fetch
     * event will be thrown. If the attribute value is invalid, an
     * error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_SOURCE = "source";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SESSIONID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SOURCE);
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
     * Construct a new move object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Move() {
        super(null);
    }

    /**
     * Construct a new move object.
     * @param node The encapsulated node.
     */
    Move(final Node node) {
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
    private Move(final Node n,
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
        return new Move(n, factory);
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
     * Retrieve the sessionid attribute.
     * @return Value of the sessionid attribute.
     * @see #ATTRIBUTE_SESSIONID
     */
    public String getSessionid() {
        return getAttribute(ATTRIBUTE_SESSIONID);
    }

    /**
     * Set the sessionid attribute.
     * @param sessionid Value of the sessionid attribute.
     * @see #ATTRIBUTE_SESSIONID
     */
    public void setSessionid(final String sessionid) {
        setAttribute(ATTRIBUTE_SESSIONID, sessionid);
    }

    /**
     * Retrieve the source attribute.
     * @return Value of the source attribute.
     * @see #ATTRIBUTE_SOURCE
     */
    public String getSource() {
        return getAttribute(ATTRIBUTE_SOURCE);
    }

    /**
     * Set the source attribute.
     * @param source Value of the source attribute.
     * @see #ATTRIBUTE_SOURCE
     */
    public void setSource(final String source) {
        setAttribute(ATTRIBUTE_SOURCE, source);
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
