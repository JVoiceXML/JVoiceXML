/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
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
 */

package org.jvoicexml.interpreter.scope;

import java.util.Collection;

import junit.framework.TestCase;

/**
 * Test case for {@link org.jvoicexml.interpreter.scope.ScopedMap}.
 *
 * @see org.jvoicexml.interpreter.scope.ScopedMap
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestScopedMap
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
     * Test method for
     * 'ScopedMap.get(java.lang.Object).
     *
     * @see ScopedMap#get(java.lang.Object)
     */
    public void testGet() {
        final ScopedMap<String, String> map =
                new ScopedMap<String, String>(observer);
        assertNull(map.get("nokey"));

        map.put("key1", "value1");
        assertEquals("value1", map.get("key1"));

        map.put("key2", "value2");
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        map.clear();
        assertNull(map.get("key1"));
        assertNull(map.get("key2"));
    }

    /**
     * Test method for
     * 'ScopedMap.put(K,V).
     *
     * @see ScopedMap#put(K,V)
     */
    public void testPut() {
        final ScopedMap<String, String> map =
                new ScopedMap<String, String>(observer);

        assertNull(map.put("key1", "value1"));
        assertEquals("value1", map.get("key1"));

        assertNull(map.put("key2", "value2"));
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        map.enterScope(Scope.SESSION, Scope.DOCUMENT);
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        assertEquals("value2", map.put("key2", "value3"));
        assertEquals("value1", map.get("key1"));
        assertEquals("value3", map.get("key2"));

        map.exitScope(Scope.DOCUMENT, Scope.SESSION);
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    /**
     * Test method for 'ScopedMap.values().
     *
     * @see ScopedMap#values()
     */
    public void testValues() {
        final ScopedMap<String, String> map =
                new ScopedMap<String, String>(observer);

        assertNull(map.put("key1", "value1"));
        assertEquals("value1", map.get("key1"));

        assertNull(map.put("key2", "value2"));
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        final Collection<String> values1 = map.values();
        assertEquals(2, values1.size());
        assertTrue(values1.contains("value1"));
        assertTrue(values1.contains("value2"));

        map.enterScope(Scope.SESSION, Scope.DOCUMENT);
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        final Collection<String> values2 = map.values();
        assertEquals(2, values2.size());
        assertTrue(values2.contains("value1"));
        assertTrue(values2.contains("value2"));

        assertEquals("value2", map.put("key2", "value3"));
        assertEquals("value1", map.get("key1"));
        assertEquals("value3", map.get("key2"));

        final Collection<String> values3 = map.values();
        assertEquals(2, values3.size());
        assertTrue(values3.contains("value1"));
        assertTrue(values3.contains("value3"));

        map.exitScope(Scope.DOCUMENT, Scope.SESSION);
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));

        final Collection<String> values4 = map.values();
        assertEquals(2, values4.size());
        assertTrue(values4.contains("value1"));
        assertTrue(values4.contains("value2"));
    }
}
