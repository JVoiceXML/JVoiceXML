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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A filter for all the JVoiceXML relevant configuration to obtain a spring
 * beans configuration source.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
final class BeansFilter extends XMLFilterImpl {
    /** Flag indicating that the current tag has to be ignored. */
    private boolean ignoreTag;

    /**
     * Constructs a new object.
     * @param parent the parent reader.
     */
    public BeansFilter(final XMLReader parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String name, final Attributes atts) throws SAXException {
        if (ignoreTag) {
            return;
        }
        if (localName.equals("classpath")) {
            ignoreTag = true;
            return;
        } else if (localName.equals("implementation")) {
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "xmlns", "xmlns", "NMTOKEN",
                "http://www.springframework.org/schema/beans");
            attributes.addAttribute("", "xmlns", "xmlns:xsi", "NMTOKEN",
                "http://www.w3.org/2001/XMLSchema-instance");
            attributes.addAttribute("", "xsi", "xsi:schemaLocation", "NMTOKEN",
                "http://www.springframework.org/schema/beans "
                    + "http://www.springframework.org/schema/beans/"
                    + "spring-beans-2.0.xsd");
            super.startElement(uri, "beans", "beans", attributes);
        } else {
            super.startElement(uri, localName, localName, atts);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        if (ignoreTag) {
            return;
        }
        super.characters(ch, start, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ignorableWhitespace(final char[] ch, final int start,
            final int length) throws SAXException {
        if (ignoreTag) {
            return;
        }
        super.ignorableWhitespace(ch, start, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName,
            final String name) throws SAXException {
        if (localName.equals("classpath")) {
            ignoreTag = false;
            return;
        } else if (localName.equals("implementation")) {
            super.endElement(uri, "beans", "beans");
        } else {
            super.endElement(uri, localName, localName);
        }
    }


}
