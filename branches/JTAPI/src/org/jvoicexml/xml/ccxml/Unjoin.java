/*
 * File:    $RCSfile: Unjoin.java,v $
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
import org.w3c.dom.Node;

/**
 * A CCXML document can attempt to tear down a bridge between two existing
 * connections, conferences, or dialogs using <code>&lt;unjoin&gt;</code>.
 * This element will instruct the implementation to tear down the bridge between
 * two connections/conferences/dialogs specified using the id1 and id2
 * attributes. The successful execution of<code>&lt;unjoin&gt;</code> will
 * result in the generation of a conference.unjoined event. If for any reason
 * the implementation is unable to terminate the bridge between the specified
 * connections/conferences/dialogs, or if no such bridge exists, it MUST fail
 * with a error.conference.unjoin event.
 *
 * Unjoining two objects that are owned by separate CCXML sessions will result
 * in the generation of a conference.unjoined to each of the sessions. However
 * if the implementation is unable to join the objects an
 * error.conference.unjoin will only be sent to the session issued the
 * <code>&lt;unjoin&gt;</code>.
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
public final class Unjoin
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "unjoin";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network when the two specified
     * Connections, Dialogs or Conferences (id1 and id2) are unjoined. This
     * information may consist of protocol-specific parameters. Note: The
     * meaning of these hints is specific to the implementing platform and the
     * underlying protocol.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * a Connection, Dialog or Conference. If the attribute value is invalid an
     * error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_ID1 = "id1";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * a Connection, Dialog or Conference. All media streams between the two
     * specified Connections, Dialogs or Conferences (id1 and id2 ) will be torn
     * down. If the attribute value is invalid an error.semantic event will be
     * thrown.
     */
    public static final String ATTRIBUTE_ID2 = "id2";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID1);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID2);
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
     * Construct a new unjoin object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Unjoin() {
        super(null);
    }

    /**
     * Construct a new unjoin object.
     * @param node The encapsulated node.
     */
    Unjoin(final Node node) {
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
        return new Unjoin(n);
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
     * Retrieve the id1 attribute.
     * @return Value of the id1 attribute.
     * @see #ATTRIBUTE_ID1
     */
    public String getId1() {
        return getAttribute(ATTRIBUTE_ID1);
    }

    /**
     * Set the id1 attribute.
     * @param id1 Value of the id1 attribute.
     * @see #ATTRIBUTE_ID1
     */
    public void setId1(final String id1) {
        setAttribute(ATTRIBUTE_ID1, id1);
    }

    /**
     * Retrieve the id2 attribute.
     * @return Value of the id2 attribute.
     * @see #ATTRIBUTE_ID2
     */
    public String getId2() {
        return getAttribute(ATTRIBUTE_ID2);
    }

    /**
     * Set the id2 attribute.
     * @param id2 Value of the id2 attribute.
     * @see #ATTRIBUTE_ID2
     */
    public void setId2(final String id2) {
        setAttribute(ATTRIBUTE_ID2, id2);
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
