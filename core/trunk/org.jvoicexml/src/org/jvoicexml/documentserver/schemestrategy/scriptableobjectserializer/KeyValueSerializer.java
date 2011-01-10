/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

import org.jvoicexml.documentserver.schemestrategy.ScriptableObjectSerializer;
import org.jvoicexml.event.error.SemanticError;
import org.mozilla.javascript.ScriptableObject;

/**
 * Serializes the scriptable objects as key-value pairs.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public class KeyValueSerializer implements ScriptableObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final String name, final ScriptableObject object)
        throws SemanticError {
        final StringBuilder str = new StringBuilder();
        serialize(object, name, str);
        return str.toString();
    }

    /**
     * Serializes the given object by appending it to the given
     * {@link StringBuilder}.
     * @param object the object to serialize
     * @param str serialized object
     */
    private void serialize(final ScriptableObject object,
            final String prefix, final StringBuilder str) {
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
                    subprefix = prefix + "." + "key";
                }
                serialize(scriptable, subprefix, str);
            } else {
                if (str.length() > 0) {
                    str.append("&");
                }
                if (!prefix.isEmpty()) {
                    str.append(prefix);
                    str.append(".");
                }
                str.append(key);
                str.append('=');
                str.append(value);
            }
        }
    }
}
