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
package org.jvoicexml.interpreter.datamodel.ecmascript.deserializer;

import javax.activation.MimeType;

import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectDeserializer;
import org.w3c.dom.Document;

/**
 * A deserializer for XML DOM into ECMAScript objects.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class XMLDeserializer implements DataModelObjectDeserializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeType getMimeType() {
        return DocumentDescriptor.MIME_TYPE_XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(final DataModel model, final MimeType type,
            final Object object) throws SemanticError {
        if (object == null) {
            return null;
        }
        if (object instanceof Document) {
            return object;
        }
        throw new SemanticError("unable to convert object of type '"
                + object.getClass().getCanonicalName() + "' to XML");
    }

}
