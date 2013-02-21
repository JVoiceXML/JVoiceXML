/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.DocumentTypeFactory;
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
 * A VoiceXML 2.0 or VoiceXML 2.1 document according to the specification in
 * specified at <a href="http://www.w3.org/TR/voicexml20/">
 * http://www.w3.org/TR/voicexml20/</a> and
 * <a href="http://www.w3.org/TR/voicexml21/">
 * http://www.w3.org/TR/voicexml21/</a>.
 *
 * <p>
 * VoiceXML is designed for creating audio dialogs that feature synthesized
 * speech, digitized audio, recognition of spoken and DTMF key input, recording
 * of spoken input, telephony and mixed initiative conversations. Its major goal
 * is to bring the advantages of web-based development and content delivery to
 * interactive voice response applications.
 * </p>
 *
 * <p>
 * Objects of this class can create such VoiceXML documents or parse them.
 * </p>
 *
 * <p>
 * The document type can be controlled via the
 * <code>jvoicexml.vxml.version</code> environment property. A value
 * of <code>2.0</code> sets the document type to {@link VoiceXml20DocumentType}
 * and a value of <code>2.1</code> sets the document type to
 * {@link VoiceXml21DocumentType}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class VoiceXmlDocument
        extends XmlDocument implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -5627050459434472927L;

    /** Name of the environment varible of the version. */
    static final String VXML_VERSION = "jvoicexml.vxml.version";

    /** The <code>XmlNodefactory</code> to use. */
    private static final transient VoiceXmlNodeFactory NODE_FACTORY;

    /** The document type factory to create document types. */
    private static transient DocumentTypeFactory documentTypeFactory;

    /** Cached document type. */
    private transient DocumentType documentType;

    static {
        NODE_FACTORY = new VoiceXmlNodeFactory();
    }

    /**
     * Create an empty VoiceXML document containing only the root Vxml element.
     *
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     */
    public VoiceXmlDocument()
            throws ParserConfigurationException {
        super();
        final Vxml vxml = getVxml();
        vxml.addDefaultAttributes();
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
     * Sets the document type factory. This method needs to be called once
     * per process if custom document types are to be supported.
     * @param factory the factory to use.
     */
    public static void setDocumentTypeFactory(
            final DocumentTypeFactory factory) {
        documentTypeFactory = factory;
    }

    /**
     * The Document Type Declaration (see <code>DocumentType</code>)
     * associated with this document.
     *
     * @return DocumentType
     */
    @Override
    public DocumentType getDoctype() {
        if (documentType != null) {
            return documentType;
        }

        final Document doc = getDocument();
        if (doc != null) {
            final DocumentType doctype = doc.getDoctype();
            if (doctype != null) {
                documentType = doctype;
                return doctype;
            }
        }

        if (documentTypeFactory != null) {
            documentType = documentTypeFactory.createDocumentType(this);
        } else {
            final String version = System.getProperty(VXML_VERSION);
            if (version != null) {
                if (version.equals("2.0")) {
                    documentType = new VoiceXml20DocumentType();
                } else if (version.equals("2.1")) {
                    documentType =  new VoiceXml21DocumentType();
                } else {
                    throw new IllegalArgumentException(
                         "environment variable jvoicexml.vxml.version must be "
                         + "set to 2.0 or 2.1!");
                }
            }
        }
        return documentType;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultNamespaceURI() {
        return Vxml.DEFAULT_XMLNS;
    }
}
