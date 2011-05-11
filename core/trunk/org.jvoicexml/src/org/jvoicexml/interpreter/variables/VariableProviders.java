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
package org.jvoicexml.interpreter.variables;

import java.util.Collection;
import java.util.Map;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.ScriptableObject;

/**
 * Variable providers.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class VariableProviders {
    /** Registered variable providers. */
    private final Map<Scope, VariableProvider> variableProviders;

    /**
     * Constructs a new object.
     */
    public VariableProviders() {
        variableProviders = new java.util.HashMap<Scope, VariableProvider>();
    }

    /**
     * Sets the variable providers.
     * @param providers the varibale providers.
     */
    public void setProviders(final Collection<VariableProvider> providers) {
        for (VariableProvider provider : providers) {
            final Scope scope = provider.getScope();
            variableProviders.put(scope, provider);
        }
    }

    /**
     * Creates all host objects for the given scope.
     * @param scripting the scripting engine
     * @param scope the current scope.
     * @throws SemanticError
     *         error creating the host object
     */
    public void createHostObjects(final ScriptingEngine scripting,
            final Scope scope) throws SemanticError {
        final VariableProvider provider = variableProviders.get(scope);
        if (provider == null) {
            return;
        }
        final Map<String, Class<ScriptableObject>> containers =
            provider.getVariableContainers();
        for (String name : containers.keySet()) {
            final Class<ScriptableObject> clazz = containers.get(name);
            scripting.createHostObject(name, clazz);
        }
    }
}
