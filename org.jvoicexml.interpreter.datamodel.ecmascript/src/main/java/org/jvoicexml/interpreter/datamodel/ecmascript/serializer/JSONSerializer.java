/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel.ecmascript.serializer;

import java.util.Collection;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;

/**
 * Serializes the scriptable objects as a JSON object..
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class JSONSerializer implements DataModelObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<KeyValuePair> serialize(final DataModel model,
            final String name, final Object value) throws SemanticError {
        final Collection<KeyValuePair> pairs =
                new java.util.ArrayList<KeyValuePair>();
        final String str = model.toString(value);
        final KeyValuePair pair = new KeyValuePair(name, str);
        pairs.add(pair);
        return pairs;
    }
}
