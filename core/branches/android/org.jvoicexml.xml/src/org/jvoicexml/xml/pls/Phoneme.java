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

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A <code>&lt;lexeme&gt;</code> MAY contain one or more
 * <code>&lt;phoneme&gt;</code> elements. The <code>&lt;phoneme&gt;</code>
 * element contains text describing how the <code>&lt;lexeme&gt;</code>
 * is pronounced.
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
public final class Phoneme
        extends AbstractPlsNode {
    /** Name of the tag. */
    public static final String TAG_NAME = "phoneme";

    /**
     * The prefer is an OPTIONAL attribute, which indicates the pronunciation
     * that MUST be used by a speech synthesis engine when it is set to
     * <code>"true"</code>.
     * The possible values are: <code>"true"</code> or <code>"false"</code>.
     * The default value is <code>"false"</code>.
     */
    public static final String ATTRIBUTE_PREFER = "prefer";

    /**
     * A <code>&lt;phoneme&gt;</code> element MAY have an alphabet attribute,
     * which indicates the pronunciation alphabet that is used for this
     * <code>&lt;phoneme&gt;</code> element only.
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

        ATTRIBUTE_NAMES.add(ATTRIBUTE_PREFER);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ALPHABET);
    }

    /**
     * Construct a new object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Phoneme() {
        super(null);
    }

    /**
     * Construct a new object.
     * @param node The encapsulated node.
     */
    Phoneme(final Node node) {
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
    private Phoneme(final Node n,
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
        return new Phoneme(n, factory);
    }
    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Create a new text within this grapheme element.
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
     * Retrieve the prefer attribute.
     *
     * @return Value of the version attribute.
     * @see #ATTRIBUTE_PREFER
     */
    public String getPrefer() {
        return getAttribute(ATTRIBUTE_PREFER);
    }

    /**
     * Set the prefer attribute.
     *
     * @param prefer Value of the prefer attribute.
     * @see #ATTRIBUTE_PREFER
     */
    public void setPrefer(final String prefer) {
        setAttribute(ATTRIBUTE_PREFER, prefer);
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
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
