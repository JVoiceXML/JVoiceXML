/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import javax.activation.MimeType;

import org.jvoicexml.DocumentDescriptor;

/**
 * A mapper of {@link MimeType}s to identify objects type within JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
class MimeTypeMapper {
    /** The type to map. */
    private final MimeType type;
    
    /**
     * Constructs a new object.
     * @param mimeType the type to check
     */
    public MimeTypeMapper(final MimeType mimeType) {
        type = mimeType;
    }
    
    /**
     * Checks if the type matches an XML format.
     * @return {@code true} if the type matches an XML format
     */
    public boolean isXml() {
        if (type == null) {
            return false;
        }
        
        if (type.match(DocumentDescriptor.MIME_TYPE_XML)) {
            return true;
        }
        if (type.match(DocumentDescriptor.MIME_TYPE_SRGS_XML)) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks if the type matches an XML format.
     * @return {@code true} if the type matches an XML format
     */
    public boolean isText() {
        if (type == null) {
            return false;
        }
        
        if (type.match(DocumentDescriptor.MIME_TYPE_JSON)) {
            return true;
        }
        final String primary = type.getPrimaryType();
        if (primary.equalsIgnoreCase("text")) {
            return true;
        }
        
        return false;
    }

}
