/*
 * File:    $RCSfile: Cancel.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/05/16 07:26:21 $
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
 * When a CCXML program uses <code>&lt;send&gt;</code> to send an event and
 * includes a delay attribute, the <code>&lt;cancel&gt;</code> command will
 * cancel the pending event, if possible.
 *
 * The cancel operation will cancel a pending event by removing it from the
 * event queue of the CCXML session from which it has been sent. If the delay
 * has expired and the event has already been removed from the event queue, the
 * <code>&lt;cancel&gt;</code> request will fail and an error.notallowed event
 * will be delivered to the event queue of the CCXML session that executed the
 * <code>&lt;cancel&gt;</code>.
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
public final class Cancel
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "cancel";

    /**
     * An ECMAScript expression which returns the value of the event identifier
     * that was received when the send command was issued.
     */
    public static final String ATTRIBUTE_SENDID = "sendid";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_SENDID);
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
     * Construct a new cancel object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Cancel() {
        super(null);
    }

    /**
     * Construct a new cancel object.
     * @param node The encapsulated node.
     */
    Cancel(final Node node) {
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
        return new Cancel(n);
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
