/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.resources;

/**
 * The datamodel is a repository for both user- and system-defined data and
 * properties.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 23-Dez-2008 20:08:21
 */
public interface DataModelResource {
    /**
     * Creates a new scope object and pushes it on top of the scope stack. If no
     * name is provided the scope is anonymous and may be accessed only when it
     * on the top of the scope stack. A Failure status is returned if a scope
     * already exists with the specified name.
     *
     * @param name
     *            maybe <code>null</code>
     * @return <code>true</code> if successful
     */
    boolean createScope(final String name);

    /**
     * Removes a scope from the scope stack. If no name is provided, the topmost
     * scope is removed. Otherwise the scope with provided name is removed. A
     * Failure status is returned if the stack is empty or no scope with the
     * specified name exists.
     *
     * @param name
     *            maybe <code>null</code>
     * @return <code>true</code> if successful
     */
    boolean deleteScope(final String name);

    /**
     * Creates a variable. If scopeName is not specified, the variable is
     * created in the top most scope on the scope stack. If no value is
     * provided, the variable is created with the default value specified by the
     * underlying datamodel. A Failure status is returned if a variable of the
     * same name already exists in the specified scope.
     *
     * @param variableName name of the variable
     * @param value
     *            maybe <code>null</code>
     * @param scopeName
     *            maybe <code>null</code>
     * @return <code>true</code> if successful
     */
    boolean createVariable(final String variableName, final String value,
            final String scopeName);

    /**
     * Deletes the variable with the specified name from the specified scope. If
     * no scopeName is provided, the variable is deleted from the topmost scope
     * on the stack. The status Failure is returned if no variable with the
     * specified name exists in the scope.
     *
     * @param variableName name of the variable
     * @param scopeName
     *            maybe <code>null</code>
     * @return <code>true</code> if successful
     */
    boolean deleteVariable(final String variableName,  final String scopeName);

    /**
     * Assigns a new value to the variable specified. If scopeName is not
     * specified, the variable is accessed in the topmost scope on the stack. A
     * Failure status is returned if the specified variable or scope cannot be
     * found.
     *
     * @param variableName name of the variable
     * @param newValue
     * @param scopeName
     *            maybe <code>null</code>
     * @return <code>true</code> if successful
     */
    boolean updateVariable(final String variableName, final String newValue,
            final String scopeName);

    /**
     * Returns the value of the variable specified. If scopeName is not
     * specified, the variable is accessed in the topmost scope on the stack. An
     * error is raised if the specified variable or scope cannot be found.
     *
     * @param variableName name of the variable
     * @param scopeName
     *            maybe <code>null</code>
     * @return value
     */
    Object readVariable(final String variableName, final String scopeName);

    /**
     * Evaluates the specified expression and returns its value. If scopeName is
     * not specified, the expression is evaluated in the topmost scope on the
     * stack. An error is raised if the specified scope cannot be found.
     *
     * @param expr
     * @param scopeName
     *            maybe <code>null</code>
     * @return value
     */
    Object evaluateExpression(final String expr, final String scopeName);
}