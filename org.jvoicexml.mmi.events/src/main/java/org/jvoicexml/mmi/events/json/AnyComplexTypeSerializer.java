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
import java.util.List;

import org.jvoicexml.mmi.events.AnyComplexType;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A JSON serializer for {@link AnyComplexType}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class AnyComplexTypeSerializer implements JsonSerializer<AnyComplexType> {

    /**
     * Removes the content attribute from the serialized JSON.. 
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(AnyComplexType src, Type typeOfSrc,
            JsonSerializationContext context) {
        final  List<Object> content = src.getContent();
        return context.serialize(content);
    }

}
