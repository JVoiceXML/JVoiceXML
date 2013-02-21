/*
 * File:    $RCSfile: Dialogterminate.java,v $
 * Version: $Revision: 1.6 $
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
 * A CCXML document may decide that it wants to terminate a currently executing
 * dialog, to throw away a previously prepared dialog, or to terminate the
 * preparation of a dialog. This is accomplished using the
 * <code>&lt;dialogterminate&gt;</code> element. When the CCXML interpreter
 * encounters a <code>&lt;dialogterminate&gt;</code> element, it sends a
 * terminate request to the specified dialog.
 *
 * A dialog terminated due to the processing of a
 * <code>&lt;dialogterminate&gt;</code> element MAY still return data to the
 * CCXML application using a dialog.exit event if the value of the immediate
 * attribute is false or unspecified. The details of the data returned are
 * dialog environment specific.
 *
 * If the immediate attribute is set to true the dialog does not return data to
 * the CCXML application and the CCXML interpreter SHALL post a dialog.exit
 * event immediately.
 *
 *
 *
 *
 * @author Steve Doyle
 * @version $Revision: 1.6 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Dialogterminate
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "dialogterminate";

    /**
     * A CCXML document may decide that it wants to terminate a currently
     * executing dialog, to throw away a previously prepared dialog, or to
     * terminate the preparation of a dialog. This is accomplished using the
     * <code>&lt;dialogterminate&gt;</code> element. When the CCXML interpreter
     * encounters a <code>&lt;dialogterminate&gt;</code> element, it sends a
     * terminate request to the specified dialog.
     */
    public static final String ATTRIBUTE_DIALOGID = "dialogid";

    /**
     * An ECMAScript Boolean expression, that identifies the termination style
     * of the dialog.
     */
    public static final String ATTRIBUTE_IMMEDIATE = "immediate";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_DIALOGID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_IMMEDIATE);
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
     * Construct a new dialogterminate object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Dialogterminate() {
        super(null);
    }

    /**
     * Construct a new dialogterminate object.
     * @param node The encapsulated node.
     */
    Dialogterminate(final Node node) {
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
        return new Dialogterminate(n);
    }

    /**
     * Retrieve the dialogid attribute.
     * @return Value of the dialogid attribute.
     * @see #ATTRIBUTE_DIALOGID
     */
    public String getDialogid() {
        return getAttribute(ATTRIBUTE_DIALOGID);
    }

    /**
     * Set the dialogid attribute.
     * @param dialogid Value of the dialogid attribute.
     * @see #ATTRIBUTE_DIALOGID
     */
    public void setDialogid(final String dialogid) {
        setAttribute(ATTRIBUTE_DIALOGID, dialogid);
    }

    /**
     * Retrieve the immediate attribute.
     * @return Value of the immediate attribute.
     * @see #ATTRIBUTE_IMMEDIATE
     */
    public String getImmediate() {
        return getAttribute(ATTRIBUTE_IMMEDIATE);
    }

    /**
     * Set the immediate attribute.
     * @param immediate Value of the immediate attribute.
     * @see #ATTRIBUTE_IMMEDIATE
     */
    public void setImmediate(final String immediate) {
        setAttribute(ATTRIBUTE_IMMEDIATE, immediate);
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
