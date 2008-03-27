/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/interpreter/ScriptingEngine.java $
 * Version: $LastChangedRevision: 717 $
 * Date:    $Date: 2008-03-05 09:19:28 +0100 (Mi, 05 Mrz 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.interpreter;

import junit.framework.TestCase;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.Context;

/**
 * Test case for {@link ScriptingEngine}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 233 $
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestScriptingEngine
        extends TestCase {
    /** The scripting engine to test. */
    private ScriptingEngine scripting = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        super.setUp();
        scripting = new ScriptingEngine(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
            throws Exception {
        scripting = null;

        super.tearDown();
    }

    /**
     * Test method for {@link ScriptingEngine#setVariable(String, Object)}.
     */
    public void testSetVariable() {
        String name = null;
        String value = null;
        scripting.setVariable(name, value);

        name = "name1";
        value = null;
        scripting.setVariable(name, value);
        assertNull(scripting.getVariable(name));

        name = null;
        value = "value2";
        scripting.setVariable(name, value);

        name = "name3";
        value = "value3";
        scripting.setVariable(name, value);
        assertEquals(value, scripting.getVariable(name));

        value = "value4";
        scripting.setVariable(name, value);
        assertEquals(value, scripting.getVariable(name));
    }

    /**
     * Test method for  {@link ScriptingEngine#getVariable(String)}.
     */
    public void testGetVariable() {
        String name = null;
        assertNull(scripting.getVariable(name));

        name = "name1";
        assertNull(scripting.getVariable(name));

        String name2 = "name2";
        String value2 = "value2";
        scripting.setVariable(name2, value2);
        assertEquals(value2, scripting.getVariable(name2));

        String name3 = "name3";
        String value3 = "value3";
        scripting.setVariable(name3, value3);
        assertEquals(value2, scripting.getVariable(name2));
        assertEquals(value3, scripting.getVariable(name3));

        String value4 = "value4";
        scripting.setVariable(name3, value4);
        assertEquals(value2, scripting.getVariable(name2));
        assertEquals(value4, scripting.getVariable(name3));

        String value5 = null;
        scripting.setVariable(name3, value5);
        assertEquals(value2, scripting.getVariable(name2));
        assertNull(scripting.getVariable(name3));
    }

    /**
     * Test method for  {@link ScriptingEngine#isVariableDefined(String)}.
     */
    public void testIsVariableDefined() {
        String name1 = null;
        assertFalse(scripting.isVariableDefined(name1));

        String name2 = "name2";
        String value2 = "value2";
        scripting.setVariable(name2, value2);
        assertFalse(scripting.isVariableDefined(name1));
        assertTrue(scripting.isVariableDefined(name2));

        String name3 = "name3";
        String value3 = null;
        scripting.setVariable(name3, value3);
        assertFalse(scripting.isVariableDefined(name1));
        assertTrue(scripting.isVariableDefined(name2));
        assertTrue(scripting.isVariableDefined(name3));
    }

    /**
     * Test method for  {@link ScriptingEngine#isVariableDefined(String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    public void testEval() throws JVoiceXMLEvent {
        final String name1 = "name1";
        final String value1 = "value1";
        scripting.setVariable(name1, value1);
        assertTrue(scripting.isVariableDefined(name1));
        assertEquals(Boolean.TRUE, scripting.eval(name1 + "=='value1'"));
    }

    /**
     * Test method for  {@link ScriptingEngine#removeVariable(String)}.
     */
    public void testRemoveVariable() {
        String name1 = null;
        scripting.removeVariable(name1);

        String name2 = "name2";
        scripting.removeVariable(name2);

        String name3 = "name3";
        String value3 = "value3";
        scripting.setVariable(name3, value3);
        assertTrue(scripting.isVariableDefined(name3));
        scripting.removeVariable(name3);
        assertFalse(scripting.isVariableDefined(name3));

        String name4 = "name4";
        String value4 = "value4";
        scripting.setVariable(name4, value4);
        String name5 = "name5";
        String value5 = "value5";
        scripting.setVariable(name5, value5);

        assertTrue(scripting.isVariableDefined(name4));
        assertTrue(scripting.isVariableDefined(name5));

        scripting.removeVariable(name4);
        assertFalse(scripting.isVariableDefined(name4));
        assertTrue(scripting.isVariableDefined(name5));
        scripting.removeVariable(name5);
        assertFalse(scripting.isVariableDefined(name5));
        assertFalse(scripting.isVariableDefined(name5));
    }

    /**
     * Test method for
     * {@link ScriptingEngine#enterScope(org.jvoicexml.interpreter.scope.Scope, org.jvoicexml.interpreter.scope.Scope)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    public void testEnterScope() throws JVoiceXMLEvent {
        final String name1 = "name1";
        final String value1 = "value1";
        scripting.setVariable(name1, value1);
        assertEquals(value1, scripting.getVariable(name1));

        final String name2 = "name2";
        final Object value2 = Context.getUndefinedValue();
        scripting.setVariable(name2, value2);
        assertEquals(value2, scripting.getVariable(name2));
        assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));

        scripting.enterScope(Scope.APPLICATION, Scope.SESSION);
        assertTrue(scripting.isVariableDefined(name1));
        assertEquals(value1, scripting.getVariable(name1));
        assertEquals(value2, scripting.getVariable(name2));
        assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));

        final String name3 = "name3";
        final String value3 = "value3";
        scripting.setVariable(name3, value3);
        assertTrue(scripting.isVariableDefined(name3));
        assertEquals(value3, scripting.getVariable(name3));
        assertEquals(Boolean.FALSE, scripting.eval(name3 + " == "
                + name1));

        final String value4 = "value4";
        scripting.setVariable(name2, value4);
        assertEquals(value4, scripting.getVariable(name2));

        scripting.exitScope(Scope.SESSION, Scope.APPLICATION);
        assertFalse(scripting.isVariableDefined(name3));
        assertEquals(value1, scripting.getVariable(name1));
        assertEquals(value4, scripting.getVariable(name2));
        assertNull(scripting.getVariable(name3));
        assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));
        JVoiceXMLEvent error = null;
        try {
            assertEquals(Boolean.FALSE, scripting.eval(name3 + " == "
                    + name1));
        } catch (SemanticError e) {
            error = e;
        }
        assertNotNull("a semantic error should have been thrown", error);
    }
}
