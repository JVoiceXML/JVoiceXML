/*
 * File:    $RCSfile: TestScopedMap.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2006/03/23 10:46:31 $
 * Author:  $Author: schnelle $
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
 */

package org.jvoicexml.interpreter.scope;

import java.util.Collection;

import junit.framework.TestCase;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlSession;
import org.jvoicexml.application.JVoiceXmlApplication;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.event.error.*;

/**
 * Test case for org.jvoicexml.interpreter.scope.ScopedMap.
 * 
 * @see org.jvoicexml.interpreter.scope.ScopedMap
 * 
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 * 
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestScopedMap
        extends TestCase {

    /** The VoiceXML interpreter context to use. */
    private VoiceXmlInterpreterContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        final JVoiceXmlMain jvxml = JVoiceXmlMain.getInstance();
        final Application dummy = new JVoiceXmlApplication("dummy", null);

        final ApplicationRegistry registry = jvxml.getApplicationRegistry();
        registry.register(dummy);

        JVoiceXmlSession session;

        try {
            session = (JVoiceXmlSession) jvxml.createSession(null, "dummy");
        } catch (ErrorEvent error) {
            session = null;
            fail(error.getMessage());
        }

        context = new VoiceXmlInterpreterContext(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        context.close();
        context = null;
    }

    /**
     * Test method for
     * 'org.jvoicexml.interpreter.scope.ScopedMap.get(java.lang.Object).
     * 
     * @see ScopedMap#get(java.lang.Object)
     */
    public void testGet() {
        final ScopedMap<String, String> map = new ScopedMap<String, String>(context);
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
     * Test method for 'org.jvoicexml.interpreter.scope.ScopedMap.put(K,V).
     * 
     * @see ScopedMap#put(K,V)
     */
    public void testPut() {
        final ScopedMap<String, String> map = new ScopedMap<String, String>(context);

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
     * Test method for 'org.jvoicexml.interpreter.scope.ScopedMap.values().
     * 
     * @see ScopedMap#values()
     */
    public void testValues() {
        final ScopedMap<String, String> map = new ScopedMap<String, String>(context);

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
