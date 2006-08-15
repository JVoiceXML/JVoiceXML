/*
 * File:    $RCSfile: ScriptingEngine.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
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

import org.jvoicexml.event.error.SemanticError;

/**
 * Provide an interface to access different scripting engines.
 *
 * @author Torben Hardt
 * @author Dirk SChnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface ScriptingEngine {
    /**
     * Sets a existing variable to a new value.
     *
     * @param name unique identifier
     * @param value the new value of the variable
     */
    void setVariable(final String name, final Object value);

    /**
     * Checks, if the given variable is defined.
     * @param name Name of the variable to check.
     *
     * @return <code>true</code> if the variable is defined.
     */
    boolean isVariableDefined(final String name);

    /**
     * Gets the variables current value.
     *
     * @param name unique identifier
     * @return the variables value object
     */
    Object getVariable(final String name);

    /**
     * Removes the given variable from the vault.
     *
     * @param name unique identifier.
     */
    void removeVariable(final String name);

    /**
     * Evaluates the given expression.
     * @param expr The expression to evaluate.
     * @return Evaluated result.
     * @exception SemanticError
     *            Error evaluating the expression.
     */
    Object eval(final String expr)
            throws SemanticError;

    /**
     * Creates a host object in the scripting engine from the given java object.
     * @param <T> Type of the host object.
     * @param name Name of the shadow variable.
     * @param template Base class of the host object.
     *
     * @return Created object.
     *
     * @exception SemanticError
     *            Error converting the given object to a host object.
     * @since 0.3.1
     *
     * @todo Use a proper interface for host objects.
     */
    <T extends Object> T createHostObject(final String name,
                                          final Class<T> template)
            throws SemanticError;
}
