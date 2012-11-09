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
 * A <code>&lt;lexeme&gt;</code> element MAY contain one or more
 * <code>&lt;alias&gt;</code> elements which are used to indicate the
 * pronunciation of an acronym or an abbreviated term, in terms of other
 * orthographies, or other substitutions as necessary.
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
public final class Alias
        extends AbstractPlsNode {
    /** Name of the tag. */
    public static final String TAG_NAME = "alias";

    /**
     * OPTIONAL prefer attribute analogous to the prefer attribute for the
     * <code>&lt;phoneme&gt;</code> element.
     */
    public static final String ATTRIBUTE_PREFER = "prefer";

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
    }

    /**
     * Construct a new object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Alias() {
        super(null);
    }

    /**
     * Construct a new object.
     * @param node The encapsulated node.
     */
    Alias(final Node node) {
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
    private Alias(final Node n,
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
        return new Alias(n, factory);
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
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
