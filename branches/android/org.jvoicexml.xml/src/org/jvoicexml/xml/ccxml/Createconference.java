/*
 * File:    $RCSfile: Createconference.java,v $
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
 * A CCXML document can attempt to create or attach to a Conference Object using
 * <code>&lt;createconference&gt;</code>. This element will instruct the
 * implementation to allocate a Conference Object using the specified options.
 * The successful execution of <code>&lt;createconference&gt;</code> will
 * result in the generation of a conference.created event. If for any reason the
 * implementation is unable to create the Conference Object using the specified
 * options it MUST fail with a error.conference.create event.
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
public final class Createconference
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "createconference";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive the conference
     * identifier. A conference identifier should be globally unique, so that
     * conferences can be uniquely addressed and possibly connected to. It
     * should be in URI format.
     */
    public static final String ATTRIBUTE_CONFERENCEID = "conferenceid";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive the conference
     * identifier. A conference identifier should be globally unique, so that
     * conferences can be uniquely addressed and possibly connected to. It
     * should be in URI format.
     */
    public static final String ATTRIBUTE_CONFNAME = "confname";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform when creating the conference.
     *
     * Note: The meaning of these hints is specific to the implementing platform
     * and the event processor.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * An ECMAScript expression which returns the number of guaranteed listener
     * slots the conference mixer should reserve. If the conference already
     * exists, then this attribute will be ignored. If the conference mixer is
     * unable to reserve this many listener slots, the createconference must
     * fail with a error.conference.create event.
     */
    public static final String ATTRIBUTE_RESERVEDLISTENERS =
            "reservedlisteners";

    /**
     * An ECMAScript expression which returns the number of guaranteed speaker
     * slots the conference mixer should reserve. If the conference already
     * exists, then this attribute will be ignored. If the conference mixer is
     * unable to reserve this many speaker slots, the createconference must fail
     * with a error.conference.create event.
     */
    public static final String ATTRIBUTE_RESERVEDTALKERS = "reservedtalkers";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONFNAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_RESERVEDLISTENERS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_RESERVEDTALKERS);
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
     * Construct a new createconference object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Createconference() {
        super(null);
    }

    /**
     * Construct a new createconference object.
     * @param node The encapsulated node.
     */
    Createconference(final Node node) {
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
    private Createconference(final Node n,
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
        return new Createconference(n, factory);
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
     * Retrieve the confname attribute.
     * @return Value of the confname attribute.
     * @see #ATTRIBUTE_CONFNAME
     */
    public String getConfname() {
        return getAttribute(ATTRIBUTE_CONFNAME);
    }

    /**
     * Set the confname attribute.
     * @param confname Value of the confname attribute.
     * @see #ATTRIBUTE_CONFNAME
     */
    public void setConfname(final String confname) {
        setAttribute(ATTRIBUTE_CONFNAME, confname);
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
     * Retrieve the reservedlisteners attribute.
     * @return Value of the reservedlisteners attribute.
     * @see #ATTRIBUTE_RESERVEDLISTENERS
     */
    public String getReservedlisteners() {
        return getAttribute(ATTRIBUTE_RESERVEDLISTENERS);
    }

    /**
     * Set the reservedlisteners attribute.
     * @param reservedlisteners Value of the reservedlisteners attribute.
     * @see #ATTRIBUTE_RESERVEDLISTENERS
     */
    public void setReservedlisteners(final String reservedlisteners) {
        setAttribute(ATTRIBUTE_RESERVEDLISTENERS, reservedlisteners);
    }

    /**
     * Retrieve the reservedtalkers attribute.
     * @return Value of the reservedtalkers attribute.
     * @see #ATTRIBUTE_RESERVEDTALKERS
     */
    public String getReservedtalkers() {
        return getAttribute(ATTRIBUTE_RESERVEDTALKERS);
    }

    /**
     * Set the reservedtalkers attribute.
     * @param reservedtalkers Value of the reservedtalkers attribute.
     * @see #ATTRIBUTE_RESERVEDTALKERS
     */
    public void setReservedtalkers(final String reservedtalkers) {
        setAttribute(ATTRIBUTE_RESERVEDTALKERS, reservedtalkers);
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
