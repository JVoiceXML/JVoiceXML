/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.datamodel;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.scope.Scope;

/**
 * The datamodel is a repository for both user- and system-defined data and
 * properties. To simplify variable lookup,we define the datamodel with a
 * synchronous function-call API, rather than an asynchronous one based on
 * events. The data model API does not assume any particular underlying
 * representation of the data or any specific access language, thus allowing
 * implementations to plug in different concrete data model languages.
 * 
 * <p>
 * There is a single global data model that is created when the system is first
 * initialized. Access to data is controlled by means of scopes, which are
 * stored in a stack. Data is always accessed within a particular scope, which
 * may be specified by name but defaults to being the top scope in the stack. At
 * initialization time, a single scope named "Global" is created. Thereafter
 * scopes are explicitly created and destroyed by the data model's clients.
 * </p>
 *
 * <p>
 * Some first steps to follow the ideas mentioned in the VoiceXML 3.0 draft <a
 * href
 * ="http://www.w3.org/TR/voicexml30/#Resources:Datamodel">http://www.w3.org/
 * TR/voicexml30/#Resources:Datamodel</a>
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public interface DataModel {
    /** A specified scope could not be found. */
    int ERROR_SCOPE_NOT_FOUND = -1;

    /** A specified variable was not found. */
    int ERROR_VARIABLE_NOT_FOUND = -2;

    /** A specified variable was previously defined. */
    int ERROR_VARIABLE_ALREADY_DEFINED = -3;

    /**
     * Retrieves the value for undefined. Either this value or {@code null} may
     * be returned to indicate a defined variable with no value.
     * 
     * @return undefined value
     */
    Object getUndefinedValue();

    /**
     * Retrieves a human readable representation of the given object retrieved
     * from this data model.
     * 
     * @param object
     *            the value to convert
     * @return string representation of {@code object}
     * @since 0.7.7
     */
    String toString(final Object object);

    /**
     * Creates a new scope object and pushes it on top of the scope stack.
     * 
     * @param scooe
     *            the scope to create
     * @return {@code 0} upon success, failure status if a scope already exists
     *         with the specified name.
     */
    int createScope(final Scope scope);

    /**
     * Creates a new scope object and pushes it on top of the scope stack. The
     * scope is anonymous and may be accessed only when it on the top of the
     * scope stack.
     * 
     * @return {@code 0} upon success
     */
    int createScope();

    /**
     * Removes a topmost scope from the scope stack.
     * 
     * @return {@code 0} upon success
     */
    int deleteScope();

    /**
     * Removes a scope with the provided name from the scope stack.
     * 
     * @param scope
     *            the scope to remove
     * @return {@code 0} upon success, failure status if the stack is empty or
     *         no scope with the specified name exists
     */
    int deleteScope(final Scope scope);

    /**
     * Creates a variable with the default value specified by the underlying
     * datamodel retrieved via {@link #getUndefinedValue()} at the topmost scope
     * on the scope stack.
     * 
     * @param variableName
     *            name of the variable to create
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists
     */
    int createVariable(final String variableName);

    /**
     * Creates a variable with the default value specified by the underlying
     * datamodel retrieved via {@link #getUndefinedValue()} at the topmost scope
     * on the scope stack.
     * 
     * @param variableName
     *            name of the variable to create
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists
     */
    int createVariableFor(final Object variable, final String variableName);

    /**
     * Creates a variable with the given initial value on top most scope on the
     * scope stack.
     * 
     * @param variableName
     *            name of the variable to create
     * @param value
     *            initial value of the variable
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists
     */
    int createVariable(final String variableName, final Object value);

    /**
     * Creates a variable with the given initial value on top most scope on the
     * scope stack.
     * 
     * @param variable
     *            name of the variable to create
     * @param value
     *            initial value of the variable
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists
     */
    int createVariableFor(final Object variable, final String variableName,
            final Object value);

    /**
     * Creates a variable. If no value is provided, the variable is created with
     * the default value specified by the underlying datamodel retrieved via
     * {@link #getUndefinedValue()}.
     * 
     * @param variableName
     *            name of the variable to create
     * @param value
     *            initial value of the variable, maybe {@code null}
     * @param scope
     *            scope, where to create the variable
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists in the specified scope
     */
    int createVariable(final String variableName, final Object value,
            final Scope scope);

    /**
     * Creates an array with the given dimension on top most scope on the scope
     * stack. All values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param value
     *            initial value of the variable
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists
     */
    int createArray(final String arrayName, final int dimension);

    /**
     * Creates an array with the given dimension at the specified scope. All
     * values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param value
     *            initial value of the variable, maybe {@code null}
     * @param scope
     *            scope, where to create the variable
     * @return {@code 0} upon success, failure status if a variable of the same
     *         name already exists in the specified scope
     */
    int createArray(final String arrayName, final int dimension,
            final Scope scope);

    /**
     * Resizes an array with the given dimension on top most scope on the scope
     * stack. All values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param value
     *            initial value of the variable
     * @return {@code 0} upon success, failure status if the arry could not be
     *         found
     */
    int resizeArray(final String arrayName, final int dimension);

    /**
     * Resizes an array with the given dimension at the specified scope. All
     * values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param value
     *            initial value of the variable, maybe {@code null}
     * @param scope
     *            scope, where to create the variable
     * @return {@code 0} upon success, failure status if the array could not be
     *         found
     */
    int resizeArray(final String arrayName, final int dimension,
            final Scope scope);

    /**
     * Checks if the given variable exists.
     * 
     * @param variableName
     *            name of the variable to check
     * @return {@code true} if the variable exists
     */
    boolean existsVariable(final String variableName);

    /**
     * Checks if the given variable exists at the given scope.
     * 
     * @param variableName
     *            name of the variabl to check
     * @param scope
     *            the scope where to check for the variable
     * @return {@code true} if the variable exists
     */
    boolean existsVariable(final String variableName, final Scope scope);

    /**
     * Deletes the variable with the specified name from the topmost scope on
     * the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @return {@code 0} upon success, failure status if no variable with the
     *         specified name exists
     */
    int deleteVariable(final String variableName);

    /**
     * Deletes the variable with the specified name from the specified scope..
     * 
     * @param variableName
     *            the variable to delete
     * @param scope
     *            scope, where to delete the variable
     * @return {@code 0} upon success, failure status if no variable with the
     *         specified name exists
     */
    int deleteVariable(final String variableName, final Scope scope);

    /**
     * Assigns a new value to the variable specified on the topmost scope on the
     * stack.
     * 
     * @param variableName
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @return {@code 0} upon success, failure status if the specified variable
     *         or scope cannot be found.
     */
    int updateVariable(final String variableName, final Object newValue);

    /**
     * Assigns a new value to the variable specified on the topmost scope on the
     * stack.
     * 
     * @param variable
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @return {@code 0} upon success, failure status if the specified variable
     *         or scope cannot be found.
     */
    int updateVariableFor(final Object variable, final String variableName,
            final Object newValue);

    /**
     * Assigns a new value to the variable specified from the specified scope.
     * 
     * @param variableName
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @param scope
     *            scope, where to update the variable
     * @return {@code 0} upon success, failure status if no variable with the
     *         specified name exists
     */
    int updateVariable(final String variableName, final Object newValue,
            final Scope scope);

    /**
     * Assigns a new value to the array at the given position on the topmost
     * scope on the stack.
     * 
     * @param variableName
     *            the variable to update
     * @param position
     *            the position in the array to update
     * @param newValue
     *            new value of the variable
     * @return {@code 0} upon success, failure status if the specified variable,
     *         field, or scope cannot be found.
     */
    int updateArray(final String variableName, final int position,
            final Object newValue);

    /**
     * Assigns a new value to the array at the given position in the specified
     * scope.
     * 
     * @param variableName
     *            the variable to update
     * @param position
     *            the position in the array to update
     * @param newValue
     *            new value of the variable
     * @param scope
     *            scope, where to update the variable
     * @return {@code 0} upon success, failure status if no variable with the
     *         specified name exists
     */
    int updateArray(final String variableName, final int position,
            final Object newValue, final Scope scope);

    /**
     * Returns the value of the variable with the specified name from the
     * topmost scope on the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     */
    <T extends Object> T readVariable(final String variableName,
            final Class<T> type) throws SemanticError;

    /**
     * Returns the value of the variable with the specified name from the
     * specified scope on the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @param scope
     *            scope, where to update the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     */
    <T extends Object> T readVariable(final String variableName,
            final Scope scope, final Class<T> type) throws SemanticError;

    /**
     * Returns the value of the array at the given position with the specified
     * name from the topmost scope on the stack.
     * 
     * @param arrayName
     *            the variable to delete
     * @param position
     *            the position to read from
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     */
    <T extends Object> T readArray(final String arrayName, final int position,
            final Class<T> type) throws SemanticError;

    /**
     * Returns the value of the array at the given position with the specified
     * name from the specified scope on the stack.
     * 
     * @param arrayName
     *            the variable to delete
     * @param position
     *            the position to read from
     * @param scope
     *            scope, where to update the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     */
    <T extends Object> T readArray(final String arrayName, final int position,
            final Scope scope, final Class<T> type) throws SemanticError;

    /**
     * Evaluates the specified expression in the topmost scope on the stack and
     * returns it value
     * 
     * @param expr
     *            the expression to evaluate
     * @return evaluated value
     * @throws SemanticError
     *             if the specified scope can not be found
     */
    <T extends Object> T evaluateExpression(final String expr,
            final Class<T> type) throws SemanticError;

    /**
     * Evaluates the specified expression in the specified scope and returns it
     * value
     * 
     * @param expr
     *            the expression to evaluate
     * @param scope
     *            scope, where to evaluate the expression
     * @return evaluated value
     * @throws SemanticError
     *             if the specified scope can not be found
     */
    <T extends Object> T evaluateExpression(final String expr,
            final Scope scope, final Class<T> type) throws SemanticError;

}
