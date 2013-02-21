/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/VoiceXmlComment.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $LastChangedDate: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Implementation of a Comment node.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class VoiceXmlComment
        extends AbstractVoiceXmlNode {
    /** Name of the block tag. */
    public static final String TAG_NAME = "#comment";

    /**
     * Construct a new text object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public VoiceXmlComment() {
        super(null);
    }

    /**
     * Construct a new text node.
     * @param node The encapsulated node.
     */
    public VoiceXmlComment(final Node node) {
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
    private VoiceXmlComment(final Node n,
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
     * This is the primary method used to write an object and
     * its children as XML text. Implementations with children
     * should use writeChildrenXml to write those children, to
     * allow selective overriding.
     * @param writer XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    public void writeXml(final XMLStreamWriter writer)
            throws IOException {
        final String value = getNodeValue();
        try {
            writer.writeCharacters(value);
        } catch (XMLStreamException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new VoiceXmlComment(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }
}
