/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/Param.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.xml.vxml;

import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The <code>&lt;param&gt;</code> element is used to specify values that are
 * passed to <code>&lt;subdialog&gt;</code>s or <code>&lt;object&gt;</code>s.
 *
 * @see org.jvoicexml.xml.vxml.ObjectTag
 * @see org.jvoicexml.xml.vxml.Subdialog
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 */
public final class Param
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "param";

    /**
     * The name to be associated with this parameter when the object or
     * subdialog is invoked.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * An expression that computes the value associated with name.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * Associates a literal string value with name.
     */
    public static final String ATTRIBUTE_VALUE = "value";

    /**
     * One of data or ref, by default data; used to indicate to an object
     * if the value associated with name is data or a URI (ref). This is not
     * used for <code>&lt;subdialog&gt;</code> since values are always data.
     */
    public static final String ATTRIBUTE_VALUETYPE = "valuetype";

    /**
     * The media type of the result provided by a URI if the valuetype is
     * ref; only relevant for uses of <code>&lt;param&gt;</code> in
     * <code>&lt;object&gt;</code>.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VALUE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VALUETYPE);
    }

    /**
     * Construct a new param object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Param() {
        super(null);
    }

    /**
     * Construct a new param object.
     * @param node The encapsulated node.
     */
    Param(final Node node) {
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
    private Param(final Node n,
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
        return new Param(n, factory);
    }

    /**
     * Retrieve the name attribute.
     * @return Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public String getName() {
        return getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Set the name attribute.
     * @param name Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public void setName(final String name) {
        setAttribute(ATTRIBUTE_NAME, name);
    }

    /**
     * Retrieve the expr attribute.
     * @return Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public String getExpr() {
        return getAttribute(ATTRIBUTE_EXPR);
    }

    /**
     * Set the expr attribute.
     * @param expr Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
    }

    /**
     * Retrieve the value attribute.
     * @return Value of the value attribute.
     * @see #ATTRIBUTE_VALUE
     */
    public String getValue() {
        return getAttribute(ATTRIBUTE_VALUE);
    }

    /**
     * Set the value attribute.
     * @param value Value of the value attribute.
     * @see #ATTRIBUTE_VALUE
     */
    public void setValue(final String value) {
        setAttribute(ATTRIBUTE_VALUE, value);
    }

    /**
     * Retrieve the valuetype attribute.
     * @return Value of the valuetype attribute.
     * @see #ATTRIBUTE_VALUETYPE
     */
    public ParamValueType getValuetype() {
        final String type = getAttribute(ATTRIBUTE_VALUETYPE);
        if (type == null) {
            return null;
        }

        final String str = type.toUpperCase();
        return ParamValueType.valueOf(str);
    }

    /**
     * Retrieve the valuetype attribute.
     * @return Value of the valuetype attribute.
     * @see #ATTRIBUTE_VALUETYPE
     * @since 0.5
     */
    public String getValuetypeName() {
        final ParamValueType type = getValuetype();

        return type.getType();
    }

    /**
     * Set the valuetype attribute.
     * @param valuetype Value of the valuetype attribute.
     * @see #ATTRIBUTE_VALUETYPE
     */
    public void setValuetype(final String valuetype) {
        setAttribute(ATTRIBUTE_VALUETYPE, valuetype);
    }

    /**
     * Set the valuetype attribute.
     * @param valuetype Value of the valuetype attribute.
     * @see #ATTRIBUTE_VALUETYPE
     * @since 0.5
     */
    public void setValuetype(final ParamValueType valuetype) {
        final String type = valuetype.getType();
        setAttribute(ATTRIBUTE_VALUETYPE, type);
    }

    /**
     * Retrieve the type attribute.
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Set the type attribute.
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
