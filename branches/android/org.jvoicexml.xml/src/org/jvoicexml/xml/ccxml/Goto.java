/*
 * File:    $RCSfile: Goto.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
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
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * <code>&lt;fetch&gt;</code>, in conjunction with <code>&lt;goto&gt;</code>,
 * is used to transfer execution to a different CCXML document in a
 * multi-document CCXML application. The <code>&lt;fetch&gt;</code> tells the
 * platform to find, load, and parse a given CCXML document. After the fetch
 * completes, the CCXML application can then issue a <code>&lt;goto&gt;</code>
 * to execute the now-fetched document.
 *
 *
 * @author Steve Doyle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Goto
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "goto";

    /**
     * An ECMAScript expression which returns the fetch identifier of a
     * completed fetch request acquired either in a fetch with the fetchid
     * attribute, or from the fetchid attribute of a fetch.done event. If the
     * attribute value is invalid, an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_FETCHID = "fetchid";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHID);
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
     * Construct a new goto object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Goto() {
        super(null);
    }

    /**
     * Construct a new goto object.
     * @param node The encapsulated node.
     */
    Goto(final Node node) {
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
    private Goto(final Node n,
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
        return new Goto(n, factory);
    }

    /**
     * Retrieve the fetchid attribute.
     * @return Value of the fetchid attribute.
     * @see #ATTRIBUTE_FETCHID
     */
    public String getFetchid() {
        return getAttribute(ATTRIBUTE_FETCHID);
    }

    /**
     * Set the fetchid attribute.
     * @param fetchid Value of the fetchid attribute.
     * @see #ATTRIBUTE_FETCHID
     */
    public void setFetchid(final String fetchid) {
        setAttribute(ATTRIBUTE_FETCHID, fetchid);
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
