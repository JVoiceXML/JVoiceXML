/* File:    $RCSfile: VoiceXmlDocumentType.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.vxml;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The <code>DOCTYPE</code> of a VoiceXML 2.0 document.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
public final class VoiceXml20DocumentType
        extends AbstractVoiceXmlNode implements DocumentType {
    /**
     * Construct a new object.
     * @param n The encapsulated node.
     */
    public VoiceXml20DocumentType(final Node n) {
        super(n);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private VoiceXml20DocumentType(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    public String getTagName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new VoiceXml20DocumentType(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String childName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "vxml";
    }

    /**
     * {@inheritDoc}
     */
    public NamedNodeMap getEntities() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NamedNodeMap getNotations() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getPublicId() {
        return "-//W3C//DTD VOICEXML 2.0//EN";
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemId() {
        return "http://www.w3.org/TR/voicexml20/vxml.dtd";
    }

    /**
     * {@inheritDoc}
     */
    public String getInternalSubset() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "<!DOCTYPE " + getName() + " PUBLIC \"" + getPublicId()
                + "\" \"" + getSystemId() + "\">";
    }
}
