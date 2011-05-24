/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.schemestrategy.scriptableobjectserializer;

import java.util.Collection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jvoicexml.documentserver.schemestrategy.ScriptableObjectSerializer;
import org.jvoicexml.event.error.SemanticError;
import org.mozilla.javascript.ScriptableObject;

/**
 * Serializes the scriptable objects as key-value pairs.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class KeyValueSerializer implements ScriptableObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NameValuePair> serialize(final String name,
            final ScriptableObject object)
        throws SemanticError {
        final Collection<NameValuePair> pairs =
            new java.util.ArrayList<NameValuePair>();
        serialize(object, name, pairs);
        return pairs;
    }

    /**
     * Serializes the given object by appending the known values to the
     * current object prefix into pairs.
     * @param object the object to serialize
     * @param prefix the current object prefix.
     * @param pairs currently serialized values
     */
    private void serialize(final ScriptableObject object,
            final String prefix, Collection<NameValuePair> pairs) {
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        for (Object id : ids) {
            final String key  = id.toString();
            final Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final ScriptableObject scriptable =
                    (ScriptableObject) value;
                final String subprefix;
                if (prefix.isEmpty()) {
                    subprefix = key;
                } else {
                    subprefix = prefix + "." + key;
                }
                serialize(scriptable, subprefix, pairs);
            } else if (value != null){
                final StringBuilder str = new StringBuilder();
                str.append(prefix);
                str.append(".");
                str.append(key);
                final NameValuePair pair = new BasicNameValuePair(
                        str.toString(), value.toString());
                pairs.add(pair);
            }
        }
    }
}
