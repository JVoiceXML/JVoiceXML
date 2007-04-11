/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Stack;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopeSubscriber;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Scripting engine.
 *
 * @author Torben Hardt
 * @author Dirk SChnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class ScriptingEngine
    implements ScopeSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScriptingEngine.class);

    /** The parent block's scope. */
    private Scriptable scriptGlobalScope;

    /**
     * Container for the nested stacks. Part of var handling.
     * @todo Implement a working stack.
     */
    private final Stack<Scriptable> scopeStack = new Stack<Scriptable>();

    /**
     * Constructs a new object.
     * @param ctx The current VoiceXML interpreter context.
     */
    public ScriptingEngine(final VoiceXmlInterpreterContext ctx) {
        final Context context = Context.enter();

        context.setLanguageVersion(Context.VERSION_1_6);
        // create a initial scope, do NOT allow access to all java objects
        // check later if sealed initial scope should be used.
        scriptGlobalScope = context.initStandardObjects();
        Scriptable firstScope = context.newObject(scriptGlobalScope);
        firstScope.setParentScope(scriptGlobalScope);
        firstScope.setPrototype(scriptGlobalScope);
        scopeStack.push(firstScope);

        if (ctx != null) {
            final ScopeObserver observer = ctx.getScopeObserver();
            if (observer != null) {
                observer.addScopeSubscriber(this);
            }
        }
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
            LOGGER.warn("ignoring empty value expr");
            return Context.getUndefinedValue();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("evaluating '" + expr + "'...");
        }

        final Object result;
        final Context ctx = Context.getCurrentContext();
        final Scriptable scope = getScope();

        // evaluate expr with parent nodes scope
        try {
            result = ctx.evaluateString(scope, expr, "expr", 1, null);
        } catch (EcmaError ee) {
            throw new SemanticError(ee);
        }

        /** @todo Hack to get rid of all the ' in string handling. */
        final Object value;
        if (result instanceof String) {
            final String str = (String) result;
            value = str.replaceAll("'", "");
        } else {
            value = result;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("value: '" + value + "'");
        }

        return value;
    }

    /**
     * Sets a existing variable to a new value.
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
        scope.put(name, scope, value);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set '" + name + "' to '" + value + "'");
        }
    }

    /**
     * Gets the variables current value.
     *
     * @param name unique identifier
     * @return the variables value object
     */
    public Object getVariable(final String name) {
        final Scriptable scope = getScope();

        if (scope.has(name, scope)) {
            return scope.get(name, scope);
        }

        return null;
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
        scope.delete(name);
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

        return scope.has(name, scope);
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
    }

    /**
     * {@inheritDoc}
     */
    public void exitScope(final Scope previous, final Scope next) {
        if (scopeStack.isEmpty()) {
            return;
        }

        scopeStack.pop();
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
            ScriptableObject.defineClass(parentScope, template);
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

        return template.cast(scriptable);
    }
}
