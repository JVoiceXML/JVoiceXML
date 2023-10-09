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
package org.jvoicexml.mmi.events.json;

import java.lang.reflect.Type;

import org.jvoicexml.mmi.events.ClearContextResponse;

/**
 * A deserializer for {@link ClearContextResponse}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class ClearContextResponseDeserializer
    extends LifeCycleResponseDeserializer<ClearContextResponse> {
    /**
     * Constructs a new object assuming the data field contains any
     * {@link Object}.
     */
    ClearContextResponseDeserializer() {
    }
    
    /**
     * Constructs a new object assuming the data field containing an object of
     * type {@code type}.
     * @param data type of the object in the data field
     * @param statusInfo type of the object in the status field
     */
    ClearContextResponseDeserializer(final Type data, final Type statusInfo) {
        super(data, statusInfo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    ClearContextResponse createLifeCycleEvent() {
        return new ClearContextResponse();
    }
}
