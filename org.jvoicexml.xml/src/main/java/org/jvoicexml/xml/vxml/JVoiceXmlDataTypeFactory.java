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

package org.jvoicexml.xml.vxml;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * Factory for the default data types.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public final class JVoiceXmlDataTypeFactory implements DataTypeFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType(final String attribute) {
        if (attribute == null) {
            return null;
        }
        final MimeType other;
        try {
            other = new MimeType(attribute);
        } catch (MimeTypeParseException e) {
            return null;
        }
        if (DataType.XML.getType().match(other)) {
            return DataType.XML;
        }
        if (DataType.JSON.getType().match(other)) {
            return DataType.JSON;
        }
        return null;
    }

}
