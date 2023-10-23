/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test case for {@link org.jvoicexml.interpreter.scope.ScopedCollection}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestScopedCollection {
    /** The VoiceXML interpreter context to use. */
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
     * Test method for {@link org.jvoicexml.interpreter.scope.ScopedCollection#add(java.lang.Object)}.
     */
    @Test
    public void testAdd() {
        ScopedCollection<String> collection =
            new ScopedCollection<String>(observer);

        String test1 = "test1";
        String test2 = "test2";

        Assert.assertFalse(collection.contains(test1));
        Assert.assertFalse(collection.contains(test2));
        collection.add(test1);
        collection.add(test2);
        Assert.assertTrue(collection.contains(test1));
        Assert.assertTrue(collection.contains(test2));
        Assert.assertEquals(2, collection.size());

        collection.enterScope(Scope.SESSION, Scope.DOCUMENT);
        String test3 = "test3";
        collection.add(test3);
        Assert.assertTrue(collection.contains(test1));
        Assert.assertTrue(collection.contains(test2));
        Assert.assertTrue(collection.contains(test3));
        Assert.assertEquals(3, collection.size());
        collection.exitScope(Scope.DOCUMENT, Scope.SESSION);
        Assert.assertTrue(collection.contains(test1));
        Assert.assertTrue(collection.contains(test2));
        Assert.assertFalse(collection.contains(test3));
        Assert.assertEquals(2, collection.size());
    }
    
    /**
     * Test method for {@link ScopedCollection#iterator()}.
     * 
     * @since 0.7.9
     */
    @Test
    public void testIterator() {
        ScopedCollection<String> collection =
                new ScopedCollection<String>(observer);
        String test1 = "test1";
        String test2 = "test2";
        collection.enterScope(Scope.SESSION, Scope.DOCUMENT);
        collection.add(test1);
        collection.add(test2);
        Assert.assertTrue(collection.contains(test1));
        final Iterator<String> iterator = collection.iterator();
        Assert.assertEquals(test1, iterator.next());
        Assert.assertEquals(test2, iterator.next());
    }

    /**
     * Test method for {@link ScopedCollection#iterator()}.
     * 
     * @since 0.7.9
     */
    @Test
    public void testIteratorEnterScope() {
        ScopedCollection<String> collection =
                new ScopedCollection<String>(observer);
        String test1 = "test1";
        String test2 = "test2";
        collection.add(test1);
        collection.add(test2);
        final Iterator<String> iterator = collection.iterator();
        Assert.assertEquals(test1, iterator.next());
        Assert.assertEquals(test2, iterator.next());
        collection.enterScope(Scope.SESSION, Scope.DOCUMENT);
        final Iterator<String> iteratorScope = collection.iterator();
        Assert.assertEquals(test1, iteratorScope.next());
        Assert.assertEquals(test2, iteratorScope.next());
    }
}
