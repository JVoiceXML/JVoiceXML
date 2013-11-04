/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/scope/TestScopeObserver.java $
 * Version: $LastChangedRevision: 3839 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test case for org.jvoicexml.interpreter.scope.ScopeObserver.
 *
 * @see org.jvoicexml.interpreter.scope.ScopeObserver
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3839 $
 */
public final class TestScopeObserver {
    /** The observer to test. */
    private ScopeObserver observer;

    /**
     * Set up the test environment.
     * @exception Exception set up failed
     */
    @Before
    public void setUp()
            throws Exception {
        observer = new ScopeObserver();
    }

    /**
     * Test method for
     * 'ScopeObserver.enterScope(Scope).
     *
     * @see ScopeObserver#enterScope()
     */
    @Test
    public void testEnterScope() {
        Assert.assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        Assert.assertEquals(Scope.SESSION, observer.currentScope());

        observer.enterScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.enterScope(null);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());
    }

    /**
     * Test method for
     * 'ScopeObserver.exitScope(Scope)'.
     *
     * @see ScopeObserver#enterScope()
     */
    @Test
    public void testExitScope() {
        Assert.assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.SESSION, observer.currentScope());

        observer.exitScope(Scope.SESSION);
        Assert.assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.DIALOG);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(null);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());
    }

    /**
     * Test method for
     * 'ScopeObserver.currentScope().
     *
     * @see ScopeObserver#currentScope()
     */
    @Test
    public void testCurrentScope() {
        Assert.assertNull(observer.currentScope());

        observer.enterScope(Scope.SESSION);
        observer.enterScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.APPLICATION, observer.currentScope());

        observer.exitScope(Scope.APPLICATION);
        Assert.assertEquals(Scope.SESSION, observer.currentScope());

        observer.exitScope(Scope.SESSION);
        Assert.assertNull(observer.currentScope());
    }
}
