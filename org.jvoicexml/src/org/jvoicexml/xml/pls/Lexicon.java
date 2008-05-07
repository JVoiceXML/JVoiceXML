/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.pls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.vxml.Meta;
import org.jvoicexml.xml.vxml.Metadata;
import org.w3c.dom.Node;

/**
 * The root element of the Pronunciation Lexicon markup language is the
 * <code>&lt;lexicon&gt;</code> element. This element is the container for all
 * other elements of the PLS language.
 * A <code>&lt;lexicon&gt;</code> element MUST contain zero or more
 * <code>&lt;meta&gt;</code>> elements, followed by an OPTIONAL
 * <code>&lt;metadata&gt;</code> element, followed by zero or more
 * <code>&lt;lexeme&gt;</code> elements. Note that a PLS document without any
 * <code>&lt;lexeme&gt;</code> elements may be useful as a placeholder for
 * future lexical entries.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Lexicon
        extends AbstractPlsNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "lexicon";

    /**
     * The REQUIRED version attribute indicates the version of the specification
     * to be used for the document and MUST have the value <code>"1.0"</code>.
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * The REQUIRED xml:lang attribute allows identification of the language for
     * which the pronunciation lexicon is relevant.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * The OPTIONAL xml:base attribute establishes a base URI for the PLS
     * document.
     */
    public static final String ATTRIBUTE_XML_BASE = "xml:base";

    /**
     * The namespace URI for PLS is
     * <code>"http://www.w3.org/2005/01/pronunciation-lexicon"</code>.
     * All PLS markup MUST be associated with the PLS namespace.
     */
    public static final String ATTRIBUTE_XMLNS = "xmlns";

    /**
     * The <code>&lt;lexicon&gt;</code> element MUST specify an alphabet
     * attribute which indicates the default pronunciation alphabet to be used
     * within the PLS document.
     */
    public static final String ATTRIBUTE_ALPHABET = "alphabet";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ALPHABET);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XMLNS);
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

        CHILD_TAGS.add(Meta.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
    }

    /**
     * Construct a new lexicon object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Lexicon() {
        super(null);
    }

    /**
     * Construct a new lexicon object.
     * @param node The encapsulated node.
     */
    Lexicon(final Node node) {
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
    private Lexicon(final Node n,
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
        return new Lexicon(n, factory);
    }

    /**
     * Retrieve the alphabet attribute.
     *
     * @return value of the alphabet attribute.
     * @see #ATTRIBUTE_ALPHABET
     */
    public String getAlpabet() {
        return getAttribute(ATTRIBUTE_ALPHABET);
    }

    /**
     * Set the alphabet attribute.
     *
     * @param alphabet value of the alphabet attribute.
     * @see #ATTRIBUTE_ALPHABET
     */
    public void setAlphabet(final String alphabet) {
        setAttribute(ATTRIBUTE_ALPHABET, alphabet);
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
     * Set the xml:lang attribute.
     *
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
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
     * Retrieve the xmlns attribute.
     *
     * @return Value of the xmlns attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public String getXmlns() {
        return getAttribute(ATTRIBUTE_XMLNS);
    }

    /**
     * Set the xml:base attribute.
     *
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public void setXmlns(final String xmlBase) {
        setAttribute(ATTRIBUTE_XMLNS, xmlBase);
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
