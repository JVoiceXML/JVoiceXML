/*
 * File:    $RCSfile: Var.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
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
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Variables are declared using the <code>&lt;var&gt;</code> element and are
 * initialized with the results of evaluating the OPTIONAL expr attribute as an
 * ECMAScript expression. If the expr attribute is not present in the
 * <code>&lt;var&gt;</code> declaration, the variable is initialized to
 * ECMAScript undefined. The values of variables MAY be subsequently changed
 * with <code>&lt;assign&gt;</code>.
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
public final class Var
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "var";

    /**
     * Indicates the new value of the variable. This is the initial value. It
     * must be a valid ECMAScript expression.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * Indicates the name of the variable. It must be a valid ECMAScript
     * variable name. However, it must not contain a scope prefix. The scope in
     * which the variable is defined is determined from the position in the
     * document at which the variable is defined.
     */
    public static final String ATTRIBUTE_NAME = "name";

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
     * Construct a new var object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Var() {
        super(null);
    }

    /**
     * Construct a new var object.
     * @param node The encapsulated node.
     */
    Var(final Node node) {
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
    private Var(final Node n,
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
        return new Var(n, factory);
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
