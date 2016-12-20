/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel.ecmascript;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * ECMA Script data model for JVoiceXML.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class EcmaScriptDataModel implements DataModel {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(EcmaScriptDataModel.class);

    /** The javascript context. */
    private final Context context;

    private Scriptable rootScope;

    /** The topmost scope. */
    private Scriptable topmostScope;

    /** Map of scopes to the corresponding contexts. */
    private final Map<Scriptable, Scope> scopes;

    /** The data object serializer. */
    private DataModelObjectSerializer serializer;

    static {
        if (!ContextFactory.hasExplicitGlobal()) {
            // Initialize GlobalFactory with custom factory
            final ContextFactory factory = new JVoiceXmlContextFactory();
            ContextFactory.initGlobal(factory);
        }
    }

    /**
     * Constructs a new object.
     */
    public EcmaScriptDataModel() {
        scopes = new java.util.HashMap<Scriptable, Scope>();
        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_DEFAULT);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataModel newInstance() {
        return new EcmaScriptDataModel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUndefinedValue() {
        return Context.getUndefinedValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object createNewObject() {
        return context.newObject(topmostScope);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int createScope(final Scope scope) {
        if (scope == null) {
            return createScope();
        }
        if (topmostScope == null) {
            // create a initial scope if none present
            rootScope = context.initStandardObjects();
            topmostScope = rootScope;
        }

        // Create the implicit variable as the scriptable on the scope stack.
        final Scriptable newScope = createImplicitVariable(scope);
        newScope.setParentScope(topmostScope);
        newScope.setPrototype(topmostScope);
        topmostScope = newScope;

        // Remember the new scope
        scopes.put(topmostScope, scope);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created scope '" + scope.name() + "'");
        }
        return 0;
    }

    /**
     * Creates an implicit variable for the given scope
     * 
     * @param scope
     *            the scope for the implicit variable
     */
    private Scriptable createImplicitVariable(final Scope scope) {
        // Do not create implicit variables for anonymous scopes
        if (scope == Scope.ANONYMOUS) {
            final Context context = Context.getCurrentContext();
            return context.newObject(topmostScope);
        }

        // Create a template for the implicit variable
        final Object template = new ImplicitVariable();
        @SuppressWarnings("unchecked")
        final Class<ScriptableObject> clazz = (Class<ScriptableObject>) template
                .getClass();
        try {
            ScriptableObject.defineClass(rootScope, clazz);
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            LOGGER.error(
                    "unable to create an implicit variable: " + e.getMessage(),
                    e);
            final Context context = Context.getCurrentContext();
            return context.newObject(topmostScope);
        }
        final Scriptable scriptable = context.newObject(rootScope,
                clazz.getSimpleName());

        // Set required attributes in the object created from rhino to actually
        // work
        final ImplicitVariable implicit = (ImplicitVariable) scriptable;
        implicit.setDatamodel(this);
        implicit.setScope(scope);

        // Create the variable in the given scope
        final String name = scope.getName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating implicit variable for '" + name + "'");
        }
        return implicit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createScope() {
        return createScope(Scope.ANONYMOUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteScope() {
        if (topmostScope == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scope scope = scopes.get(topmostScope);
        if (scope == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        return deleteScope(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteScope(final Scope scope) {
        if (scope == null) {
            return deleteScope();
        }
        // Is there such a scope?
        if (!scopes.values().contains(scope)) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        if (topmostScope == null) {
            return 0;
        }

        // See if we are already there.
        final Scope topscope = scopes.remove(topmostScope);
        if (topscope == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        if (topscope == scope) {
            topmostScope = topmostScope.getParentScope();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleted scope '" + scope.name() + "'");
            }
            if (topmostScope == rootScope) {
                topmostScope = null;
                rootScope = null;
            }
            return 0;
        }
        // Dig deeper...
        return deleteScope(scope);
    }

    private Scriptable getAndCreateScope(final Scriptable scope,
            final String name) {
        int dotPos = name.indexOf('.');
        if (dotPos >= 0) {
            final String prefix = name.substring(0, dotPos);
            final String suffix = name.substring(dotPos + 1);
            final Object value;
            if (ScriptableObject.hasProperty(scope, prefix)) {
                value = ScriptableObject.getProperty(scope, prefix);
            } else {
                value = new NativeObject();
                ScriptableObject.putProperty(scope, prefix, value);
            }
            if (value instanceof Scriptable) {
                final Scriptable subscope = (Scriptable) value;
                return getAndCreateScope(subscope, suffix);
            } else {
                return null;
            }
        } else {
            return scope;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createVariable(final String variableName) {
        return createVariable(variableName, null, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createVariable(final String variableName, final Object value) {
        return createVariable(variableName, value, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createVariable(final String variableName, final Object value,
            final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return createVariable(variableName, value, scope, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createVariableFor(final Object variable,
            final String variableName) {
        if (!(variable instanceof Scriptable)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variable
                        + "' is not an instance of Scriptable");
            }
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable scriptable = (Scriptable) variable;
        return createVariable(variableName, null, null, scriptable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createVariableFor(Object variable, final String variableName,
            final Object value) {
        if (!(variable instanceof Scriptable)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variable
                        + "' is not an instance of Scriptable");
            }
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable scriptable = (Scriptable) variable;
        return createVariable(variableName, value, null, scriptable);
    }

    /**
     * Actually create the variable in the specified scope.
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the initial value of the variable
     * @param scope
     *            logical scope where to create the variable
     * @param start
     *            scriptable scope where to create the variable
     * @return {@code 0} if successful
     */
    private int createVariable(final String variableName, final Object value,
            final Scope scope, final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable subscope = getAndCreateScope(start, variableName);
        if (subscope == null) {
            LOGGER.warn("unable to create scope for '" + variableName + "'."
                    + " Please check object creation in var tag or in your"
                    + " grammar file.");
            return ERROR_SCOPE_NOT_FOUND;
        }
        final String name;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            name = variableName.substring(dotPos + 1);
        } else {
            name = variableName;
        }
        if (ScriptableObject.hasProperty(subscope, name)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variableName + "' already defined");
            }
            return ERROR_VARIABLE_ALREADY_DEFINED;
        }
        final Object wrappedValue = Context.javaToJS(value, start);
        ScriptableObject.putProperty(subscope, name, wrappedValue);
        if (LOGGER.isDebugEnabled()) {
            final String json = toString(wrappedValue);
            if (scope == null) {
                LOGGER.debug("created '" + variableName + "' with '" + json
                        + "'");
            } else {
                LOGGER.debug("created '" + variableName + "' in scope '"
                        + scope + "' with '" + json + "'");
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createArray(String arrayName, int dimension) {
        return createArray(arrayName, dimension, null, topmostScope);
    }

    Scriptable getScriptable(final Scope scope) {
        for (final Scriptable scriptable : scopes.keySet()) {
            final Scope currentScope = scopes.get(scriptable);
            if (currentScope.equals(scope)) {
                return scriptable;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createArray(final String arrayName, final int dimension,
            final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return createArray(arrayName, dimension, scope, start);
    }

    private int createArray(final String arrayName, final int dimension,
            final Scope scope, final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable targetScope = getAndCreateScope(start, arrayName);
        final String name;
        int dotPos = arrayName.lastIndexOf('.');
        if (dotPos >= 0) {
            name = arrayName.substring(dotPos + 1);
        } else {
            name = arrayName;
        }
        if (ScriptableObject.hasProperty(targetScope, name)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + arrayName + "' already defined");
            }
            return ERROR_VARIABLE_ALREADY_DEFINED;
        }
        final NativeArray array = new NativeArray(dimension);
        ScriptableObject.putProperty(targetScope, name, array);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created '" + arrayName + "' as an array of "
                    + dimension);
        }

        // Fill the array
        for (int i = 0; i < dimension; i++) {
            final Context context = Context.getCurrentContext();
            final Scriptable scriptable = context.newObject(topmostScope);
            ScriptableObject.putProperty(array, i, scriptable);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int resizeArray(final String arrayName, final int dimension) {
        return resizeArray(arrayName, dimension, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int resizeArray(final String arrayName, final int dimension,
            final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return resizeArray(arrayName, dimension, null, start);
    }

    public int resizeArray(final String arrayName, final int dimension,
            final Scope scope, final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable subcope = getAndCreateScope(start, arrayName);
        final String name;
        int dotPos = arrayName.lastIndexOf('.');
        if (dotPos >= 0) {
            name = arrayName.substring(dotPos + 1);
        } else {
            name = arrayName;
        }
        if (!ScriptableObject.hasProperty(subcope, name)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + arrayName + "' not found");
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        final NativeArray oldArray;
        final Object oldValue = ScriptableObject.getProperty(subcope, name);
        if (oldValue instanceof NativeArray) {
            oldArray = (NativeArray) oldValue;
        } else {
            oldArray = new NativeArray(1);
            ScriptableObject.putProperty(oldArray, 0, oldValue);
        }
        final NativeArray array = new NativeArray(dimension);
        for (int i = 0; i < array.size(); i++) {
            if (i < oldArray.size()) {
                final Object value = oldArray.get(i);
                ScriptableObject.putProperty(array, i, value);
            } else {
                final Context context = Context.getCurrentContext();
                final Scriptable scriptable = context.newObject(topmostScope);
                ScriptableObject.putProperty(array, i, scriptable);
            }
        }
        ScriptableObject.putProperty(subcope, name, array);
        if (LOGGER.isDebugEnabled()) {
            if (scope == null) {
                LOGGER.debug("resized '" + arrayName + "' to an array of "
                        + dimension);
            } else {
                LOGGER.debug("resized '" + arrayName + "' in scope '" + scope
                        + "' to an array of " + dimension);
            }
        }
        return 0;
    }

    private Scriptable getScope(final Scriptable scope, final String name) {
        int dotPos = name.indexOf('.');
        if (dotPos >= 0) {
            final String prefix = name.substring(0, dotPos);
            final String suffix = name.substring(dotPos + 1);
            final Object value;
            if (ScriptableObject.hasProperty(scope, prefix)) {
                value = ScriptableObject.getProperty(scope, prefix);
            } else {
                return null;
            }
            if (value instanceof Scriptable) {
                final Scriptable subscope = (Scriptable) value;
                return getScope(subscope, suffix);
            } else {
                return null;
            }
        } else {
            return scope;
        }
    }

    private Scriptable getFullScope(final Scriptable scope, final String name) {
        int dotPos = name.indexOf('.');
        if (dotPos >= 0) {
            final String prefix = name.substring(0, dotPos);
            final String suffix = name.substring(dotPos + 1);
            final Object value;
            if (ScriptableObject.hasProperty(scope, prefix)) {
                value = ScriptableObject.getProperty(scope, prefix);
            } else {
                return null;
            }
            if (value instanceof Scriptable) {
                final Scriptable subscope = (Scriptable) value;
                return getScope(subscope, suffix);
            } else {
                return null;
            }
        } else {
            final Object value = ScriptableObject.getProperty(scope, name);
            if (value instanceof Scriptable) {
                return (Scriptable) value;
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsVariable(final String variableName) {
        return existsVariable(variableName, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsVariable(final String variableName, final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return existsVariable(variableName, scope, start);
    }

    public boolean existsVariable(final String variableName, final Scope scope,
            final Scriptable start) {
        if (start == null) {
            return false;
        }
        final Scriptable targetScope = getScope(start, variableName);
        if (targetScope == null) {
            return false;
        }
        final String property;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            property = variableName.substring(dotPos + 1);
        } else {
            property = variableName;
        }
        return ScriptableObject.hasProperty(targetScope, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteVariable(final String variableName) {
        return deleteVariable(variableName, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteVariable(final String variableName, final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return deleteVariable(variableName, scope, start);
    }

    private int deleteVariable(final String variableName, final Scope scope,
            final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable targetScope = getScope(start, variableName);
        if (targetScope == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("socpe for '" + variableName + "' not found");
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        final String property;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            property = variableName.substring(dotPos + 1);
        } else {
            property = variableName;
        }
        if (!ScriptableObject.hasProperty(targetScope, property)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variableName + "' not found");
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        ScriptableObject.deleteProperty(targetScope, property);
        if (LOGGER.isDebugEnabled()) {
            if (scope == null) {
                LOGGER.debug("deleted '" + variableName + "'");
            } else {
                LOGGER.debug("deleted '" + variableName + "' in scope '"
                        + scope + "'");
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateVariable(final String variableName, final Object newValue) {
        return updateVariable(variableName, newValue, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateVariable(final String variableName, final Object newValue,
            final Scope scope) {
        final Scriptable start = getScriptable(scope);
        return updateVariable(variableName, newValue, scope, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateVariableFor(final Object variable,
            final String variableName, final Object newValue) {
        if (!(variable instanceof Scriptable)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variable
                        + "' is not an instance of Scriptable");
            }
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable scriptable = (Scriptable) variable;
        return updateVariable(variableName, newValue, null, scriptable);
    }

    private int updateVariable(final String variableName,
            final Object newValue, final Scope scope, final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable subscope = getAndCreateScope(start, variableName);
        final String property;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            property = variableName.substring(dotPos + 1);
        } else {
            property = variableName;
        }
        if (!ScriptableObject.hasProperty(subscope, property)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variableName + "' not found");
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        final Object jsValue = Context.javaToJS(newValue, subscope);
        ScriptableObject.putProperty(subscope, property, jsValue);
        if (LOGGER.isDebugEnabled()) {
            final String json = toString(jsValue);
            if (scope == null) {
                LOGGER.debug("set '" + variableName + "' to '" + json + "'");
            } else {
                LOGGER.debug("set '" + variableName + "' in scope '" + scope
                        + "' to '" + json + "'");
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateArray(final String variableName, final int position,
            final Object newValue) {
        return updateArray(variableName, position, newValue, null, topmostScope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateArray(final String variableName, final int position,
            final Object newValue, final Scope scope) {
        final Scriptable start = getScriptable(scope);
        ;
        return updateArray(variableName, position, newValue, scope, start);
    }

    private int updateArray(final String variableName, final int position,
            final Object newValue, final Scope scope, final Scriptable start) {
        if (start == null) {
            return ERROR_SCOPE_NOT_FOUND;
        }
        final Scriptable subscope = getAndCreateScope(start, variableName);
        final String property;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            property = variableName.substring(dotPos + 1);
        } else {
            property = variableName;
        }
        if (!ScriptableObject.hasProperty(subscope, property)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variableName + "' not found");
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        final Object jsValue = Context.javaToJS(newValue, subscope);
        final NativeArray array = (NativeArray) ScriptableObject.getProperty(
                subscope, property);
        if (!ScriptableObject.hasProperty(array, position)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("'" + variableName + "' has no position "
                        + position);
            }
            return ERROR_VARIABLE_NOT_FOUND;
        }
        ScriptableObject.putProperty(array, position, jsValue);
        if (LOGGER.isDebugEnabled()) {
            final String json = toString(jsValue);
            if (scope == null) {
                LOGGER.debug("set '" + variableName + "[" + position
                        + "]' to '" + json + "'");
            } else {
                LOGGER.debug("set '" + variableName + "[" + position
                        + "]' to '" + json + "' in scope '" + scope + "'");
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T readVariable(final String variableName,
            final Class<T> type) throws SemanticError {
        return readVariable(variableName, null, topmostScope, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T readVariable(String variableName, Scope scope,
            final Class<T> type) throws SemanticError {
        final Scriptable start = getScriptable(scope);
        return readVariable(variableName, scope, start, type);
    }

    private <T extends Object> T readVariable(String variableName, Scope scope,
            final Scriptable start, final Class<T> type) throws SemanticError {
        if (start == null) {
            throw new SemanticError("no scope '" + scope
                    + "' present to read '" + variableName + "'");
        }
        final Scriptable subcope = getScope(topmostScope, variableName);
        if (subcope == null) {
            throw new SemanticError("'" + variableName + "' not found");
        }
        final String property;
        int dotPos = variableName.lastIndexOf('.');
        if (dotPos >= 0) {
            property = variableName.substring(dotPos + 1);
        } else {
            property = variableName;
        }
        if (!ScriptableObject.hasProperty(subcope, property)) {
            throw new SemanticError("'" + variableName + "' not found");
        }
        final Object value = ScriptableObject.getProperty(subcope, property);
        if (value == getUndefinedValue()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final T t = (T) Context.jsToJava(value, type);
        return t;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T readArray(final String arrayName, final int position,
            final Class<T> type) throws SemanticError {
        return readArray(arrayName, position, null, topmostScope, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T readArray(final String arrayName, final int position,
            final Scope scope, final Class<T> type) throws SemanticError {
        final Scriptable start = getScriptable(scope);
        return readArray(arrayName, position, scope, start, type);
    }

    private <T> T readArray(final String arrayName, final int position,
            final Scope scope, final Scriptable start, final Class<T> type)
            throws SemanticError {
        if (start == null) {
            throw new SemanticError("no scope '" + scope
                    + "' present to read '" + arrayName + "'");
        }
        final Scriptable targetScope = getFullScope(topmostScope, arrayName);
        if (targetScope == null) {
            throw new SemanticError("'" + arrayName + "' not found");
        }
        if (!ScriptableObject.hasProperty(targetScope, position)) {
            throw new SemanticError("'" + arrayName + "' has no position "
                    + position);
        }
        final Object value = ScriptableObject
                .getProperty(targetScope, position);
        if (value == getUndefinedValue()) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final T t = (T) Context.jsToJava(value, type);
        return t;
    }

    private String prepareExpression(final String expr) {
        if (expr == null) {
            return null;
        }
        final String trimmedExpr = expr.trim();
        if (trimmedExpr.isEmpty()) {
            return null;
        }
        if (trimmedExpr.endsWith(";") || trimmedExpr.endsWith("}")) {
            return trimmedExpr;
        }
        return trimmedExpr + ";";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T evaluateExpression(final String expr,
            final Class<T> type) throws SemanticError {
        return evaluateExpression(expr, null, topmostScope, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T evaluateExpression(final String expr,
            final Scope scope, final Class<T> type) throws SemanticError {
        final Scriptable start = getScriptable(scope);
        return evaluateExpression(expr, scope, start, type);
    }

    private <T extends Object> T evaluateExpression(final String expr,
            final Scope scope, final Scriptable start, final Class<T> type)
            throws SemanticError {
        if (start == null) {
            throw new SemanticError("no scope '" + scope
                    + "' present to evaluate '" + expr + "'");
        }
        final String preparedExpression = prepareExpression(expr);
        if (preparedExpression == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring empty value expr");
            }
            return null;
        }
        try {
            final Object value = context.evaluateString(start,
                    preparedExpression, "expr", 1, null);
            if (value == getUndefinedValue()) {
                return null;
            }
            if (LOGGER.isDebugEnabled()) {
                final String json = toString(value);
                LOGGER.debug("evaluated '" + preparedExpression + "' to '"
                        + json + "'");
            }
            @SuppressWarnings("unchecked")
            final T t = (T) Context.jsToJava(value, type);
            return t;
        } catch (EcmaError | EvaluatorException e) {
            final String message = "error evaluating '" + preparedExpression
                    + "'";
            LOGGER.warn(message, e);
            final String concatenatedMessage = getConcatenadedErrorMessage(
                    message, e);
            throw new SemanticError(concatenatedMessage, e);
        }
    }

    /**
     * Creates a new error message from the message and the detailed message
     * with max 256 chars.
     * 
     * @param message
     *            the error message
     * @param e
     *            the exception that caused the error
     * @return concatenated detailed message
     */
    private String getConcatenadedErrorMessage(final String message,
            final RhinoException e) {
        final StringBuilder str = new StringBuilder();
        str.append(message);
        str.append(" (");
        str.append(e.getMessage());
        str.append(" at line ");
        str.append(e.lineNumber());
        if (e.lineSource() != null) {
            str.append(": ");
            str.append(e.lineSource());
        }
        str.append(")");
        if (str.length() > 256) {
            str.setLength(256);
            str.setCharAt(255, '.');
            str.setCharAt(254, '.');
            str.setCharAt(253, '.');
        }
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NativeJavaObject) {
            final Object jobject = Context.jsToJava(object, Object.class);
            return jobject.toString();
        } else if (object instanceof Scriptable) {
            final Scriptable scriptable = (Scriptable) object;
            return EcmaScriptDataModel.toJSON(scriptable);
        }
        return object.toString();
    }

    /**
     * Transforms the given {@link ScriptableObject} into a JSON string.
     * 
     * @param object
     *            the object to serialize
     * @return JSON formatted string
     * @since 0.7.5
     */
    public static String toJSON(final Scriptable object) {
        final JSONObject json = toJSONObject(object);
        if (json == null) {
            return null;
        }
        return json.toJSONString();
    }

    /**
     * Transforms the given {@link ScriptableObject} into a JSON object.
     * 
     * @param object
     *            the object to serialize
     * @return JSON object
     * @since 0.7.5
     */
    @SuppressWarnings("unchecked")
    private static JSONObject toJSONObject(final Scriptable object) {
        if (object == null) {
            return null;
        }
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        JSONObject json = new JSONObject();
        for (Object id : ids) {
            final String key = id.toString();
            Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final ScriptableObject scriptable = (ScriptableObject) value;
                final JSONObject subvalue = toJSONObject(scriptable);
                json.put(key, subvalue);
            } else {
                json.put(key, value);
            }
        }
        return json;
    }

    /**
     * Sets the serializer.
     * 
     * @param value
     *            the serializer
     */
    public void setSerializer(final DataModelObjectSerializer value) {
        serializer = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataModelObjectSerializer getSerializer() {
        return serializer;
    }
}
