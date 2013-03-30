/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/scope/ScopeObserver.java $
 * Version: $LastChangedRevision: 2698 $
 * Date:    $Date: 2011-06-06 06:37:55 -0500 (lun, 06 jun 2011) $
 * Author:  $LastChangedBy: gonzman83 $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;

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
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2698 $
 *
 * @since 0.3
 */
public final class ScopeObserver
        implements ScopePublisher {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ScopeObserver.class);

    /** All registered scope subscribers. */
    private final Collection<ScopeSubscriber> scopeSubscriber;

    /** The observed  scopes. */
    private final Stack<Scope> scopes;

    /**
     * Constructs a new object.
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

        LOGGER.info("entering new scope '" + scope.getName() + "'...");

        final Scope previous = currentScope();
        scopes.push(scope);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current scope stack: " + scopes);
        }

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

        LOGGER.info("exiting scope '" + scope.getName() + "'...");

        final int position = scopes.search(scope);
        if (position < 0) {
            LOGGER.warn("mismatched scope order. cannot exit '"
                        + scope.getName() + "'");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("current scope stack: " + scopes);
            }
            return;
        }

        for (int i = 0; i < position; i++) {
            scopes.pop();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current scope stack: " + scopes);
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
     * @return The current scope, <code>null</code> if there is none.
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
