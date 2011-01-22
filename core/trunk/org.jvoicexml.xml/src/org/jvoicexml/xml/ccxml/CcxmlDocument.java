/*
 * File:    $RCSfile: CcxmlDocument.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.CcxmlNode;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.XmlNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A CCXML document according to the specifcation in
 * <a href="http://www.w3.org/TR/ccxml">http://www.w3.org/TR/ccxml</a>.
 *
 * <p>
 * CCXML is designed to provide telephony call control support for
 * dialog systems, such as VoiceXML [VOICEXML]. While CCXML can be used
 * with any dialog systems capable of handling media, CCXML has been
 * designed to complement and integrate with a VoiceXML interpreter.
 * </p>
 *
 * <p>
 * Objects of this class can create such CCXML documents or parse them.
 * </p>
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class CcxmlDocument
        extends XmlDocument {
    /** The serial version UID. */
    private static final long serialVersionUID = 4774076725232451813L;

    /** The <code>XmlNodefactory</code> to use. */
    private static final transient CcxmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new CcxmlNodeFactory();
    }

    /**
     * Create an empty CCXML document containg only the root Ccxml element.
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     */
    public CcxmlDocument()
            throws ParserConfigurationException {
        super();
    }

    /**
     * Constructs a new CCXML document from the given input source.
     * @param source Input source for a single XML document.
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     * @throws SAXException
     *         Error parsing the input source.
     * @throws IOException
     *         Error reading the input source.
     */
    public CcxmlDocument(final InputSource source)
            throws ParserConfigurationException, SAXException, IOException {
        super(source);
    }

    /**
     * Construct a new CCXML document with the given document.
     * @param doc Encapsulated document.
     */
    public CcxmlDocument(final Document doc) {
        super(doc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlNodeFactory<?> getXmlNodefactory() {
        return NODE_FACTORY;
    }

    /**
     * Create a new Ccxml node.
     * @return The new created ccxml.
     */
    @Override
    protected Node createRootNode() {
        final Document document = getDocument();
        final Node node = document.createElement(Ccxml.TAG_NAME);
        return new Ccxml(node);
    }

    /**
     * Get the one and only child of this document: The Ccxml node.
     * @return The Ccxml child, <code>null</code> if there is none.
     */
    public Ccxml getCcxml() {
        final NodeList ccxml = getElementsByTagName(Ccxml.TAG_NAME);
        if (ccxml.getLength() == 0) {
            return null;
        }

        return new Ccxml(ccxml.item(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeList getXmlNodeList(final NodeList nodeList) {
        return new XmlNodeList<CcxmlNode>(NODE_FACTORY, nodeList);
    }
}
