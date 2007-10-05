/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
 * Define an expansion with optional repeating and probability.
 *
 * An item element can surround any expansion to permit a repeat
 * attribute or language identifier to be attached.
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Item
        extends AbstractSrgsNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "item";

    /**
     * Defines a legal rule expansion as being another sub-expansion that
     * is optional, that is repeated zero or more times, that is repeated
     * one or more times, or that is repeated some range of times.
     */
    public static final String ATTRIBUTE_REPEAT = "repeat";

    /**
     * Any repeat operator may specify an optional repeat probability.
     * The value indicates the probability of successive repetition of
     * the repeated expansion.
     */
    public static final String ATTRIBUTE_REPEAT_PROB = "repeatProb";

    /**
     * A weight may be optionally provided for any number of alternatives
     * in an alternative expansion. Weights are simple positive floating
     * point values without exponentials. Legal formats are "n", "n.", ".n"
     * and "n.n" where "n" is a sequence of one or many digits.
     *
     * A weight is nominally a multiplying factor in the likelihood domain
     * of a speech recognition search. A weight of 1.0 is equivalent to
     * providing no weight at all. A weight greater than "1.0" positively
     * biases the alternative and a weight less than "1.0" negatively biases
     * the alternative.
     */
    public static final String ATTRIBUTE_WEIGHT = "weight";

    /**
     * The language identifier for the grammar. If omitted, the value is
     * inherited down from the document hierarchy.
     */
    public static final String ATTRIBUTE_XML_LANG = "xmlLang";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_REPEAT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_REPEAT_PROB);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_WEIGHT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
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
    }

    /**
     * Construct a new item object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Item() {
        super(null);
    }

    /**
     * Construct a new item object.
     * @param node The encapsulated node.
     */
    Item(final Node node) {
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
    private Item(final Node n,
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
        return new Item(n, factory);
    }

    /**
     * Retrieve the repeat attribute.
     * @return Value of the repeat attribute.
     * @see #ATTRIBUTE_REPEAT
     */
    public String getRepeat() {
        return getAttribute(ATTRIBUTE_REPEAT);
    }

    /**
     * Set the repeat attribute.
     * @param repeat Value of the repeat attribute.
     * @see #ATTRIBUTE_REPEAT
     */
    public void setRepeat(final String repeat) {
        setAttribute(ATTRIBUTE_REPEAT, repeat);
    }

    /**
     * Retrieve the repeatProb attribute.
     * @return Value of the repeatProb attribute.
     * @see #ATTRIBUTE_REPEAT_PROB
     */
    public String getRepeatProb() {
        return getAttribute(ATTRIBUTE_REPEAT_PROB);
    }

    /**
     * Set the repeatProb attribute.
     * @param repeatProb Value of the repeatProb attribute.
     * @see #ATTRIBUTE_REPEAT_PROB
     */
    public void setRepeatProb(final String repeatProb) {
        setAttribute(ATTRIBUTE_REPEAT_PROB, repeatProb);
    }

    /**
     * Retrieve the weight attribute.
     * @return Value of the weight attribute.
     * @see #ATTRIBUTE_WEIGHT
     */
    public String getWeight() {
        return getAttribute(ATTRIBUTE_WEIGHT);
    }

    /**
     * Set the weight attribute.
     * @param weight Value of the weight attribute.
     * @see #ATTRIBUTE_WEIGHT
     */
    public void setWeight(final String weight) {
        setAttribute(ATTRIBUTE_WEIGHT, weight);
    }

    /**
     * Retrieve the xmlLang attribute.
     * @return Value of the xmlLang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public String getXmlLang() {
        return getAttribute(ATTRIBUTE_XML_LANG);
    }

    /**
     * Set the xmlLang attribute.
     * @param xmlLang Value of the xmlLang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
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
