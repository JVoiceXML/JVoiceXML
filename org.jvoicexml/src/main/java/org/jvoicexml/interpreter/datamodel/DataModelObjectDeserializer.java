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
package org.jvoicexml.interpreter.datamodel;

import javax.activation.MimeType;

import org.jvoicexml.event.error.SemanticError;

/**
 * Deserializer for objects that have been received from external systems
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public interface DataModelObjectDeserializer {
    /**
     * Retrieves the {@link MimeType} that is handled by this deserializer.
     * @return the MIME type
     */
    MimeType getMimeType();


    /**
     * Deserializes the given {@link Object} that has been retrieved from the
     * external systems so that it can be used by the {@link DataModel}.
     * 
     * @param model
     *            the employed data model
     * @param type
     *            the MIME type of the received object
     * @param object
     *            the object to deserialize.
     * @return the deserialized object
     * @throws SemanticError
     *             error deserializing the given object.
     */
    Object deserialize(DataModel model,
            MimeType type, Object object) throws SemanticError;
}
