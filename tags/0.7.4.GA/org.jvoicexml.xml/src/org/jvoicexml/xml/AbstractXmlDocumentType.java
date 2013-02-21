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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */


package org.jvoicexml.xml;

import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

/**
 * Base class for a <code>DOCTYPE</code> node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
@SuppressWarnings("serial")
public abstract class AbstractXmlDocumentType extends AbstractXmlNode
    implements DocumentType, Serializable {
    /** Factory for new document types. */
    private static final DOMImplementation DOM_IMPLEMENTATION;

    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        DOMImplementation dom = null;
        try {
            db = factory.newDocumentBuilder();
            dom = db.getDOMImplementation();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        DOM_IMPLEMENTATION = dom;
    }

    /**
     * Constructs a new objects.
     * @param name the qualified name of the document type to be created.
     * @param publicId the external subset public identifier.
     * @param systemId the external subset system identifier.
     */
    public AbstractXmlDocumentType(final String name, final String publicId,
            final String systemId) {
        super(DOM_IMPLEMENTATION.createDocumentType(name, publicId, systemId),
                null);
    }

    /**
     * Selector for the encapsulated document type.
     * @return the encapsulated document type.
     */
    private DocumentType getDocumentType() {
        return (DocumentType) getNode();
    }

    /**
     * {@inheritDoc}
     */
    public final String getName() {
        final DocumentType  type = getDocumentType();
        return type.getName();
    }

    /**
     * {@inheritDoc}
     */
    public final String getTagName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final String getPublicId() {
        final DocumentType  type = getDocumentType();
        return type.getPublicId();
    }

    /**
     * {@inheritDoc}
     */
    public final String getSystemId() {
        final DocumentType  type = getDocumentType();
        return type.getSystemId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canContainChild(final String childName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final NamedNodeMap getEntities() {
        final DocumentType  type = getDocumentType();
        return type.getEntities();
    }

    /**
     * {@inheritDoc}
     */
    public final NamedNodeMap getNotations() {
        final DocumentType  type = getDocumentType();
        return type.getNotations();
    }

    /**
     * {@inheritDoc}
     */
    public final String getInternalSubset() {
        final DocumentType  type = getDocumentType();
        return type.getInternalSubset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "<!DOCTYPE " + getName() + " PUBLIC \"" + getPublicId()
                + "\" \"" + getSystemId() + "\">";
    }
}
