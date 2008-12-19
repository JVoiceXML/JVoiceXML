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

package org.jvoicexml.interpreter;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard dialog variables.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class DialogShadowVarContainer
        extends ScriptableObject
        implements StandardSessionVariable {
    /** Name of the document variable. */
    public static final String VARIABLE_NAME = "dialog";

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
        return DialogShadowVarContainer.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves a variable of dialog scope.
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
    public void setScripting(final ScriptingEngine engine) {
        scripting = engine;
    }
}
