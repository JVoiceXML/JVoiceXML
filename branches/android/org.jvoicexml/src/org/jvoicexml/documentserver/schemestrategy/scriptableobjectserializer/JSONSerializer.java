/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/scriptableobjectserializer/JSONSerializer.java $
 * Version: $LastChangedRevision: 2674 $
 * Date:    $Date: 2011-05-24 03:58:04 -0500 (mar, 24 may 2011) $
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
package org.jvoicexml.documentserver.schemestrategy.scriptableobjectserializer;

import java.util.Collection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jvoicexml.documentserver.schemestrategy.ScriptableObjectSerializer;
import org.jvoicexml.event.error.SemanticError;
import org.mozilla.javascript.ScriptableObject;

/**
 * Serializes the scriptable objects as a JSON object..
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2674 $
 * @since 0.7.5
 */
public final class JSONSerializer implements ScriptableObjectSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NameValuePair> serialize(final String name,
            final ScriptableObject object)
        throws SemanticError {
        final Collection<NameValuePair> pairs =
            new java.util.ArrayList<NameValuePair>();
        final JSONObject json = toJSONObject(object);
        final String str = json.toString();
        final NameValuePair pair = new BasicNameValuePair(name, str);
        pairs.add(pair);
        return pairs;
    }

    /**
     * Transforms the given {@link ScriptableObject} into a JSON object.
     * @param object the object to serialize 
     * @return JSON object
     */
    @SuppressWarnings("unchecked")
    private JSONObject toJSONObject(final ScriptableObject object) {
        if (object == null) {
            return null;
        }
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        final JSONObject json = new JSONObject();
        for (Object id : ids) {
            final String key = id.toString();
            Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final ScriptableObject scriptable = (ScriptableObject) value;
                final JSONObject subvalue = toJSONObject(scriptable);
                try {
                    json.put(key, subvalue);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return json;
    }
}
