/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.config/src/org/jvoicexml/config/ClasspathExtractor.java $
 * Version: $LastChangedRevision: 2605 $
 * Date:    $Date: 2011-02-20 05:38:38 -0500 (So, 20 Feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.marc;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Content handler to extract the event id from a response from MARC.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2605 $
 * @since 0.7
 */
final class ResponseExtractor implements ContentHandler {
    /** The parsed event id. */
    private String eventId;

    /**
     * Retrieves the parsed event id.
     * @return parsed event id.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * {@inheritDoc}
     */
    public void startDocument() throws SAXException {
    }


    /**
     * {@inheritDoc}
     */
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument() throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(final String uri, final String localName,
            final String name) throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void endPrefixMapping(final String prefix) throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void ignorableWhitespace(final char[] ch, final int start,
            final int length) throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void processingInstruction(final String target, final String data)
            throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentLocator(final Locator locator) {
    }

    /**
     * {@inheritDoc}
     */
    public void skippedEntity(final String name) throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(final String uri, final String localName,
            final String name, final Attributes atts) throws SAXException {
        if (localName.equals("event")) {
            eventId = atts.getValue("id");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startPrefixMapping(final String prefix, final String uri)
            throws SAXException {
    }
}
