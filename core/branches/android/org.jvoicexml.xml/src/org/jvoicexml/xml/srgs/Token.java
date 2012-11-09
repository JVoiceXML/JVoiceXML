/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/Token.java $
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

package org.jvoicexml.xml.srgs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.jvoicexml.xml.LanguageIdentifierConverter;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Define a word or other entity that may serve as input.
 *
 * A token (a.k.a. a terminal symbol) is the part of a grammar that
 * defines words or other entities that may be spoken.
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 */
public final class Token
        extends AbstractSrgsNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "token";

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

    }

    /**
     * Construct a new token object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Token() {
        super(null);
    }

    /**
     * Construct a new token object.
     * @param node The encapsulated node.
     */
    Token(final Node node) {
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
    private Token(final Node n,
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
        return new Token(n, factory);
    }

    /**
     * Retrieve the xml:lang attribute.
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
     * Set the xml:lang attribute.
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
