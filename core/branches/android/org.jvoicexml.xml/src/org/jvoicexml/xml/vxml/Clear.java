/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/Clear.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The <code>&lt;clear&gt;</code> element resets one or more variables,
 * including form items. For each specified variable name, the variable is
 * resolved relative to the current scope.
 *
 * <p>
 * <code>
 * &lt;clear namelist="city state zip"/&gt;
 * </code>
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 */
public final class Clear
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "clear";

    /**
     * The list of variables to be reset; this can include variable names other
     * than form items. If an undeclared variable is referenced in the namelist,
     * then an error.semantic is thrown. When not specified, all form items in
     * the current form are cleared.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
    }

    /**
     * Construct a new clear object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Clear() {
        super(null);
    }

    /**
     * Construct a new clear object.
     *
     * @param node
     *        The encapsulated node.
     */
    Clear(final Node node) {
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
    private Clear(final Node n,
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
        return new Clear(n, factory);
    }

    /**
     * Retrieve the namelist attribute.
     *
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #getNameListObject()
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Set the namelist attribute.
     *
     * @param namelist
     *        Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #setNamelist(TokenList)
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieve the namelist attribute as a list object.
     *
     * @return Value of the namelist attribute as a list.
     *
     * @see #getNamelist()
     */
    public TokenList getNameListObject() {
        final String namelist = getNamelist();

        return new TokenList(namelist);
    }

    /**
     * Set the namelist attribute.
     *
     * @param list
     *        Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #setNamelist(String)
     */
    public void setNamelist(final TokenList list) {
        final String namelist;
        if (list == null) {
            namelist = null;
        } else {
            namelist = list.toString();
        }
        setNamelist(namelist);
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
