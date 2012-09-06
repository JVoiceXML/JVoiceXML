/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/ScriptingEngine.java $
 * Version: $LastChangedRevision: 2947 $
 * Date:    $Date: 2012-02-08 03:18:26 -0600 (mié, 08 feb 2012) $
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

package org.jvoicexml.interpreter;

import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopeSubscriber;
import org.jvoicexml.interpreter.variables.StandardSessionVariable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Scripting engine.
 *
 * <p>
 * This is a simple wrapper around the Rhino scripting engine to simplify
 * the handling and to maintain scope aspects.
 * </p>
 *
 * <p>
 * The Rhino implementation has the disadvantage that setting and accessing
 * variables do not traverse the scope stack. This implementation offers
 * a workaround for that. This also means that it has to be changed, once
 * Rhino traverses the scope stack.
 * </p>
 *
 * @author Torben Hardt
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2947 $
 */
public final class ScriptingEngine
    implements ScopeSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ScriptingEngine.class);

    /** The parent block's scope. */
    private final Scriptable scriptGlobalScope;

    /**
     * Container for the nested stacks. Part of var handling.
     */
    private final Stack<Scriptable> scopeStack = new Stack<Scriptable>();

    /** Map of scopes to the corresponding contexts. */
    private final Map<Scope, Scriptable> scopes;

    static {
        // Initialize GlobalFactory with custom factory
        ContextFactory.initGlobal(new JVoiceXmlContextFactory());
    }


    /**
     * Constructs a new object.
     * @param observer the scope observer.
     */
    public ScriptingEngine(final ScopeObserver observer) {
        scopes = new java.util.HashMap<Scope, Scriptable>();
        final Context context = Context.enter();

        context.setLanguageVersion(Context.VERSION_DEFAULT);
        // create a initial scope, do NOT allow access to all java objects
        // check later if sealed initial scope should be used.
        scriptGlobalScope = context.initStandardObjects();
        final Scriptable firstScope = context.newObject(scriptGlobalScope);
        firstScope.setParentScope(scriptGlobalScope);
        firstScope.setPrototype(scriptGlobalScope);
        scopeStack.push(firstScope);

        if (observer != null) {
            observer.addScopeSubscriber(this);
        }
    }

    /**
     * Transforms the given {@link ScriptableObject} into a JSON string.
     * @param object the object to serialize 
     * @return JSON formatted string
     * @since 0.7.5
     */
    public static String toJSON(final ScriptableObject object) {
        if (object == null) {
            return null;
        }
        final JSONObject json = toJSONObject(object);
        return json.toJSONString();
    }

    /**
     * Transforms the given {@link ScriptableObject} into a JSON object.
     * @param object the object to serialize 
     * @return JSON object
     * @since 0.7.5
     */
    @SuppressWarnings("unchecked")
    private static JSONObject toJSONObject(final ScriptableObject object) {
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
     * Evaluates the given expression.
     * @param expr The expression to evaluate.
     * @return Evaluated result.
     * @exception SemanticError
     *            Error evaluating the expression.
     */
    public Object eval(final String expr)
            throws SemanticError {
        // get the expr-attribute
        if (expr == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring empty value expr");
            }
            return Context.getUndefinedValue();
        }

        final String trimmedExpr = expr.trim();
        if (trimmedExpr.length() == 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring empty value expr");
            }
            return Context.getUndefinedValue();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("evaluating '" + trimmedExpr + "'...");
        }

        final Object result;
        final Context ctx = Context.getCurrentContext();
        final Scriptable scope = getScope();
        // evaluate expr with parent nodes scope
        try {
            result = ctx.evaluateString(scope, trimmedExpr, "expr", 1, null);
        } catch (EcmaError e) {
            throw new SemanticError(e.getMessage(), e);
        } catch (EvaluatorException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage());
            sb.append(" at line ");
            sb.append(e.lineNumber());
            sb.append(": ");
            sb.append(e.lineSource());
            throw new SemanticError(sb.toString(), e);
        }

        if (LOGGER.isDebugEnabled()) {
            if (result instanceof String) {
                LOGGER.debug("value of '" + expr + "': '" + result + "'");
            } else {
                LOGGER.debug("value of '" + expr + "': " + result);
            }
        }

        return result;
    }

    /**
     * Sets an existing variable to a new value.
     *
     * @param name unique identifier
     * @param value the new value of the variable
     */
    public void setVariable(final String name, final Object value) {
        if (name == null) {
            LOGGER.warn("ignoring empty name to set");

            return;
        }

        final Scriptable scope = getScope();
        // If the variable is defined set it in the relevant scope,
        // otherwise put it here.
        final boolean set = setVariable(name, value, scope);
        if (!set) {
            scope.put(name, scope, value);
        }

        if (LOGGER.isDebugEnabled()) {
            if (value instanceof String) {
                LOGGER.debug("set '" + name + "' to '" + value + "'");
            } else {
                LOGGER.debug("set '" + name + "' to " + value);
            }
        }
    }

    /**
     * Sets an existing variable to a new value ath teh given scope.
     *
     * @param scope the scope where to put the variable
     * @param name unique identifier
     * @param value the new value of the variable
     * @since 0.7.5
     */
    public void setVariable(final Scope scope, final String name,
            final Object value) {
        if (name == null) {
            LOGGER.warn("ignoring empty name to set");

            return;
        }

        final Scriptable context = scopes.get(scope);
        if (context == null) {
            return;
        }
        // If the variable is defined set it in the relevant scope,
        // otherwise put it here.
        final boolean set = setVariable(name, value, context);
        if (!set) {
            context.put(name, context, value);
        }

        if (LOGGER.isDebugEnabled()) {
            if (value instanceof String) {
                LOGGER.debug("set '" + name + "' at + " + scope + " to '"
                        + value + "'");
            } else {
                LOGGER.debug("set '" + name + "' at " + scope + " to " + value);
            }
        }
    }

    /**
     * Traverse the scope stack and set the variable.
     * @param name name of the variable to set.
     * @param value value of the variable.
     * @param scope start scope.
     * @return <code>true</code> if the variable was set.
     * @since 0.6
     */
    private boolean setVariable(final String name, final Object value,
            final Scriptable scope) {
        if (scope.has(name, scope)) {
            scope.put(name, scope, value);
            return true;
        }
        final Scriptable parent = scope.getParentScope();
        if (parent == null) {
            return false;
        }
        return setVariable(name, value, parent);
    }

    /**
     * Gets the variables current value.
     *
     * @param name unique identifier
     * @return the variables value object, <code>null</code> if the
     *         variable is not defined.
     */
    public Object getVariable(final String name) {
        final Scriptable scope = getScope();

        if (isVariableDefined(name)) {
            return getVariable(name, scope);
        }

        return null;
    }

    /**
     * Retrieves the current value of a variable starting with the
     * given scope.
     * @param scope the scope
     * @param name name of the variable
     * @return the variables value object, <code>null</code> if the
     *         variable is not defined.
     * @since 0.7.5
     */
    public Object getVariable(final Scope scope, final String name) {
        final Scriptable context = scopes.get(scope);
        if (context == null) {
            return null;
        }

        if (isVariableDefined(name)) {
            return getVariable(name, context);
        }

        return null;
    }

    /**
     * Traverse the scope stack and retrieve the value of the variable.
     * @param name name of the variable.
     * @param scope start scope.
     * @return value of the variable.
     * @since 0.6
     */
    private Object getVariable(final String name, final Scriptable scope) {
        if (scope.has(name, scope)) {
            return scope.get(name, scope);
        }
        final Scriptable parent = scope.getParentScope();
        if (parent == null) {
            return null;
        }
        return getVariable(name, parent);
    }

    /**
     * Gets the variables current value as an array.
     *
     * @param name unique identifier
     * @return the variables value object.
     * @exception SemanticError
     *            Error retrieving the value for <code>name</code>.
     * @since 0.6
     */
    public Object[] getVariableAsArray(final String name) throws SemanticError {
        final NativeArray nativeArray = (NativeArray) eval(name + ";");
        final Object[] ids = NativeArray.getPropertyIds(nativeArray);
        final Object[] retObjects = new Object[ids.length];
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] instanceof Integer) {
                int id = ((Integer) ids[i]).intValue();
                retObjects[i] = NativeArray.getProperty(nativeArray, id);
            } else {
                String id = ids[i].toString();
                retObjects[i] = NativeArray.getProperty(nativeArray, id);
            }
        }

        return retObjects;
    }

    /**
     * Removes the given variable from the vault.
     *
     * @param name unique identifier.
     */
    public void removeVariable(final String name) {
        if (name == null) {
            LOGGER.warn("ignoring empty name to remove");

            return;
        }

        final Scriptable scope = getScope();
        removeVariable(name, scope);
    }

    /**
     * Traverses the scope stack and removes the variable.
     * @param name name of the variable to remove.
     * @param scope start scope.
     * @since 0.6
     */
    private void removeVariable(final String name, final Scriptable scope) {
        if (scope.has(name, scope)) {
            scope.delete(name);
            return;
        }
        final Scriptable parent = scope.getParentScope();
        if (parent == null) {
            return;
        }
        removeVariable(name, parent);
    }

    /**
     * Checks, if the given variable is defined.
     * @param name Name of the variable to check.
     *
     * @return <code>true</code> if the variable is defined.
     */
    public boolean isVariableDefined(final String name) {
        if (name == null) {
            return false;
        }

        final Scriptable scope = getScope();
        return isVariableDefined(name, scope);
    }

    /**
     * Traverses the scope stack and checks if the variable is defined.
     * @param name name of the variable to check.
     * @param scope start scope.
     * @return <code>true</code> if the variable is defined.
     * @since 0.6
     */
    private boolean isVariableDefined(final String name,
            final Scriptable scope) {
        if (scope.has(name, scope)) {
            return true;
        }
        final Scriptable parent = scope.getParentScope();
        if (parent == null) {
            return false;
        }
        return isVariableDefined(name, parent);
    }

    /**
     * {@inheritDoc}
     */
    public void enterScope(final Scope previous, final Scope next) {
        final Context context = Context.getCurrentContext();
        final Scriptable parentScope = getScope();
        final Scriptable newScope = context.newObject(parentScope);

        newScope.setParentScope(parentScope);
        newScope.setPrototype(scriptGlobalScope);
        scopeStack.push(newScope);
        scopes.put(next, newScope);
    }

    /**
     * {@inheritDoc}
     */
    public void exitScope(final Scope previous, final Scope next) {
        if (scopeStack.isEmpty()) {
            return;
        }

        scopeStack.pop();
        scopes.remove(previous);
    }

    /**
     * Retrieves the current scriptable scope.
     * @return Current scriptable scope.
     */
    private Scriptable getScope() {
        return scopeStack.peek();
    }

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
     */
    @SuppressWarnings("unchecked")
    public <T extends Object> T createHostObject(final String name,
                                                 final Class<T> template)
            throws SemanticError {
        if (template == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cannot create null host object");
            }

            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating host object for '" + name + "'"
                         + " from '" + template + "'...");
        }

        final Scriptable parentScope = scriptGlobalScope;
        try {
            // OpenJDK is not able to do the conversion from Class<T> to Class.
            // That's why we have to do it before calling define.
            @SuppressWarnings("rawtypes")
            final Class clazz = template;
            ScriptableObject.defineClass(parentScope, clazz);
        } catch (java.lang.IllegalAccessException iae) {
            throw new SemanticError(iae);
        } catch (java.lang.InstantiationException ie) {
            throw new SemanticError(ie);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            throw new SemanticError(ite);
        } catch (org.mozilla.javascript.EvaluatorException ee) {
            throw new SemanticError(ee);
        }

        final Context context = Context.getCurrentContext();
        final Scriptable scope = getScope();

        final Scriptable scriptable =
                context.newObject(scope, template.getSimpleName());
        scope.put(name, scope, scriptable);

        if (scriptable instanceof StandardSessionVariable) {
            final StandardSessionVariable reference =
                (StandardSessionVariable) scriptable;
            reference.setScripting(this);
        }
        return template.cast(scriptable);
    }

}
