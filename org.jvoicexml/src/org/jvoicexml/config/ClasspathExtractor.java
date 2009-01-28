/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Content handler to extract the classpath entries from a configuration file.
 * @author DS01191
 * @version $Revision$
 * @since 0.7
 */

final class ClasspathExtractor implements ContentHandler {
    /** Intermediately read string between two tags. */
    private StringBuilder str;

    /** Found classpath entries. */
    private Collection<URL> entries;

    /**
     * Retrieves the parsed classpath entries.
     * @return parsed classpath entries.
     */
    public URL[] getClasspathEntries() {
        final URL[] urls = new URL[entries.size()];
        return entries.toArray(urls);
    }

    /**
     * {@inheritDoc}
     */
    public void startDocument() throws SAXException {
        entries = new java.util.ArrayList<URL>();
    }


    /**
     * {@inheritDoc}
     */
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        if (str != null) {
            str.append(ch, start, length);
        }
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
        if (localName.equals("classpath")) {
            final String entry = str.toString();
            URL url;
            try {
                url = new URL("file:" + entry);
            } catch (MalformedURLException e) {
                throw new SAXException(e.getMessage(), e);
            }
            entries.add(url);
        }
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
        if (localName.equals("classpath")) {
            str = new StringBuilder();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startPrefixMapping(final String prefix, final String uri)
            throws SAXException {
    }

}
