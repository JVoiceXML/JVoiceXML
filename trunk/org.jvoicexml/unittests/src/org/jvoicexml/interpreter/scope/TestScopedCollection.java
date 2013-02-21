/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Test case for {@link org.jvoicexml.interpreter.scope.ScopedCollection}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestScopedCollection
        extends TestCase {
    /** The VoiceXML interpreter context to use. */
    private ScopeObserver observer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        observer = new ScopeObserver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
            throws Exception {
        observer = null;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.scope.ScopedCollection#add(java.lang.Object)}.
     */
    public void testAdd() {
        ScopedCollection<String> collection =
            new ScopedCollection<String>(observer);

        String test1 = "test1";
        String test2 = "test2";

        assertFalse(collection.contains(test1));
        assertFalse(collection.contains(test2));
        collection.add(test1);
        collection.add(test2);
        assertTrue(collection.contains(test1));
        assertTrue(collection.contains(test2));
        assertEquals(2, collection.size());

        collection.enterScope(Scope.SESSION, Scope.DOCUMENT);
        String test3 = "test3";
        collection.add(test3);
        assertTrue(collection.contains(test1));
        assertTrue(collection.contains(test2));
        assertTrue(collection.contains(test3));
        assertEquals(3, collection.size());
        collection.exitScope(Scope.DOCUMENT, Scope.SESSION);
        assertTrue(collection.contains(test1));
        assertTrue(collection.contains(test2));
        assertFalse(collection.contains(test3));
        assertEquals(2, collection.size());
    }
}
