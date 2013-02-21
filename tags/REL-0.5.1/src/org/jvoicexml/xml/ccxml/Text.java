/*
 * File:    $RCSfile: Text.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/05/16 07:26:22 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ccxml;

import java.io.IOException;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlWriter;
import org.w3c.dom.Node;

/**
 * Implementaion of a text node.
 *
 * @author Steve Doyle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Text
        extends AbstractCcxmlNode {
    /** Name of the block tag. */
    public static final String TAG_NAME = "#text";

    /**
     * Construct a new text object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Text() {
        super(null);
    }

    /**
     * Construct a new text node.
     * @param node The encapsulated node.
     */
    public Text(final Node node) {
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
     * This is the primary method used to write an object and
     * its children as XML text. Implementations with children
     * should use writeChildrenXml to write those children, to
     * allow selective overriding.
     * @param writer XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    public void writeXml(final XmlWriter writer)
            throws IOException {
        writer.printIndent();
        writer.write(getNodeValue());
    }

    /**
     * Create a new instance for the given node.
     * @param n The node to encapsulate.
     * @return The new instance.
     */
    public XmlNode newInstance(final Node n) {
        return new Text(n);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }
}
