/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test case for {@link ScriptingEngine}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class TestScriptingEngine {
    /** The scripting engine to test. */
    private ScriptingEngine scripting = null;

    /**
     * Set up the test environment.
     * @exception Exception
     *            set up failed
     */
    @Before
    public void setUp()
            throws Exception {
        scripting = new ScriptingEngine(null);
    }

    /**
     * Tear down the test environment.
     */
    @After
    public void tearDown() {
        scripting = null;
    }

    /**
     * Test method for {@link ScriptingEngine#setVariable(String, Object)}.
     */
    @Test
    public void testSetVariable() {
        String name = null;
        String value = null;
        scripting.setVariable(name, value);

        name = "name1";
        value = null;
        scripting.setVariable(name, value);
        Assert.assertNull(scripting.getVariable(name));

        name = null;
        value = "value2";
        scripting.setVariable(name, value);

        name = "name3";
        value = "value3";
        scripting.setVariable(name, value);
        Assert.assertEquals(value, scripting.getVariable(name));

        value = "value4";
        scripting.setVariable(name, value);
        Assert.assertEquals(value, scripting.getVariable(name));
    }

    /**
     * Test method for  {@link ScriptingEngine#getVariable(String)}.
     */
    @Test
    public void testGetVariable() {
        String name = null;
        Assert.assertNull(scripting.getVariable(name));

        name = "name1";
        Assert.assertNull(scripting.getVariable(name));

        String name2 = "name2";
        String value2 = "value2";
        scripting.setVariable(name2, value2);
        Assert.assertEquals(value2, scripting.getVariable(name2));

        String name3 = "name3";
        String value3 = "value3";
        scripting.setVariable(name3, value3);
        Assert.assertEquals(value2, scripting.getVariable(name2));
        Assert.assertEquals(value3, scripting.getVariable(name3));

        String value4 = "value4";
        scripting.setVariable(name3, value4);
        Assert.assertEquals(value2, scripting.getVariable(name2));
        Assert.assertEquals(value4, scripting.getVariable(name3));

        String value5 = null;
        scripting.setVariable(name3, value5);
        Assert.assertEquals(value2, scripting.getVariable(name2));
        Assert.assertNull(scripting.getVariable(name3));
    }

    /**
     * Test method for  {@link ScriptingEngine#isVariableDefined(String)}.
     */
    @Test
    public void testIsVariableDefined() {
        String name1 = null;
        Assert.assertFalse(scripting.isVariableDefined(name1));

        String name2 = "name2";
        String value2 = "value2";
        scripting.setVariable(name2, value2);
        Assert.assertFalse(scripting.isVariableDefined(name1));
        Assert.assertTrue(scripting.isVariableDefined(name2));

        String name3 = "name3";
        String value3 = null;
        scripting.setVariable(name3, value3);
        Assert.assertFalse(scripting.isVariableDefined(name1));
        Assert.assertTrue(scripting.isVariableDefined(name2));
        Assert.assertTrue(scripting.isVariableDefined(name3));
    }

    /**
     * Test method for  {@link ScriptingEngine#isVariableDefined(String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testEval() throws JVoiceXMLEvent {
        final String name1 = "name1";
        final String value1 = "value1";
        scripting.setVariable(name1, value1);
        Assert.assertTrue(scripting.isVariableDefined(name1));
        Assert.assertEquals(Boolean.TRUE, scripting.eval(name1 + "=='value1'"));

        SemanticError error = null;
        try {
            scripting.eval("somethingUndefined=42");
        } catch (SemanticError e) {
            error = e;
        }
        Assert.assertNotNull(
                "expected an error when accessing an undeclared variable",
                error);
    }

    /**
     * Test method for  {@link ScriptingEngine#removeVariable(String)}.
     */
    @Test
    public void testRemoveVariable() {
        String name1 = null;
        scripting.removeVariable(name1);

        String name2 = "name2";
        scripting.removeVariable(name2);

        String name3 = "name3";
        String value3 = "value3";
        scripting.setVariable(name3, value3);
        Assert.assertTrue(scripting.isVariableDefined(name3));
        scripting.removeVariable(name3);
        Assert.assertFalse(scripting.isVariableDefined(name3));

        String name4 = "name4";
        String value4 = "value4";
        scripting.setVariable(name4, value4);
        String name5 = "name5";
        String value5 = "value5";
        scripting.setVariable(name5, value5);

        Assert.assertTrue(scripting.isVariableDefined(name4));
        Assert.assertTrue(scripting.isVariableDefined(name5));

        scripting.removeVariable(name4);
        Assert.assertFalse(scripting.isVariableDefined(name4));
        Assert.assertTrue(scripting.isVariableDefined(name5));
        scripting.removeVariable(name5);
        Assert.assertFalse(scripting.isVariableDefined(name5));
        Assert.assertFalse(scripting.isVariableDefined(name5));
    }

    /**
     * Test method for
     * {@link ScriptingEngine#enterScope(org.jvoicexml.interpreter.scope.Scope, org.jvoicexml.interpreter.scope.Scope)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testEnterScope() throws JVoiceXMLEvent {
        final String name1 = "name1";
        final String value1 = "value1";
        scripting.setVariable(name1, value1);
        Assert.assertEquals(value1, scripting.getVariable(name1));

        final String name2 = "name2";
        final Object value2 = Context.getUndefinedValue();
        scripting.setVariable(name2, value2);
        Assert.assertEquals(value2, scripting.getVariable(name2));
        Assert.assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));

        scripting.enterScope(Scope.APPLICATION, Scope.SESSION);
        Assert.assertTrue(scripting.isVariableDefined(name1));
        Assert.assertEquals(value1, scripting.getVariable(name1));
        Assert.assertEquals(value2, scripting.getVariable(name2));
        Assert.assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));

        final String name3 = "name3";
        final String value3 = "value3";
        scripting.setVariable(name3, value3);
        Assert.assertTrue(scripting.isVariableDefined(name3));
        Assert.assertEquals(value3, scripting.getVariable(name3));
        Assert.assertEquals(Boolean.FALSE, scripting.eval(name3 + " == "
                + name1));

        final String value4 = "value4";
        scripting.setVariable(name2, value4);
        Assert.assertEquals(value4, scripting.getVariable(name2));

        scripting.exitScope(Scope.SESSION, Scope.APPLICATION);
        Assert.assertFalse(scripting.isVariableDefined(name3));
        Assert.assertEquals(value1, scripting.getVariable(name1));
        Assert.assertEquals(value4, scripting.getVariable(name2));
        Assert.assertNull(scripting.getVariable(name3));
        Assert.assertEquals(Boolean.TRUE, scripting.eval("'" + value1 + "' == "
                + name1));
        JVoiceXMLEvent error = null;
        try {
            Assert.assertEquals(Boolean.FALSE, scripting.eval(name3 + " == "
                    + name1));
        } catch (SemanticError e) {
            error = e;
        }
        Assert.assertNotNull("a semantic error should have been thrown", error);
    }

    /**
     * Test method for {@link ScriptingEngine#toJSON(org.mozilla.javascript.ScriptableObject)}.
     * @exception JVoiceXMLEvent test failed
     * @since 0.7.5
     */
    @Test
    public void testToJSON() throws JVoiceXMLEvent {
        scripting.eval("var A = new Object()");
        scripting.eval("A.B = 'test'");
        scripting.eval("A.C = new Object()");
        scripting.eval("A.C.D = 5");
        final ScriptableObject object =
            (ScriptableObject) scripting.getVariable("A");
        Assert.assertEquals("{\"B\":\"test\",\"C\":{\"D\":5}}",
                scripting.toJSON(object));
    }
}
