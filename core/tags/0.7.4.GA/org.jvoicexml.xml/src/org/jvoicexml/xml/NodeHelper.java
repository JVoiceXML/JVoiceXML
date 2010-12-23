/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Helper methods for XML handling.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class NodeHelper {
    /**
     * Do not create.
     */
    private NodeHelper() {
    }

    /**
     * Utility method to add the given text the text container.
     * <p>
     * This method is intended to facilitate implementations of
     * {@link TextContainer#addText(String)}.
     * </p>
     * @param container the container where to add the text.
     * @param text the text to add.
     * @return added text.
     */
    public static Text addText(final TextContainer container,
            final String text) {
        if (text == null) {
            return null;
        }
        final Text textNode;
        final Node lastChild = container.getLastChild();
        if (lastChild instanceof Text) {
            textNode = (Text) lastChild;
            final String value = textNode.getNodeValue().trim();
            final StringBuilder str = new StringBuilder();
            str.append(value);
            str.append(' ');
            str.append(text.trim());
            textNode.setNodeValue(str.toString());
        } else {
            final Document document = container.getOwnerDocument();
            final Node node = document.createTextNode(text);
            final XmlNodeFactory<? extends XmlNode> factory =
                container.getNodeFactory();
            textNode = new Text(node, factory);
            container.appendChild(textNode);
        }
        return textNode;
    }
}
