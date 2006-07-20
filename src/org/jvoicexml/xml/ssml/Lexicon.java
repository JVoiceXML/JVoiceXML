/*
 * File:    $RCSfile: Lexicon.java,v $
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

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * An SSML document may reference one or more external pronunciation lexicon
 * documents. A lexicon document is identified by a URI with an optional
 * media type. No standard lexicon media type has yet been defined as the
 * default for this specification.
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
public final class Lexicon
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "lexicon";

    /**
     * Identifies the location of the pronunciation lexicon document.
     */
    public static final String ATTRIBUTE_URI = "uri";

    /**
     * Specifies the media type of the pronunciation lexicon document.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_URI);
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
        return new Lexicon(n);
    }

    /**
     * Retrieve the uri attribute.
     * @return Value of the uri attribute.
     * @see #ATTRIBUTE_URI
     */
    public String getUri() {
        return getAttribute(ATTRIBUTE_URI);
    }

    /**
     * Set the uri attribute.
     * @param uri Value of the uri attribute.
     * @see #ATTRIBUTE_URI
     */
    public void setUri(final String uri) {
        setAttribute(ATTRIBUTE_URI, uri);
    }

    /**
     * Retrieve the type attribute.
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Set the type attribute.
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
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
