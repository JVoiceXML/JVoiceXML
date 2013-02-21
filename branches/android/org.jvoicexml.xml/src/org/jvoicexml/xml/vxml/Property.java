/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/Property.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Control implementation platform settings.
 * <p>
 * The <code>&lt;property&gt;</code> element sets a property value. Properties
 * are used to set values that affect platform behavior, such as the recognition
 * process, timeouts, caching policy, etc.
 * </p>
 * <p>
 * Properties may be defined for the whole application, for the whole document
 * at the <code>&lt;vxml&gt;</code> level, for a particular dialog at the
 * <code>&lt;form&gt;</code> or <code>&lt;menu&gt;</code> level, or for a
 * particular form item. Properties apply to their parent element and all the
 * descendants of the parent. A property at a lower level overrides a property
 * at a higher level. When different values for a property are specified at the
 * same level, the last one in document order applies. Properties specified in
 * the application root document provide default values for properties in every
 * document in the application; properties specified in an individual document
 * override property values specified in the application root document.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Vxml
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.vxml.Menu
 *
 * @author Steve Doyle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Property
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "property";

    /**
     * The name of the property.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The value of the property.
     */
    public static final String ATTRIBUTE_VALUE = "value";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VALUE);
    }

    /**
     * Construct a new property object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Property() {
        super(null);
    }

    /**
     * Construct a new property object.
     * @param node The encapsulated node.
     */
    Property(final Node node) {
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
    private Property(final Node n,
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
        return new Property(n, factory);
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
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
