/*
 * File:    $RCSfile: SrgsXmlDocument.java,v $
 * Version: $Revision: 1.7 $
 * Date:    $Date: 2006/07/17 14:21:01 $
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
package org.jvoicexml.xml.srgs;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.SrgsNode;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.XmlNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A SRGS XML document according to the specifcation in <a
 * href="http://www.w3.org/TR/2004/REC-speech-grammar-20040316/">
 * http://www.w3.org/TR/2004/REC-speech-grammar-20040316/</a>.
 *
 * <p>
 * Objects of this class can create SRGS XML grammar documents or
 * parse them.
 * </p>
 *
 * @author Christoph Buente
 *
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class SrgsXmlDocument
        extends XmlDocument {
    /** The <code>XmlNodefactory</code> to use. */
    private static final SrgsNodeFactory NODE_FACTORY;

    static {
        NODE_FACTORY = new SrgsNodeFactory();
    }

    /**
     * Creates an empty XML Grammar Document.
     *
     * @throws ParserConfigurationException
     *         If anything goes wrong while parsing the document.
     */
    public SrgsXmlDocument()
            throws ParserConfigurationException {
        super();
    }

    /**
     * Constructs a new Grammar document from the given input source.
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
    public SrgsXmlDocument(final InputSource source)
            throws ParserConfigurationException, SAXException, IOException {
        super(source);
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
     */
    @Override
    protected Node createRootNode() {
        final Document document = getDocument();
        final Node node = document.createElement(Grammar.TAG_NAME);
        return new Grammar(node);
    }

    /**
     * {@inheritDoc}
     *
     * @todo Fina a factory concept for the grammar nodes.
     */
    @Override
    protected NodeList getXmlNodeList(final NodeList nodeList) {
        return new XmlNodeList<SrgsNode>(NODE_FACTORY, nodeList);
    }

    /**
     * Get the one and only child of this document: The grammar node.
     *
     * @return The grammar child, <code>null</code> if there is
     *         none.
     */
    public Grammar getGrammar() {
        final NodeList grammar = getElementsByTagName(Grammar.TAG_NAME);
        if (grammar.getLength() == 0) {
            return null;
        }

        return new Grammar(grammar.item(0));
    }

}
