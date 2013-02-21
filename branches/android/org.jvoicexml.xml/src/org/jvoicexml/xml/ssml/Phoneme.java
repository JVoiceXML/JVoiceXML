/*
 * File:    $RCSfile: Phoneme.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $Author: schnelle $
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

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The phoneme element provides a phonemic/phonetic pronunciation for the
 * contained text. The phoneme element may be empty. However, it is recommended
 * that the element contain human-readable text that can be used for non-spoken
 * rendering of the document. For example, the content may be displayed
 * visually for users with hearing impairments.
 *
 * @author Steve Doyle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Phoneme
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "phoneme";

    /**
     * Required attribute that specifies the phoneme/phone string.
     */
    public static final String ATTRIBUTE_PH = "ph";

    /**
     * Optional attribute that specifies the phonemic/phonetic alphabet. An
     * alphabet in this context refers to a collection of symbols to represent
     * the sounds of one or more human languages.
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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_PH);
    }

    /**
     * Construct a new phoneme object without a node.
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
     * Construct a new phoneme object.
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
     * Retrieve the ph attribute.
     * @return Value of the ph attribute.
     * @see #ATTRIBUTE_PH
     */
    public String getPh() {
        return getAttribute(ATTRIBUTE_PH);
    }

    /**
     * Set the ph attribute.
     * @param ph Value of the ph attribute.
     * @see #ATTRIBUTE_PH
     */
    public void setPh(final String ph) {
        setAttribute(ATTRIBUTE_PH, ph);
    }

    /**
     * Retrieve the alphabet attribute.
     * @return Value of the alphabet attribute.
     * @see #ATTRIBUTE_ALPHABET
     */
    public String getAlphabet() {
        return getAttribute(ATTRIBUTE_ALPHABET);
    }

    /**
     * Set the alphabet attribute.
     * @param alphabet Value of the alphabet attribute.
     * @see #ATTRIBUTE_ALPHABET
     */
    public void setAlphabet(final String alphabet) {
        setAttribute(ATTRIBUTE_ALPHABET, alphabet);
    }

    /**
     * Create a new text within this block.
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
