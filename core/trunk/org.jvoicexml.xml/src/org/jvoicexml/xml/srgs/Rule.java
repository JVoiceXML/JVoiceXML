/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A rule definition associates a legal rule expansion with a rulename.
 * The rule definition is also responsible for defining the scope of the
 * rule definition: whether it is local to the grammar in which it is
 * defined or whether it may be referenced within other grammars. Finally,
 * the rule definition may additionally include documentation comments and
 * other pragmatics.
 *
 * <p>
 * The rulename for each rule definition must be unique within a grammar.
 * The same rulename may be used in multiple grammars.
 * </p>
 *
 * @author Steve Doyle
 * @version $Revision$
 */
public final class Rule
        extends AbstractSrgsNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "rule";

    /**
     * Rule identifier.
     */
    public static final String ATTRIBUTE_ID = "id";

    /**
     * defines the scope of the rule definition. Defined values are public
     * and private.
     */
    public static final String ATTRIBUTE_SCOPE = "scope";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SCOPE);
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

        CHILD_TAGS.add(Token.TAG_NAME);
        CHILD_TAGS.add(Ruleref.TAG_NAME);
        CHILD_TAGS.add(Item.TAG_NAME);
        CHILD_TAGS.add(OneOf.TAG_NAME);
        CHILD_TAGS.add(Tag.TAG_NAME);
        CHILD_TAGS.add(Example.TAG_NAME);
    }

    /**
     * Construct a new rule object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Rule() {
        super(null);
    }

    /**
     * Construct a new rule object.
     * @param node The encapsulated node.
     */
    Rule(final Node node) {
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
    private Rule(final Node n,
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
        return new Rule(n, factory);
    }

    /**
     * Retrieve the id attribute.
     * @return Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public String getId() {
        return getAttribute(ATTRIBUTE_ID);
    }

    /**
     * Set the id attribute.
     * @param id Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public void setId(final String id) {
        setAttribute(ATTRIBUTE_ID, id);
    }

    /**
     * Retrieve the scope attribute.
     * @return Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public String getScope() {
        return getAttribute(ATTRIBUTE_SCOPE);
    }

    /**
     * Set the scope attribute.
     * @param scope Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public void setScope(final String scope) {
        setAttribute(ATTRIBUTE_SCOPE, scope);
    }

    /**.
     * Marks this rule as private
     * @since 0.7.4
     */
    public void makePrivate() {
        setScope(null);
    }

    /**
     * Marks this rule as public.
     * @since 0.7.4
     */
    public void makePublic() {
        setScope("public");
    }

    /**
     * Checks if the current rule is publicly visible.
     * @return <code>true</code> if the rule is public.
     * @since 0.7.5
     */
    public boolean isPublic() {
        final String scope = getScope();
        if (scope == null) {
            return false;
        }
        return scope.equalsIgnoreCase("public");
    }

    /**
     * Create a new text within this node.
     * @param text The text to be added.
     * @return The new created text.
     */
    public Text addText(final String text) {
        final Document document = getOwnerDocument();
        final Node node = document.createTextNode(text);
        final Text textNode = new Text(node, getNodeFactory());
        appendChild(textNode);
        return textNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
