/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 2325 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
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
import org.w3c.dom.Node;

/**
 * The <code>&lt;lexeme&gt;</code> element is a container for a lexical entry
 * which MAY include multiple orthographies and multiple pronunciation
 * information.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Lexeme
        extends AbstractPlsNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "lexeme";

    /**
     * The  OPTIONAL xml:id [XML-ID] attribute allows the element to be
     * referenced from other documents (through fragment identifiers or
     * XPointer, for instance).
     * For example, developers may use external RDF statements to associate
     * metadata (such as part of speech or word relationships) with a lexeme.
     */
    public static final String ATTRIBUTE_XML_ID = "xml:id";

    /**
     * The OPTIONAL role attribute which takes as its value one or more white
     * space separated QNames as defined in Section 4 of Namespaces in XML
     * (1.0 or 1.1, depending on the version of XML being used).
     */
    public static final String ATTRIBUTE_ROLE = "role";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ROLE);
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

        CHILD_TAGS.add(Grapheme.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Example.TAG_NAME);
        CHILD_TAGS.add(Alias.TAG_NAME);
    }

    /**
     * Construct a new lexicon object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Lexeme() {
        super(null);
    }

    /**
     * Construct a new lexicon object.
     * @param node The encapsulated node.
     */
    Lexeme(final Node node) {
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
    private Lexeme(final Node n,
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
        return new Lexeme(n, factory);
    }

    /**
     * Retrieve the xml:id attribute.
     *
     * @return value of the xml:id attribute.
     * @see #ATTRIBUTE_XML_ID
     */
    public String getXmlId() {
        return getAttribute(ATTRIBUTE_XML_ID);
    }

    /**
     * Set the xml:id attribute.
     *
     * @param xmlId value of the xml:id attribute.
     * @see #ATTRIBUTE_XML_ID
     */
    public void setXmlId(final String xmlId) {
        setAttribute(ATTRIBUTE_XML_ID, xmlId);
    }

    /**
     * Retrieve the role attribute.
     *
     * @return Value of the role attribute.
     * @see #ATTRIBUTE_ROLE
     */
    public String getRole() {
        return getAttribute(ATTRIBUTE_ROLE);
    }

    /**
     * Set the xml:lang attribute.
     *
     * @param role Value of the xml:lang attribute.
     * @see #ATTRIBUTE_ROLE
     */
    public void setRole(final String role) {
        setAttribute(ATTRIBUTE_ROLE, role);
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
