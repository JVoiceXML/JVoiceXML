/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/ssml/SayAs.java $
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

package org.jvoicexml.xml.ssml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.NodeHelper;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.Node;

/**
 * The say-as element allows the author to indicate information on the type
 * of text construct contained within the element and to help specify the
 * level of detail for rendering the contained text.
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 */

public final class SayAs
        extends AbstractSsmlNode implements VoiceXmlNode, TextContainer {

    /** Name of the tag. */
    public static final String TAG_NAME = "say-as";

    /**
     * indicates the content type of the contained text construct.
     */
    public static final String ATTRIBUTE_INTERPRET_AS = "interpret-as";

    /**
     * Provides hints on the precise formatting of the contained text
     * for content types that may have ambiguous formats.
     */
    public static final String ATTRIBUTE_FORMAT = "format";

    /**
     * Optional attribute that indicates the level of detail to be
     * read aloud or rendered.
     */
    public static final String ATTRIBUTE_DETAIL = "detail";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_DETAIL);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FORMAT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_INTERPRET_AS);
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

        CHILD_TAGS.add(Value.TAG_NAME);
    }

    /**
     * Construct a new say-as object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public SayAs() {
        super(null);
    }

    /**
     * Construct a new SayAs object.
     * @param node The encapsulated node.
     */
    SayAs(final Node node) {
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
    private SayAs(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Retrieve the interpret-as attribute.
     * @return Value of the interpret-as attribute.
     * @see #ATTRIBUTE_INTERPRET_AS
     */
    public String getInterpretAs() {
        return getAttribute(ATTRIBUTE_INTERPRET_AS);
    }

    /**
     * Set the interpret-as attribute.
     * @param interpretAs Value of the interpretAs attribute.
     * @see #ATTRIBUTE_INTERPRET_AS
     */
    public void setInterpretAs(final String interpretAs) {
        setAttribute(ATTRIBUTE_INTERPRET_AS, interpretAs);
    }

    /**
     * Retrieve the format attribute.
     * @return Value of the format attribute.
     * @see #ATTRIBUTE_FORMAT
     */
    public String getFormat() {
        return getAttribute(ATTRIBUTE_FORMAT);
    }

    /**
     * Set the format attribute.
     * @param format Value of the format attribute.
     * @see #ATTRIBUTE_FORMAT
     */
    public void setFormat(final String format) {
        setAttribute(ATTRIBUTE_FORMAT, format);
    }

    /**
     * Retrieve the detail attribute.
     * @return Value of the detail attribute.
     * @see #ATTRIBUTE_DETAIL
     */
    public String getDetail() {
        return getAttribute(ATTRIBUTE_DETAIL);
    }

    /**
     * Set the detail attribute.
     * @param detail Value of the detail attribute.
     * @see #ATTRIBUTE_DETAIL
     */
    public void setDetail(final String detail) {
        setAttribute(ATTRIBUTE_DETAIL, detail);
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
    @Override
    public Text addText(final String text) {
        return NodeHelper.addText(this, text);
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new SayAs(n, factory);
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
