/*
 * File:    $RCSfile: Merge.java,v $
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
 * The <code>&lt;merge&gt;</code> element allows two calls being handled by a
 * particular CCXML session to be merged together at the network level, if
 * supported by the underlying network and CCXML platform.
 *
 * If successful, the two referenced calls will be merged at the network level,
 * and the connections to the CCXML platform associated with those calls will be
 * terminated. A connection.merged event will be generated on each of the two
 * calls affected by a merge. If the merge fails, then a single error.merge
 * event will be thrown which identifies both of the connections against which
 * the merge was performed.
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
public final class Merge
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "merge";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * the first connection that is to be merged. The order (connectionid1 vs.
     * connectionid2) of the Connections does not matter. If the attribute value
     * is invalid an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_CONNECTIONID1 = "connectionid1";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * the second connection that is to be merged. If the attribute value is
     * invalid an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_CONNECTIONID2 = "connectionid2";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network when merging the two
     * connections. This information MAY consist of protocol-specific
     * parameters. Note: The meaning of these hints is specific to the
     * implementing platform and the underlying protocol.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTIONID1);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTIONID2);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
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
     * Construct a new merge object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Merge() {
        super(null);
    }

    /**
     * Construct a new merge object.
     * @param node The encapsulated node.
     */
    Merge(final Node node) {
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
        return new Merge(n);
    }

    /**
     * Retrieve the connectionid1 attribute.
     * @return Value of the connectionid1 attribute.
     * @see #ATTRIBUTE_CONNECTIONID1
     */
    public String getConnectionid1() {
        return getAttribute(ATTRIBUTE_CONNECTIONID1);
    }

    /**
     * Set the connectionid1 attribute.
     * @param connectionid1 Value of the connectionid1 attribute.
     * @see #ATTRIBUTE_CONNECTIONID1
     */
    public void setConnectionid1(final String connectionid1) {
        setAttribute(ATTRIBUTE_CONNECTIONID1, connectionid1);
    }

    /**
     * Retrieve the connectionid2 attribute.
     * @return Value of the connectionid2 attribute.
     * @see #ATTRIBUTE_CONNECTIONID2
     */
    public String getConnectionid2() {
        return getAttribute(ATTRIBUTE_CONNECTIONID2);
    }

    /**
     * Set the connectionid2 attribute.
     * @param connectionid2 Value of the connectionid2 attribute.
     * @see #ATTRIBUTE_CONNECTIONID2
     */
    public void setConnectionid2(final String connectionid2) {
        setAttribute(ATTRIBUTE_CONNECTIONID2, connectionid2);
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
