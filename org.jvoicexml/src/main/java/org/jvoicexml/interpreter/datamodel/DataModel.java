/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.activation.MimeType;

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
 * Some first steps to follow the ideas mentioned in the VoiceXML 3.0 draft
 * <a href
 * ="http://www.w3.org/TR/voicexml30/#Resources:Datamodel">http://www.w3.org/
 * TR/voicexml30/#Resources:Datamodel</a>
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public interface DataModel {
    /** No error. */
    int NO_ERROR = 0;
    
    /** A specified scope could not be found. */
    int ERROR_SCOPE_NOT_FOUND = -1;

    /** A specified variable was not found. */
    int ERROR_VARIABLE_NOT_FOUND = -2;

    /** A specified variable was previously defined. */
    int ERROR_VARIABLE_ALREADY_DEFINED = -3;

    /**
     * Creates a new data model.
     * 
     * @return new data model
     */
    DataModel newInstance();

    /**
     * Retrieves the value for undefined. Either this value or {@code null} may
     * be returned to indicate a defined variable with no value.
     * 
     * @return undefined value
     */
    Object getUndefinedValue();

    /**
     * Retrieves a new empty object that may contain,e.g., other variables as
     * members for this data model.
     * 
     * @return newly created object
     */
    Object createNewObject();

    /**
     * Retrieves a human readable representation of the given object retrieved
     * from this data model.
     * 
     * @param object
     *            the value to convert
     * @return string representation of {@code object}
     */
    String toString(Object object);

    /**
     * Creates a new scope object and pushes it on top of the scope stack.
     * 
     * @param scope
     *            the scope to create
     * @return {@code NO_ERROR} upon success, failure status if a scope already exists
     *         with the specified name.
     */
    int createScope(Scope scope);

    /**
     * Creates a new scope object and pushes it on top of the scope stack. The
     * scope is anonymous and may be accessed only when it on the top of the
     * scope stack.
     * 
     * @return {@code NO_ERROR} upon success
     */
    int createScope();

    /**
     * Removes a topmost scope from the scope stack.
     * 
     * @return {@code NO_ERROR} upon success
     */
    int deleteScope();

    /**
     * Removes a scope with the provided name from the scope stack.
     * 
     * @param scope
     *            the scope to remove
     * @return {@code NO_ERROR} upon success, failure status if the stack is 
     *          empty or no scope with the specified name exists
     */
    int deleteScope(Scope scope);

    /**
     * Creates a variable with the default value specified by the underlying
     * datamodel retrieved via {@link #getUndefinedValue()} at the topmost scope
     * on the scope stack.
     * 
     * @param variableName
     *            name of the variable to create
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *         the same name already exists
     */
    int createVariable(String variableName);

    /**
     * Creates a variable as a nested property in the specified variable
     * container with the default value specified by the underlying datamodel
     * retrieved via {@link #getUndefinedValue()} at the topmost scope on the
     * scope stack.
     * 
     * @param variable
     *            the container (previously obtained from the datamodel) where
     *            to create the variable
     * @param variableName
     *            name of the variable to create
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *          the same name already exists
     */
    int createVariableFor(Object variable, String variableName);

    /**
     * Creates a variable with the given initial value on top most scope on the
     * scope stack.
     * 
     * @param variableName
     *            name of the variable to create
     * @param value
     *            initial value of the variable
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *          the same name already exists
     */
    int createVariable(String variableName, Object value);

    /**
     * Creates a variable as a nested property in the given container with the
     * given initial value on top most scope on the scope stack.
     * 
     * @param variable
     *            the container (previously obtained from the datamodel) where
     *            to create the variable
     * @param variableName
     *            name of the variable to create
     * @param value
     *            initial value of the variable
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *         the same name already exists
     */
    int createVariableFor(Object variable, String variableName, Object value);

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
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *          the same name already exists in the specified scope
     */
    int createVariable(String variableName, Object value, Scope scope);

    /**
     * Creates an array with the given dimension on top most scope on the scope
     * stack. All values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param dimension
     *            initial dimension of the array
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *          the same name already exists
     */
    int createArray(String arrayName, int dimension);

    /**
     * Creates an array with the given dimension at the specified scope. All
     * values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param dimension
     *            initial dimension of the array
     * @param scope
     *            scope, where to create the array
     * @return {@code NO_ERROR} upon success, failure status if a variable of
     *          the same name already exists in the specified scope
     */
    int createArray(String arrayName, int dimension, Scope scope);

    /**
     * Resizes an array with the given dimension on top most scope on the scope
     * stack. All values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param dimension
     *            new dimension of the array after resizing
     * @return {@code NO_ERROR} upon success, failure status if the array could
     *           not be found
     */
    int resizeArray(String arrayName, int dimension);

    /**
     * Resizes an array with the given dimension at the specified scope. All
     * values are initialized with {@linkplain #getUndefinedValue()}.
     * 
     * @param arrayName
     *            name of the array to create
     * @param dimension
     *            new dimension of the array after resizing
     * @param scope
     *            scope, where to create the variable
     * @return {@code NO_ERROR} upon success, failure status if the array could
     *           not be found
     */
    int resizeArray(String arrayName, int dimension, Scope scope);

    /**
     * Checks if the given variable exists.
     * 
     * @param variableName
     *            name of the variable to check
     * @return {@code true} if the variable exists
     */
    boolean existsVariable(String variableName);

    /**
     * Checks if the given variable exists at the given scope.
     * 
     * @param variableName
     *            name of the variable to check
     * @param scope
     *            the scope where to check for the variable
     * @return {@code true} if the variable exists
     */
    boolean existsVariable(String variableName, Scope scope);

    /**
     * Deletes the variable with the specified name from the topmost scope on
     * the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @return {@code NO_ERROR} upon success, failure status if no variable with
     *           the specified name exists
     */
    int deleteVariable(String variableName);

    /**
     * Deletes the variable with the specified name from the specified scope..
     * 
     * @param variableName
     *            the variable to delete
     * @param scope
     *            scope, where to delete the variable
     * @return {@code NO_ERROR} upon success, failure status if no variable with
     *           the specified name exists
     */
    int deleteVariable(String variableName, Scope scope);

    /**
     * Assigns a new value to the variable specified on the topmost scope on the
     * stack.
     * 
     * @param variableName
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @return {@code NO_ERROR} upon success, failure status if the specified
     *           variable or scope cannot be found.
     */
    int updateVariable(String variableName, Object newValue);

    /**
     * Assigns a new value to the variable specified on the topmost scope on the
     * stack.
     * 
     * @param variable
     *            the variable that has {@code variableName} as a property
     * @param variableName
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @return {@code NO_ERROR} upon success, failure status if the specified
     *           variable or scope cannot be found.
     */
    int updateVariableFor(Object variable, String variableName,
            Object newValue);

    /**
     * Assigns a new value to the variable specified from the specified scope.
     * 
     * @param variableName
     *            the variable to update
     * @param newValue
     *            new value of the variable
     * @param scope
     *            scope, where to update the variable
     * @return {@code NO_ERROR} upon success, failure status if no variable with
     *           the specified name exists
     */
    int updateVariable(String variableName, Object newValue, Scope scope);

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
     * @return {@code NO_ERROR} upon success, failure status if the specified
     *           variable, field, or scope cannot be found.
     */
    int updateArray(String variableName, int position, Object newValue);

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
     * @return {@code NO_ERROR} upon success, failure status if no variable
     *          with the specified name exists
     */
    int updateArray(String variableName, int position, Object newValue,
            Scope scope);

    /**
     * Returns the value of the variable with the specified name from the
     * topmost scope on the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @param type
     *            type of the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T readVariable(String variableName, Class<T> type)
            throws SemanticError;

    /**
     * Returns the value of the variable with the specified name from the
     * specified scope on the stack.
     * 
     * @param variableName
     *            the variable to delete
     * @param scope
     *            scope, where to update the variable
     * @param type
     *            type of the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T readVariable(String variableName, Scope scope,
            Class<T> type) throws SemanticError;

    /**
     * Returns the value of the array at the given position with the specified
     * name from the topmost scope on the stack.
     * 
     * @param arrayName
     *            the variable to delete
     * @param position
     *            the position to read from
     * @param type
     *            type of the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T readArray(String arrayName, int position,
            Class<T> type) throws SemanticError;

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
     * @param type
     *            type of the variable
     * @return value of the variable
     * @throws SemanticError
     *             if the specified variable or scope can not be found
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T readArray(String arrayName, int position, Scope scope,
            Class<T> type) throws SemanticError;

    /**
     * Evaluates the specified expression in the topmost scope on the stack and
     * returns it value.
     * 
     * @param expr
     *            the expression to evaluate
     * @param type
     *            type of the variable
     * @return evaluated value
     * @throws SemanticError
     *             if the specified scope can not be found or the expression
     *             could not be evaluated
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T evaluateExpression(String expr, Class<T> type)
            throws SemanticError;

    /**
     * Evaluates the specified expression in the specified scope and returns it
     * value.
     * 
     * @param expr
     *            the expression to evaluate
     * @param scope
     *            scope, where to evaluate the expression
     * @param type
     *            type of the variable
     * @return evaluated value
     * @throws SemanticError
     *             if the specified scope can not be found or the expression
     *             could not be evaluated
     * @param <T>
     *            type of the variable to read
     */
    <T extends Object> T evaluateExpression(String expr, Scope scope,
            Class<T> type) throws SemanticError;

    /**
     * Retrieves the Serializer for objects when submitting.
     * 
     * @return serializer to use
     */
    DataModelObjectSerializer getSerializer();

    /**
     * Retrieves a deserializer for the given {@link MimeType}.
     * @param type the MIME type of the object to deserialize
     * @return deserializer to use
     * @since 0.7.9
     */
    DataModelObjectDeserializer getDeserializer(final MimeType type);
    
    /**
     * Copies all values from the current data model to the given datamodel
     * preserving the scope stack.
     * 
     * @param model 
     *            target data model 
     * @return {@code 0} upon success, failure status if the specified variable,
     *         field, or scope cannot be found.
     * @exception SemanticError
     *          error copying values
     * @since 0.7.9
     */
    int copyValues(final DataModel model) throws SemanticError;
}
