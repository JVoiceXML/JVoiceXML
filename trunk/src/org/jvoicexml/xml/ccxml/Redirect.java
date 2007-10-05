/*
 * File:    $RCSfile: Redirect.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * When a CCXML document executes a <code>&lt;redirect&gt;</code> within the
 * <code>&lt;transition&gt;</code> block, this will cause the underlying
 * platform to signal the telephony  system to send the call to a specified
 * destination. The use of  redirect is only valid when a call is in the
 * ALERTING and  CONNECTED states.
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
public final class Redirect
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "redirect";

    /**
     * An ECMAScript expression which returns a string that is the
     * identifier of a Connection on which a call is active or on
     * which an incoming call is being signaled. This call will be
     * redirected. If the connectionid attribute is omitted, the
     * interpreter will redirect using the id indicated in the
     * current event being processed.
     * If the attribute value is invalid or there is no valid
     * default value, an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_CONNECTIONID = "connectionid";

    /**
     * An ECMAScript expression which returns a string that is the
     * target of the outbound telephone call. A platform must support
     * a telephone URI, as described in [RFC2806] or a SIP URI as
     * described in [RFC3261].
     */
    public static final String ATTRIBUTE_DEST = "dest";

    /**
     * The ECMAScript object returned contains information which may
     * be used by the implementing platform or passed to the network
     * redirecting the connection. This information may consist of
     * protocol-specific parameters.
     * Note: The meaning of these hints is specific to the
     * implementing platform and protocol.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * An ECMAScript expression which returns a string that is the
     * reason the call is being redirected.
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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DEST);
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
     * Construct a new redirect object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Redirect() {
        super(null);
    }

    /**
     * Construct a new redirect object.
     * @param node The encapsulated node.
     */
    Redirect(final Node node) {
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
    private Redirect(final Node n,
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
        return new Redirect(n, factory);
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
     * Retrieve the dest attribute.
     * @return Value of the dest attribute.
     * @see #ATTRIBUTE_DEST
     */
    public String getDest() {
        return getAttribute(ATTRIBUTE_DEST);
    }

    /**
     * Set the dest attribute.
     * @param dest Value of the dest attribute.
     * @see #ATTRIBUTE_DEST
     */
    public void setDest(final String dest) {
        setAttribute(ATTRIBUTE_DEST, dest);
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
