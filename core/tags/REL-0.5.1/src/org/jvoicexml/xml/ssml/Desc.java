/*
 * File:    $RCSfile: Desc.java,v $
 * Version: $Revision: 1.7 $
 * Date:    $Date: 2006/05/16 07:26:22 $
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The desc element can only occur within the content of the audio element.
 * When the audio source referenced in audio is not speech, e.g. audio
 * wallpaper or sonicon punctuation, it should contain a desc element whose
 * textual content is a description of the audio source (e.g. "door slamming").
 *
 * @see org.jvoicexml.xml.ssml.Audio
 *
 * @author Steve Doyle
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Desc
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "desc";

    /**
     * Used to indicate that the content of the element is in a different
     * language from that of the content surrounding the element.
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
     * Construct a new desc object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Desc() {
        super(null);
    }

    /**
     * Construct a new desc object.
     * @param node The encapsulated node.
     */
    Desc(final Node node) {
        super(node);
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
    public XmlNode newInstance(final Node n) {
        return new Desc(n);
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
