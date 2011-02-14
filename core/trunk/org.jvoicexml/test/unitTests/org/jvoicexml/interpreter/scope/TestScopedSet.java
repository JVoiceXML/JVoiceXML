/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.test.interpreter.scope.DummyScopedSetObserver;

/**
 * Test case for {@link org.jvoicexml.interpreter.scope.ScopedSet}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class TestScopedSet {
    /** The scope observer to use. */
    private ScopeObserver observer;

    /**
     * Set up this test case.
     * @exception Exception
     *            if the set up fails
     */
    @Before
    public void setUp()
            throws Exception {
        observer = new ScopeObserver();
    }

    /**
     * Tear down this test case.
     * @exception Exception
     *            if the tear down fails
     */
    @After
    public void tearDown()
            throws Exception {
        observer = null;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.scope.ScopedSet#add(java.lang.Object)}.
     */
    @Test
    public void testAdd() {
        final ScopedSet<String> set = new ScopedSet<String>(observer);

        final String test1 = "test1";
        final String test2 = "test2";
        final String test3 = "test2";
        
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        set.add(test1);
        set.add(test2);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.enterScope(Scope.SESSION, Scope.DOCUMENT);
        final String test4 = "test4";
        set.add(test4);
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertTrue(set.contains(test4));
        Assert.assertEquals(3, set.size());
        set.exitScope(Scope.DOCUMENT, Scope.SESSION);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertFalse(set.contains(test4));
        Assert.assertEquals(2, set.size());
    }

    /**
     * Test case for {@link ScopedSet#addScopedSetObserver(ScopedSetObserver)}.
     * 
     * @since 0.7.3
     */
    @Test
    public void testAddObserver() {
        final ScopedSet<Object> set = new ScopedSet<Object>(observer);
        final DummyScopedSetObserver obs = new DummyScopedSetObserver();
        set.addScopedSetObserver(obs);
        final String test1 = "test1";
        final String test2 = "test2";
        final String test3 = "test2";
        
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        set.add(test1);
        set.add(test2);
        set.add(test3);
        Assert.assertNull(obs.getLastRemoved());

        set.enterScope(Scope.SESSION, Scope.DOCUMENT);
        final String test4 = "test4";
        set.add(test4);
        set.add(test3);
        Assert.assertNull(obs.getLastRemoved());
        set.exitScope(Scope.DOCUMENT, Scope.SESSION);
        final Collection<?> removed = obs.getLastRemoved(); 
        Assert.assertNotNull(removed);
        Assert.assertEquals(1, removed.size());
        Assert.assertEquals(test4, removed.iterator().next());
    }

    /**
     * Test case for {@link ScopedSet#remove(Object)}.
     * 
     * @since 0.7.5
     */
    @Test
    public void testRemove() {
        final ScopedSet<String> set = new ScopedSet<String>(observer);

        final String test1 = "test1";
        final String test2 = "test2";
        final String test3 = "test2";
        
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        set.add(test1);
        set.add(test2);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.enterScope(Scope.SESSION, Scope.DOCUMENT);
        final String test4 = "test4";
        set.add(test4);
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertTrue(set.contains(test4));
        Assert.assertEquals(3, set.size());
        set.remove(test1);
        Assert.assertFalse(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertTrue(set.contains(test4));
        Assert.assertEquals(2, set.size());
        set.remove(test2);
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        Assert.assertTrue(set.contains(test4));
        Assert.assertEquals(1, set.size());
        set.remove(test4);
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        Assert.assertFalse(set.contains(test4));
        Assert.assertEquals(0, set.size());
    }

    /**
     * Test case for {@link ScopedSet#removeAll(Collection)}.
     * 
     * @since 0.7.5
     */
    @Test
    public void testRemoveAll() {
        final ScopedSet<String> set = new ScopedSet<String>(observer);
        set.enterScope(null, Scope.SESSION);
        final String test1 = "test1";
        final String test2 = "test2";
        final String test3 = "test2";
        
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        set.add(test1);
        set.add(test2);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertEquals(2, set.size());
        set.enterScope(Scope.SESSION, Scope.DOCUMENT);
        final String test4 = "test4";
        set.add(test4);
        set.add(test3);
        Assert.assertTrue(set.contains(test1));
        Assert.assertTrue(set.contains(test2));
        Assert.assertTrue(set.contains(test4));
        Assert.assertEquals(3, set.size());
        final Collection<String> col = new java.util.ArrayList<String>();
        col.add(test1);
        col.add(test2);
        col.add(test4);
        set.removeAll(col);
        Assert.assertFalse(set.contains(test1));
        Assert.assertFalse(set.contains(test2));
        Assert.assertFalse(set.contains(test4));
        Assert.assertEquals(0, set.size());
    }
}
