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

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The <code>&lt;cancel&gt;></code> element is used to cancel a delayed
 * <code>&lt;send&gt;</code> event. The SCXML Processor MUST NOT allow
 * <code>&lt;cancel&gt;</code> to affect events that were not raised in the
 * same document. The Processor SHOULD make its best attempt to cancel the
 * delayed event. Note, however, that it can not be guaranteed to succeed,
 * for example if the event has already been delivered by the time the
 *  <code>&lt;cancel&gt;></code> tag executes.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Cancel
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "cancel";

    /**
     * The ID of the event which is to be canceled.
     */
    private static final String ATTRIBUTE_SENDID = "sendid";

    /**
     * A dynamic alternative to <code>sendid</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;cancel&gt;</code> element is evaluated and treat the result as
     * if it had been entered as the value of <code>sendid</code>.
     */
    private static final String ATTRIBUTE_SENDIDEXPR = "sendidEXPR";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SENDIDEXPR);
    }

    /**
     * Construct a new final object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Cancel() {
        super(null);
    }

    /**
     * Construct a new final object.
     * @param node The encapsulated node.
     */
    Cancel(final Node node) {
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
    private Cancel(final Node n,
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
        return new Cancel(n, factory);
    }

    /**
     * Retrieves the sendid attribute.
     *
     * @return value of the sendid attribute.
     * @see #ATTRIBUTE_SENDID
     */
    public String getSendid() {
        return getAttribute(ATTRIBUTE_SENDID);
    }

    /**
     * Sets the sendid attribute.
     *
     * @param sendid Value of the sendid attribute.
     * @see #ATTRIBUTE_SENDID
     */
    public void setSendid(final String sendid) {
        setAttribute(ATTRIBUTE_SENDID, sendid);
    }

    /**
     * Retrieves the sendidexpr attribute.
     *
     * @return value of the sendidexpr attribute.
     * @see #ATTRIBUTE_SENDIDEXPR
     */
    public String getSendidexpr() {
        return getAttribute(ATTRIBUTE_SENDIDEXPR);
    }

    /**
     * Sets the sendidexpr attribute.
     *
     * @param sendidexpr Value of the sendidexpr attribute.
     * @see #ATTRIBUTE_SENDIDEXPR
     */
    public void setSendidexp(final String sendidexpr) {
        setAttribute(ATTRIBUTE_SENDIDEXPR, sendidexpr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
