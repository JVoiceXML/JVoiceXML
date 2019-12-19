/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.xml.vxml;

import javax.activation.MimeType;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.srgs.GrammarType;
import org.w3c.dom.Node;

/**
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 */
public final class JVoiceXmlData
        extends Data {
    /**
     * JVoiceXML extension to add MIME types of the data. Defaults to
     * {@link DataType#XML}.
     */
    public static final String ATTRIBUTE_JVOICEXML_TYPE = "type";

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES.add(ATTRIBUTE_JVOICEXML_TYPE);
    }

    /**
     * Construct a new data object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public JVoiceXmlData() {
        super(null);
    }

    /**
     * Construct a new data object.
     * @param node The encapsulated node.
     */
    JVoiceXmlData(final Node node) {
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
    private JVoiceXmlData(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new JVoiceXmlData(n, factory);
    }
    
    /**
     * Retrieves the type attribute.
     *
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public String getTypename() {
        final String type = getAttribute(ATTRIBUTE_JVOICEXML_TYPE);
        if (type == null) {
            return DataType.XML.toString();
        }
        return type;
    }

    /**
     * Retrieves the type attribute.
     *
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public GrammarType getType() {
        final String type = getTypename();
        return GrammarType.valueOfAttribute(type);
    }

    /**
     * Retrieves the type attribute as a {@link MimeType}.
     *
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public MimeType getTypeAsMimeType() {
        final GrammarType type = getType();
        return type.getType();
    }
    
    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_JVOICEXML_TYPE, type);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public void setType(final GrammarType type) {
        final String str = type.toString();

        setType(str);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_JVOICEXML_TYPE
     * @since 0.7.9
     */
    public void setType(final MimeType type) {
        final String str = type.toString();

        setType(str);
    }
}
