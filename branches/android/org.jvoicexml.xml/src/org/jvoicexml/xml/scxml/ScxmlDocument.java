/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/scxml/ScxmlDocument.java $
 * Version: $LastChangedRevision: 3205 $
 * Date:    $Date: 2012-08-10 08:32:17 +0200 (Fri, 10 Aug 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.scxml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.ScxmlNode;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.XmlNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An SCXML XML document according to the specification in <a
 * href="http://www.w3.org/TR/scxml//">
 * http://www.w3.org/TR/scxml/</a>.
 *
 * <p>
 * Objects of this class can create SCXML XML  documents or
 * parse them.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3205 $
 * @since 0.7.6
 */
public final class ScxmlDocument extends XmlDocument {

    /** The serial version UID.  */
    private static final long serialVersionUID = 8531815223302622356L;

    /** The <code>XmlNodefactory</code> to use. */
    private static final transient ScxmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new ScxmlNodeFactory();
    }

    /**
     * Creates an empty SCXML Document.
     *
     * @throws ParserConfigurationException
     *         If anything goes wrong while parsing the document.
     */
    public ScxmlDocument()
            throws ParserConfigurationException {
        super();
    }

    /**
     * Constructs a new SCXML document from the given input source.
     *
     * @param source
     *        Input source for a single XML document.
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     * @throws SAXException
     *         Error parsing the input source.
     * @throws IOException
     *         Error reading the input source.
     */
    public ScxmlDocument(final InputSource source)
            throws ParserConfigurationException, SAXException, IOException {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlNodeFactory<?> getXmlNodefactory() {
        return NODE_FACTORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node createRootNode() {
        final Document document = getDocument();
        final Node node = document.createElement(Scxml.TAG_NAME);
        return new Scxml(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeList getXmlNodeList(final NodeList nodeList) {
        return new XmlNodeList<ScxmlNode>(NODE_FACTORY, nodeList);
    }

    /**
     * Get the one and only child of this document: The scxml node.
     *
     * @return The scxml child, <code>null</code> if there is
     *         none.
     */
    public Scxml getScxml() {
        final NodeList scxml = getElementsByTagName(Scxml.TAG_NAME);
        if (scxml.getLength() == 0) {
            return null;
        }

        return (Scxml) scxml.item(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultNamespaceURI() {
        return "http://www.w3.org/2005/07/scxml";
    }

}
