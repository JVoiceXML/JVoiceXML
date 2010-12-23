/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.interpreter.ScriptingEngine;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard document variables.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class DocumentShadowVarContainer
        extends ScriptableObject
        implements StandardSessionVariable {
    /** The serial version UID. */
    private static final long serialVersionUID = 3698216563262612372L;

    /** Name of the document variable. */
    public static final String VARIABLE_NAME = "document";

    /** Reference to the scripting engine. */
    private ScriptingEngine scripting;


    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return DocumentShadowVarContainer.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves a variable of document scope.
     *
     * @since 0.7
     */
    @Override
    public Object get(final String name, final Scriptable start) {
        if (has(name, start)) {
            return super.get(name, start);
        }
        return scripting.getVariable(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String name, final Scriptable start,
            final Object value) {
        if (scripting == null || has(name, start)) {
            super.put(name, start, value);
        } else {
            scripting.setVariable(name, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setScripting(final ScriptingEngine engine) {
        scripting = engine;
    }
}
