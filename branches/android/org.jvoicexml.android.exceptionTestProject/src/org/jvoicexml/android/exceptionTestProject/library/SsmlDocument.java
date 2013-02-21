/*
 * File:    $RCSfile: SsmlDocument.java,v $
 * Version: $Revision: 2526 $
 * Date:    $Date: 2011-01-24 02:42:33 -0600 (lun, 24 ene 2011) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.android.exceptionTestProject.library;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A SSML XML document according to the specifcation in <a
 * href="http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/">
 * http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/</a>.
 *
 * <p>
 * Objects of this class can create SSML XML documents or parse them.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2526 $
 * @since 0.5
 */
public final class SsmlDocument
        extends XmlDocument {
    /** The serial version UID. */
    private static final long serialVersionUID = -1716883656994858759L;

    /** The <code>XmlNodefactory</code> to use. */
    private static final transient SsmlNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new SsmlNodeFactory();
    }

    /**
     * Creates an empty SSML Document.
     *
     * @throws ParserConfigurationException
     *         If anything goes wrong while parsing the document.
     */
    public SsmlDocument()
            throws ParserConfigurationException {
        super();
    }

    /**
     * Creates a new SSML document from the given input source.
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
    public SsmlDocument(final InputSource source)
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
        final Node node = document.createElement(Speak.TAG_NAME);

        return new Speak(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeList getXmlNodeList(final NodeList nodeList) {
        return new XmlNodeList<SsmlNode>(NODE_FACTORY, nodeList);
    }

    /**
     * Get the one and only child of this document: The speak node.
     *
     * @return The speak child, <code>null</code> if there is
     *         none.
     */
    public Speak getSpeak() {
        final NodeList speak = getElementsByTagName(Speak.TAG_NAME);
        if (speak.getLength() == 0) {
            return null;
        }

        return new Speak(speak.item(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultNamespaceURI() {
        return Speak.DEFAULT_XMLNS;
    }
}
