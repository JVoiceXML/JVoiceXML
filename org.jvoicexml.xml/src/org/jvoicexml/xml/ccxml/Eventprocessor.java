/*
 * File:    $RCSfile: Eventprocessor.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 * State: $State: Exp $
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
 * The <code>&lt;eventprocessor&gt;</code> acts a container for
 * <code>&lt;transition&gt;</code>s. A valid CCXML document MUST only have a
 * single <code>&lt;eventprocessor&gt;</code>.
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
public final class Eventprocessor
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "eventprocessor";

    /**
     * This is a CCXML variable name, which is the name of the eventprocessor's
     * state variable. This variable must be defined using the var or the script
     * element in the ccxml scope.
     */
    public static final String ATTRIBUTE_STATEVARIABLE = "statevariable";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_STATEVARIABLE);
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

        CHILD_TAGS.add(Transition.TAG_NAME);
    }

    /**
     * Construct a new eventprocessor object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Eventprocessor() {
        super(null);
    }

    /**
     * Construct a new eventprocessor object.
     * @param node The encapsulated node.
     */
    Eventprocessor(final Node node) {
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
    private Eventprocessor(final Node n,
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
        return new Eventprocessor(n, factory);
    }

    /**
     * Retrieve the statevariable attribute.
     * @return Value of the statevariable attribute.
     * @see #ATTRIBUTE_STATEVARIABLE
     */
    public String getStatevariable() {
        return getAttribute(ATTRIBUTE_STATEVARIABLE);
    }

    /**
     * Set the statevariable attribute.
     * @param statevariable Value of the statevariable attribute.
     * @see #ATTRIBUTE_STATEVARIABLE
     */
    public void setStatevariable(final String statevariable) {
        setAttribute(ATTRIBUTE_STATEVARIABLE, statevariable);
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
