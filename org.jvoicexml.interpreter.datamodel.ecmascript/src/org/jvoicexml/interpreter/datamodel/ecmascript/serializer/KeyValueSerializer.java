/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/scriptableobjectserializer/KeyValueSerializer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
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
package org.jvoicexml.interpreter.datamodel.ecmascript.serializer;

import java.util.Collection;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Serializes the scriptable objects as key-value pairs.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.5
 */
public final class KeyValueSerializer implements DataModelObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<KeyValuePair> serialize(final DataModel model,
            final String name, final Object value) throws SemanticError {
        final Collection<KeyValuePair> pairs = new java.util.ArrayList<KeyValuePair>();
        if (value instanceof Scriptable) {
            final Scriptable scriptable = (Scriptable) value;
            serialize(scriptable, name, pairs);
        } else {

        }
        return pairs;
    }

    /**
     * Serializes the given object by appending the known values to the current
     * object prefix into pairs.
     * 
     * @param object
     *            the object to serialize
     * @param prefix
     *            the current object prefix.
     * @param pairs
     *            currently serialized values
     */
    private void serialize(final Scriptable object, final String prefix,
            final Collection<KeyValuePair> pairs) {
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        for (Object id : ids) {
            final String key = id.toString();
            final Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final ScriptableObject scriptable = (ScriptableObject) value;
                final String subprefix;
                if (prefix.isEmpty()) {
                    subprefix = key;
                } else {
                    subprefix = prefix + "." + key;
                }
                serialize(scriptable, subprefix, pairs);
            } else if (value != null) {
                final StringBuilder str = new StringBuilder();
                str.append(prefix);
                str.append(".");
                str.append(key);
                final KeyValuePair pair = new KeyValuePair(str.toString(),
                        value.toString());
                pairs.add(pair);
            }
        }
    }
}
