/*
 * File:    $RCSfile: TestScopeObserver.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

/**
 * Test case for org.jvoicexml.interpreter.scope.ScopeObserver.
 *
 * @see org.jvoicexml.interpreter.scope.ScopeObserver
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestScopeObserver
        extends TestCase {
    /** The observer to test. */
    private ScopeObserver observer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp()
            throws Exception {
        super.setUp();
        observer = new ScopeObserver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown()
            throws Exception {
        observer = null;

        super.tearDown();
    }

    /**
     * Test method for
     * 'org.jvoicexml.interpreter.scope.ScopeObserver.enterScope(Scope).
     *
     * @see ScopeObserver#enterScope()
     */
    public void testEnterScope() {
        assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        assertEquals(Scope.SESSION, observer.currentScope());

        observer.enterScope(Scope.APPLICATION);
        assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.enterScope(null);
        assertEquals(Scope.APPLICATION, observer.currentScope());
    }

    /**
     * Test method for
     * 'org.jvoicexml.interpreter.scope.ScopeObserver.exitScope(Scope)
     *
     * @see ScopeObserver#enterScope()
     */
    public void testExitScope() {
        assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.APPLICATION);
        assertEquals(Scope.SESSION, observer.currentScope());

        observer.exitScope(Scope.SESSION);
        assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.DIALOG);
        assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(null);
        assertEquals(Scope.APPLICATION, observer.currentScope());
    }

    /**
     * Test method for
     * 'org.jvoicexml.interpreter.scope.ScopeObserver.currentScope().
     *
     * @see ScopeObserver#currentScope()
     */
    public void testCurrentScope() {
        assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.APPLICATION);
        assertEquals(Scope.SESSION, observer.currentScope());

        observer.exitScope(Scope.SESSION);
        assertNull(observer.currentScope());
    }
}
