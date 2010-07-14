/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date $, Dirk Schnelle-Walka, project lead
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
import java.util.Locale;
import java.util.Set;

import org.jvoicexml.xml.LanguageIdentifierConverter;
import org.jvoicexml.xml.NodeHelper;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * This is the root element of all SSML documents.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
public final class Speak
        extends AbstractSsmlNode implements TextContainer {

    /** Name of the tag. */
    public static final String TAG_NAME = "speak";

    /**
     * Defines the version of the SSML document.
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * Default Voice XML version number.
     * @see #ATTRIBUTE_VERSION
     */
    public static final String DEFAULT_VERSION = "1.0";

    /**
     * Default namespace.
     * @see #ATTRIBUTE_XMLNS
     */
    public static final String DEFAULT_XMLNS =
        "http://www.w3.org/2001/10/synthesis";

    /**
     * The designated namespace for VoiceXXML (required). The namespace for
     * SSML is defined to be <code>DEFAULT_XMLNS</code>
     */
    public static final String ATTRIBUTE_XMLNS = "xmlns";

    /**
     * The language identifier for the text to be spoek. If omitted, the value
     * is inherited down from the document hierarchy.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * Declares the base URI from which relative URIs in the SSML is
     * resolved. This base declaration has precedence over the
     * <code>&lt;vxml&gt;</code> base URI declaration. If a local declaration
     * is omitted, the value is inherited down the document hierarchy.
     */
    public static final String ATTRIBUTE_XML_BASE = "xml:base";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
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
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Lexicon.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Text.TAG_NAME);
    }

    /**
     * Construct a new audio object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Speak() {
        super(null);
    }

    /**
     * Construct a new speak object.
     * @param node The encapsulated node.
     */
    Speak(final Node node) {
        super(node);

        // Set the default attributes.
        setVersion(DEFAULT_VERSION);
        setAttribute(ATTRIBUTE_XMLNS, DEFAULT_XMLNS);
        setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        setAttribute("xsi:schematicLocation",
                     DEFAULT_XMLNS
                     + " http://www.w3.org/TR/speech-synthesis/synthesis.xsd");
        setXmlLang(Locale.getDefault());
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Speak(final Node n,
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
        return new Speak(n, factory);
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

    /**
     * Retrieve the xml:lang attribute.
     *
     * @return Value of the xml:lang attribute.
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
     * @since 0.7.3
     */
    public Locale getXmlLangObject() {
        final String xmlLang = getXmlLang();
        return LanguageIdentifierConverter.toLocale(xmlLang);
    }

    /**
     * Set the xml:lang attribute.
     *
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Set the xml:lang attribute.
     * @param locale Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     * @since 0.7.3
     */
    public void setXmlLang(final Locale locale) {
        final String xmlLang =
            LanguageIdentifierConverter.toLanguageIdentifier(locale);
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Retrieve the version attribute.
     *
     * @return Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public String getVersion() {
        return getAttribute(ATTRIBUTE_VERSION);
    }

    /**
     * Set the version attribute.
     *
     * @param version Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public void setVersion(final String version) {
        setAttribute(ATTRIBUTE_VERSION, version);
    }

    /**
     * Retrieve the xml:base attribute.
     *
     * @return Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public String getXmlBase() {
        return getAttribute(ATTRIBUTE_XML_BASE);
    }

    /**
     * Set the xml:base attribute.
     *
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
    }

    /**
     * Create a new text within this speak element.
     * @param text The text to be added.
     * @return The new created text.
     */
    public Text addText(final String text) {
        return NodeHelper.addText(this, text);
    }
}
