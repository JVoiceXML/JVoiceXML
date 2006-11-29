/*
 * File:    $RCSfile: Destroyconference.java,v $
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
 * A CCXML document can attempt to detach from an existing Conference Object
 * using <code>&lt;destroyconference&gt;</code>. This destroys the conference
 * if no other sessions are attached to it. The target Conference Object is
 * identified using the conferenceid attribute. The successful execution of
 * <code>&lt;destroyconference&gt;</code> will result in the generation of a
 * conference.destroyed event. If for any reason the implementation is unable to
 * deallocate the Conference Object it MUST fail with a error.conference.destroy
 * event.
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
public final class Destroyconference
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "destroyconference";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * the conference that should be destroyed. If the attribute value is
     * invalid an error.semantic event will be thrown.
     */
    public static final String ATTRIBUTE_CONFERENCEID = "conferenceid";

    /**
     * An ECMAScript expression which returns a string that is the identifier of
     * the conference that should be destroyed. If the attribute value is
     * invalid an error.semantic event will be thrown.
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

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONFERENCEID);
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
     * Construct a new destroyconference object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Destroyconference() {
        super(null);
    }

    /**
     * Construct a new destroyconference object.
     * @param node The encapsulated node.
     */
    Destroyconference(final Node node) {
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
        return new Destroyconference(n);
    }

    /**
     * Retrieve the conferenceid attribute.
     * @return Value of the conferenceid attribute.
     * @see #ATTRIBUTE_CONFERENCEID
     */
    public String getConferenceid() {
        return getAttribute(ATTRIBUTE_CONFERENCEID);
    }

    /**
     * Set the conferenceid attribute.
     * @param conferenceid Value of the conferenceid attribute.
     * @see #ATTRIBUTE_CONFERENCEID
     */
    public void setConferenceid(final String conferenceid) {
        setAttribute(ATTRIBUTE_CONFERENCEID, conferenceid);
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
