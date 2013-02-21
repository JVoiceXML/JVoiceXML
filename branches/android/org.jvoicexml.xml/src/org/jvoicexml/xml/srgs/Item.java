/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/Item.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Locale;
import java.util.Set;

import org.jvoicexml.xml.LanguageIdentifierConverter;
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
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
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
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

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
     * Retrieves the minimal number of repetitions.
     * @return minimal number of repetitions.
     * @since 0.7
     */
    public int getMinRepeat() {
        final String repeat = getRepeat();
        if (repeat == null) {
            return 1;
        }
        final int pos = repeat.indexOf('-');
        if (pos < 0) {
            return Integer.parseInt(repeat);
        }
        final String min = repeat.substring(0, pos);
        if (min.trim().length() == 0) {
            return 1;
        }
        return Integer.parseInt(min);
    }

    /**
     * Retrieves the maximal number of repetitions.
     * @return maximal number of repetitions, <code>-1</code> if there is no
     * maximum.
     * @since 0.7
     */
    public int getMaxRepeat() {
        final String repeat = getRepeat();
        if (repeat == null) {
            return 1;
        }
        final int pos = repeat.indexOf('-');
        if (pos < 0) {
            return Integer.parseInt(repeat);
        } else if (pos == repeat.length()) {
            return -1;
        }
        final String max = repeat.substring(pos + 1);
        if (max.trim().length() == 0) {
            return -1;
        }
        return Integer.parseInt(max);
    }

    /**
     * Set the repeat attribute.
     * @param repeat number of repetitions
     * @see #ATTRIBUTE_REPEAT
     * @since 0.7
     */
    public void setRepeat(final int repeat) {
        if (repeat < 1) {
            throw new IllegalArgumentException(
                    "Repitions (" + repeat + ") must be greater than 0");
        }
        final String value = Integer.toString(repeat);
        setAttribute(ATTRIBUTE_REPEAT, value);
    }

    /**
     * Set the repeat attribute.
     * <p>
     * Throws an {@link IllegalArgumentException} for illegal values, i.e.
     * <code>min &lt; 0</code> or <code>min &lt; max</code>.
     * </p>
     * @param min minimal number of repetitions
     * @param max maximal number of repetitions, a value of <code>-1</code>
     *            denotes an infinite maximum.
     * @see #ATTRIBUTE_REPEAT
     * @since 0.7
     */
    public void setRepeat(final int min, final int max) {
        if (min < 0) {
            throw new IllegalArgumentException(
                    "Repitions (" + min + ", " + max
                    + ") must be greater than 0");
        }
        if ((max > 0) && (min > max)) {
            throw new IllegalArgumentException(
                    "Minimal number of repitions (" + min
                    + ") must not be greeater than max (" + max + ")");
        }
        StringBuilder str = new StringBuilder();
        str.append(min);
        str.append('-');
        if (max > 0) {
            str.append(max);
        }
        setAttribute(ATTRIBUTE_REPEAT, str.toString());
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
     * Sets the repeat attribute to optional, this means to a value
     * <code>0-1</code>.
     * @since 0.6
     */
    public void setOptional() {
        setAttribute(ATTRIBUTE_REPEAT, "0-1");
    }

    /**
     * Checks if this item is optional.This means the value of the repeat
     * attribute equals <code>0-1</code>.
     * @return <code>true</code> if this item is optional.
     * @since 0.6
     */
    public boolean isOptional() {
        final String repeat = getRepeat();

        return "0-1".equals(repeat);
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
     * Retrieve the xml:lang attribute.
     *
     * @return Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     * @since 0.7.1
     */
    public Locale getXmlLangObject() {
        final String xmlLang = getXmlLang();
        return LanguageIdentifierConverter.toLocale(xmlLang);
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
     * Set the xml:lang attribute.
     * @param locale Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     * @since 0.7.1
     */
    public void setXmlLang(final Locale locale) {
        final String xmlLang =
            LanguageIdentifierConverter.toLanguageIdentifier(locale);
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
