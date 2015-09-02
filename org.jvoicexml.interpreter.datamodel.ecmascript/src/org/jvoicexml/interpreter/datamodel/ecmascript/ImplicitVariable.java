/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/variables/SessionShadowVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel.ecmascript;

import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

/**
 * Component that provides a container for the shadowed variables for the
 * standard session variables.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public final class ImplicitVariable extends ScriptableObject {
    /** Reference to the scripting engine. */
    private EcmaScriptDataModel model;

    /** The default scope for this implicit variable. */
    private Scope scope;

    /**
     * Constructs a new objects.
     */
    @JSConstructor
    public ImplicitVariable() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return ImplicitVariable.class.getSimpleName();
    }

    /**
     * Retrieves the scope for the given name.
     * 
     * @param name
     *            name of a property
     * @return the corresponding scope or {@code null} if the property does not
     *         denote a scope
     */
    private Scope getScope(final String name) {
        if (name.equalsIgnoreCase(Scope.SESSION.getName())) {
            return Scope.SESSION;
        } else if (name.equalsIgnoreCase(Scope.APPLICATION.getName())) {
            return Scope.APPLICATION;
        } else if (name.equalsIgnoreCase(Scope.DOCUMENT.getName())) {
            return Scope.DOCUMENT;
        } else if (name.equalsIgnoreCase(Scope.DIALOG.getName())) {
            return Scope.DIALOG;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String name, Scriptable start) {
        final Scope scope = getScope(name);
        if (scope == null) {
            return super.has(name, start);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final String name, final Scriptable start) {
        final Scope scope = getScope(name);
        if (scope == null) {
            return super.get(name, start);
        }
        return model.getScriptable(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String name, final Scriptable start,
            final Object value) {
        if (model == null) {
            super.put(name, start, value);
            return;
        }
        final Scope scope = getScope(name);
        if (scope == null) {
            super.put(name, start, value);
        } else {
            final Scriptable scriptable = model.getScriptable(scope);
            if (this.equals(scriptable)) {
                super.put(name, start, value);
            } else {
                scriptable.put(name, scriptable, value);
            }
        }
    }

    /**
     * Sets the data model.
     * 
     * @param datamodel
     *            the datamodel
     */
    public void setDatamodel(final EcmaScriptDataModel datamodel) {
        model = datamodel;
    }

    /**
     * Sets the default scope.
     * 
     * @param defaultScope
     *            the default scope
     */
    public void setScope(final Scope defaultScope) {
        scope = defaultScope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(ImplicitVariable.class.getCanonicalName());
        str.append('[');
        str.append(scope);
        str.append(']');
        return str.toString();
    }
}
