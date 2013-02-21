/*
 * File:    $RCSfile: Text.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Node;

/**
 * Implementation of a text node.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public class Text
    extends AbstractXmlNode
    implements VoiceXmlNode, SrgsNode, SsmlNode, CcxmlNode, PlsNode {
    /** Name of the text tag. */
    public static final String TAG_NAME = "#text";

    /**
     * Constructs a new text object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.XmlNodeFactory
     */
    public Text() {
        super(null, null);
    }

    /**
     * Construct a new text object.
     * @param n The encapsulated node.
     * @param nodeFactory The node factory.
     */
    public Text(final Node n, final XmlNodeFactory<? extends XmlNode>
                nodeFactory) {
        super(n, nodeFactory);
    }

    /**
     * {@inheritDoc}
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * {@inheritDoc}
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
        return new Text(n, factory);
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>false</code> since this tag cannot contain child nodes.
     */
    protected final boolean canContainChild(final String tagName) {
        return false;
    }
}
