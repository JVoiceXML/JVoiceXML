/*
 * File:    $RCSfile: VoiceXmlDocument.java,v $
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

package org.jvoicexml.xml.vxml;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.XmlNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A VoiceXML document according to the specifcation in <a
 * href="http://www.w3.org/TR/2005/REC-voicexml20-20050316">
 * http://www.w3.org/TR/2005/REC-voicexml20-20050316</a>.
 *
 * <p>
 * VoiceXML is designed for creating audio dialogs that feature synthesized
 * speech, digitized audio, regognition of spoken and DTMF key input, recording
 * of spoken input, telephony and mixed initiative conversations. Its major goal
 * is to bring the advantages of web-based development and content delivery to
 * interactive voiceresponse applications.
 * </p>
 *
 * <p>
 * Objects of this class can create such VoiceXML documents or parse them.
 * </p>
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
public final class VoiceXmlDocument
        extends XmlDocument implements Serializable {
    /** The serial version UID. */
    static final long serialVersionUID = 8692660905150924226L;

    /** The <code>XmlNodefactory</code> to use. */
    private static final VoiceXmlNodeFactory NODE_FACTORY;

    /** The document type of all VoiceXML documents. */
    private static VoiceXmlDocumentType vxmlDocumentType =
            new VoiceXmlDocumentType(null);

    static {
        NODE_FACTORY = new VoiceXmlNodeFactory();
    }

    /**
     * Create an empty VoiceXML document containg only the root Vxml element.
     *
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     */
    public VoiceXmlDocument()
            throws ParserConfigurationException {
        super();
    }

    /**
     * Constructs a new VoiceXML document from the given input source.
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
    public VoiceXmlDocument(final InputSource source)
            throws ParserConfigurationException, SAXException, IOException {
        super(source);
    }

    /**
     * Construct a new VoiceXML document with the given document.
     *
     * @param doc
     *        Encapsulated document.
     */
    public VoiceXmlDocument(final Document doc) {
        super(doc);
    }

    /**
     * The Document Type Declaration (see <code>DocumentType</code>)
     * associated with this document.
     *
     * @return DocumentType
     */
    public DocumentType getDoctype() {
        return null; //vxmlDocumentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlNodeFactory getXmlNodefactory() {
        return NODE_FACTORY;
    }

    /**
     * {@inheritDoc}
     *
     * Creates a new Vxml node.
     *
     * @return The new created vxml.
     */
    @Override
    protected Node createRootNode() {
        final Document document = getDocument();
        final Node node = document.createElement(Vxml.TAG_NAME);
        return new Vxml(node);
    }

    /**
     * Get the one and only child of this document: The Vxml node.
     *
     * @return The Vxml child, <code>null</code> if there is none.
     */
    public Vxml getVxml() {
        final NodeList vxml = getElementsByTagName(Vxml.TAG_NAME);
        if (vxml.getLength() == 0) {
            return null;
        }

        return (Vxml) vxml.item(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeList getXmlNodeList(final NodeList nodeList) {
        return new XmlNodeList<VoiceXmlNode>(NODE_FACTORY, nodeList);
    }
}
