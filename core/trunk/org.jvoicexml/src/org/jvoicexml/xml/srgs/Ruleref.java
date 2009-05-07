/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.xml.srgs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Refer to a rule defined locally or externally.
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class Ruleref
        extends AbstractSrgsNode implements VoiceXmlNode {
    /** Name of the tag. */
    public static final String TAG_NAME = "ruleref";

    /**
     * URI of grammar where the rule is defined.
     */
    public static final String ATTRIBUTE_URI = "uri";

    /**
     * The optional type attribute specifies the media type of the grammar
     * containing the reference.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Identifies the rule as being a special rule which is defined to
     * have specific interpretation and processing by a speech recogniser.
     */
    public static final String ATTRIBUTE_SPECIAL = "special";


    /**
     * special rule - GARBAGE.
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     */
    private static final String SPECIAL_VALUE_GARBAGE = "GARBAGE";

    /**
     * special rule - NULL.
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     */
    private static final String SPECIAL_VALUE_NULL = "NULL";

    /**
     * special rule - VOID.
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     */
    private static final String SPECIAL_VALUE_VOID = "VOID";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_SPECIAL);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_URI);
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
     * Construct a new ruleref object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Ruleref() {
        super(null);
    }

    /**
     * Construct a new ruleref object.
     * @param node The encapsulated node.
     */
    Ruleref(final Node node) {
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
    private Ruleref(final Node n,
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
        return new Ruleref(n, factory);
    }

    /**
     * Retrieves the uri attribute.
     * @return Value of the uri attribute.
     * @see #ATTRIBUTE_URI
     */
    public String getUri() {
        return getAttribute(ATTRIBUTE_URI);
    }

    /**
     * Sets the uri attribute.
     * @param uri Value of the uri attribute.
     * @see #ATTRIBUTE_URI
     */
    public void setUri(final String uri) {
        setAttribute(ATTRIBUTE_URI, uri);
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
     * Retrieve the special attribute.
     * @return Value of the special attribute.
     * @see #ATTRIBUTE_SPECIAL
     */
    public String getSpecial() {
        return getAttribute(ATTRIBUTE_SPECIAL);
    }

    /**
     * Set the special attribute.
     * @param special Value of the special attribute.
     * @see #ATTRIBUTE_SPECIAL
     */
    public void setSpecial(final String special) {
        setAttribute(ATTRIBUTE_SPECIAL, special);
    }

    /**
     * Tests if this RuleRef is a special GARBAGE rule.
     *
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     * @return <code>true</code> if this is a GARBAGE rule
     * @since 0.7
     */
    public boolean isSpecialGarbage() {
        final String specialValue = getSpecial();
        return SPECIAL_VALUE_GARBAGE.equals(specialValue);
    }

    /**
     * Tests if this RuleRef is a special VOID rule.
     *
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     * @return <code>true</code> if this is a VOID rule
     * @since 0.7
     */
    public boolean isSpecialVoid() {
        final String specialValue = getSpecial();
        return SPECIAL_VALUE_VOID.equals(specialValue);
    }

    /**
     * Tests if this RuleRef is a special NULL rule.
     *
     * @see <a href="http://www.w3.org/TR/2003/PR-speech-grammar-20031218/#S2.2.3">Speech Recognition Grammar Specification Version 1.0 - 2.2.3 Special Rules</a>
     * @return <code>true</code> if this is a NULL rule
     * @since 0.7
     */
    public boolean isSpecialNull() {
        final String specialValue = getSpecial();
        return SPECIAL_VALUE_NULL.equals(specialValue);
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
