/*
 * File:    $RCSfile: Disconnect.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * A CCXML document MAY disconnect a call leg on a Connection by using
 * <code>&lt;disconnect&gt;</code> . The underlying platform will send the
 * appropriate protocol messages to perform the disconnect, and send an
 * asynchronous event to the CCXML document when the disconnect operation
 * completes.
 *
 * If the connection had been bridged when the <code>&lt;disconnect&gt;</code>
 * request was made, the platform will tear down all bridges to the connection
 * and send a conference.unjoined to the CCXML document once the media paths
 * have been freed.
 *
 * Note the platform is not required to generate the connection.disconnected or
 * conference.unjoined in any particular order.
 *
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
public final class Disconnect
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "disconnect";

    /**
     * An ECMAScript expression which returns a string that is the id of a call
     * leg that should be disconnected. If the connectionid attribute is
     * omitted, the interpreter will disconnect using the id indicated in the
     * current event being processed. If the attribute value is invalid or there
     * is no valid default value, an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_CONNECTIONID = "connectionid";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network disconnecting the
     * connection. This information may consist of protocol-specific parameters.
     * Note: The meaning of these hints is specific to the implementing platform
     * and protocol.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * An ECMAScript expression which returns a string that is the reason the
     * call is being disconnected.
     */
    public static final String ATTRIBUTE_REASON = "reason";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTIONID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_REASON);
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
     * Construct a new disconnect object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Disconnect() {
        super(null);
    }

    /**
     * Construct a new disconnect object.
     * @param node The encapsulated node.
     */
    Disconnect(final Node node) {
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
        return new Disconnect(n);
    }

    /**
     * Retrieve the connectionid attribute.
     * @return Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public String getConnectionid() {
        return getAttribute(ATTRIBUTE_CONNECTIONID);
    }

    /**
     * Set the connectionid attribute.
     * @param connectionid Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public void setConnectionid(final String connectionid) {
        setAttribute(ATTRIBUTE_CONNECTIONID, connectionid);
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
     * Retrieve the reason attribute.
     * @return Value of the reason attribute.
     * @see #ATTRIBUTE_REASON
     */
    public String getReason() {
        return getAttribute(ATTRIBUTE_REASON);
    }

    /**
     * Set the reason attribute.
     * @param reason Value of the reason attribute.
     * @see #ATTRIBUTE_REASON
     */
    public void setReason(final String reason) {
        setAttribute(ATTRIBUTE_REASON, reason);
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
