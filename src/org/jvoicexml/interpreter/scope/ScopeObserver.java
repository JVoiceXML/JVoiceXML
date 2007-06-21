/*
 * File:    $RCSfile: ScopeObserver.java,v $
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

package org.jvoicexml.interpreter.scope;

import java.util.Collection;
import java.util.Stack;

import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Central that gets notified about scope changes. A <code>ScopeObserver</code>
 * is bound to a session and manages the scope changes of a user within the
 * session.
 *
 * <p>
 * All scopes are maintained in a stack. If the <code>ScopeObserver</code>
 * gets notified about entering a new scope, the new scope is pushed
 * a the top of this stack. Exiting a scope will cause the observer to
 * remove the exited scope from the stack.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class ScopeObserver
        implements ScopePublisher {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScopeObserver.class);

    /** All registered scope subscribers. */
    private final Collection<ScopeSubscriber> scopeSubscriber;

    /** The observed  scopes. */
    private final Stack<Scope> scopes;

    /**
     * Constrcuct a new object.
     */
    public ScopeObserver() {
        scopeSubscriber = new java.util.ArrayList<ScopeSubscriber>();
        scopes = new Stack<Scope>();
    }

    /**
     * The application has entered the given scope.
     * @param scope The new scope.
     */
    public void enterScope(final Scope scope) {
        if (scope == null) {
            LOGGER.warn("ignoring entered null scope");

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("entering new scope '" + scope.getName() + "'...");
        }

        final Scope previous = currentScope();
        scopes.push(scope);

        synchronized (scopeSubscriber) {
            for (ScopeSubscriber listener : scopeSubscriber) {
                listener.enterScope(previous, scope);
            }
        }
    }

    /**
     * The application has left the given scope.
     * @param scope The scope to exit.
     */
    public void exitScope(final Scope scope) {
        if (scope == null) {
            LOGGER.warn("ignoring exited null scope");

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("exiting scope '" + scope.getName() + "'...");
        }

        final int position = scopes.search(scope);

        if (position < 0) {
            LOGGER.warn("mismatched scope order. cannot exit '"
                        + scope.getName() + "'");

            return;
        }

        for (int i = 0; i < position; i++) {
            scopes.pop();
        }

        final Scope previous = currentScope();

        synchronized (scopeSubscriber) {
            for (ScopeSubscriber listener : scopeSubscriber) {
                listener.exitScope(scope, previous);
            }
        }
    }

    /**
     * Retrieve the current scope.
     * @return The current scope, <code>null</code> if there is noen.
     */
    public Scope currentScope() {
        if (scopes.empty()) {
            return null;
        }

        return scopes.peek();
    }

    /**
     * {@inheritDoc}
     */
    public void addScopeSubscriber(final ScopeSubscriber subscriber) {
        synchronized (scopeSubscriber) {
            scopeSubscriber.add(subscriber);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeScopeSubscriber(final ScopeSubscriber subscriber) {
        synchronized (scopeSubscriber) {
            scopeSubscriber.remove(subscriber);
        }
    }
}
