/*
 * File:    $RCSfile: Voice.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ssml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The voice element is a production element that requests a change in
 * speaking voice.
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Voice
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "voice";

    /**
     * Optional language specification attribute.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * Optional attribute indicating the preferred gender of the voice to speak
     * the contained text. Enumerated values are: "male", "female", "neutral".
     */
    public static final String ATTRIBUTE_GENDER = "gender";

    /**
     * Optional attribute indicating the preferred age in years
     * (since birth) of the voice to speak the contained text.
     */
    public static final String ATTRIBUTE_AGE = "age";

    /**
     * optional attribute indicating a preferred variant of the other voice
     * characteristics to speak the contained text. (e.g. the second male
     * child voice).
     */
    public static final String ATTRIBUTE_VARIANT = "variant";

    /**
     * optional attribute indicating a preferred variant of the other voice
     * characteristics to speak the contained text. (e.g. the second male
     * child voice).
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

        ATTRIBUTE_NAMES.add(ATTRIBUTE_AGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_GENDER);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VARIANT);
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

        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
    }

    /**
     * Construct a new voice object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Voice() {
        super(null);
    }

    /**
     * Construct a new voice object.
     * @param node The encapsulated node.
     */
    Voice(final Node node) {
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
    private Voice(final Node n,
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
        return new Voice(n, factory);
    }

    /**
     * Retrieve the xml:lang attribute.
     * @return Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public String getXmlLang() {
        return getAttribute(ATTRIBUTE_XML_LANG);
    }

    /**
     * Set the xml:lang attribute.
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Retrieve the gender attribute.
     * @return Value of the gender attribute.
     * @see #ATTRIBUTE_GENDER
     */
    public String getGenderName() {
        final GenderType gender = getGender();
        if (gender == null) {
            return null;
        }
        return gender.getType();
    }

    /**
     * Retrieve the gender attribute.
     * @return Value of the gender attribute.
     * @see #ATTRIBUTE_GENDER
     * @since 0.6
     */
    public GenderType getGender() {
        final String gender = getAttribute(ATTRIBUTE_GENDER);
        if (gender == null) {
            return null;
        }
        return GenderType.valueOf(gender);
    }

    /**
     * Set the gender attribute.
     * @param gender Value of the gender attribute.
     * @see #ATTRIBUTE_GENDER
     */
    public void setGender(final String gender) {
        setAttribute(ATTRIBUTE_GENDER, gender);
    }

    /**
     * Set the gender attribute.
     * @param gender Value of the gender attribute.
     * @see #ATTRIBUTE_GENDER
     * @since 0.6
     */
    public void setGender(final GenderType gender) {
        final String value;
        if (gender == null) {
            value = null;
        } else {
            value = gender.getType();
        }
        setGender(value);
    }
    /**
     * Retrieve the age attribute.
     * @return Value of the age attribute.
     * @see #ATTRIBUTE_AGE
     */
    public String getAge() {
        return getAttribute(ATTRIBUTE_AGE);
    }

    /**
     * Retrieves the age attribute as an integer.
     * @return age, <code>-1</code> if no age is specified.
     * @since 0.6
     */
    public int getAgeAsInt() {
        final String age = getAge();
        if (age == null) {
            return -1;
        }
        return Integer.parseInt(age);
    }

    /**
     * Set the age attribute.
     * @param age Value of the age attribute.
     * @see #ATTRIBUTE_AGE
     */
    public void setAge(final String age) {
        setAttribute(ATTRIBUTE_AGE, age);
    }

    /**
     * Set the age attribute.
     * @param age Value of the age attribute.
     * @see #ATTRIBUTE_AGE
     * @since 0.6
     */
    public void setAge(final int age) {
        final String value = Integer.toString(age);
        setAge(value);
    }

    /**
     * Retrieve the variant attribute.
     * @return Value of the variant attribute.
     * @see #ATTRIBUTE_VARIANT
     */
    public String getVariant() {
        return getAttribute(ATTRIBUTE_VARIANT);
    }

    /**
     * Set the variant attribute.
     * @param variant Value of the variant attribute.
     * @see #ATTRIBUTE_VARIANT
     */
    public void setVariant(final String variant) {
        setAttribute(ATTRIBUTE_VARIANT, variant);
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
     * Create a new text within this block.
     * @param text The text to be added.
     * @return The new created text.
     * @since 0.6
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
