/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/variables/VariableProvider.java $
 * Version: $LastChangedRevision: 2650 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.variables;

import java.util.Map;

import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.ScriptableObject;

/**
 * Storage for user defined var containers.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2650 $
 * @since 0.7.5
 */
public final class VariableProvider {
    /** Scope of the variables in this container. */
    private final Scope scope;

    /** Configured variable containers. */
    private final Map<String, Class<ScriptableObject>> containers;

    /**
     * Constructs a new object.
     * @param sc the scope of variables in this container
     */
    public VariableProvider(final String sc) {
        scope = Scope.valueOf(sc);
        containers = new java.util.HashMap<String, Class<ScriptableObject>>();
    }

    /**
     * Retrieves the scope.
     * @return the scope.
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Sets the user defined var containers.
     * @param values user defined var containers.
     * @exception ClassNotFoundException
     *            the specified var container could not be loaded
     */
    public void setContainers(final Map<String, String> values)
        throws ClassNotFoundException {
        final ClassLoader loader =
            VariableProvider.class.getClassLoader();
        for (String key : values.keySet()) {
            final String name = values.get(key);
            @SuppressWarnings("unchecked")
            final Class<ScriptableObject> clazz =
                (Class<ScriptableObject>) loader.loadClass(name);
            containers.put(key, clazz);
        }
    }

    /**
     * Retrieves the known variable providers together with their name. 
     * @return known variable providers
     */
    public Map<String, Class<ScriptableObject>> getVariableContainers() {
        return containers;
    }
}
