/*
 * File:    $RCSfile: TestRhinoScriptingEngine.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/03/23 10:45:52 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.interpreter.variables;

import junit.framework.TestCase;

/**
 * Test case for org.jvoicexml.interpreter.variables.RhinoScriptingEngine
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestRhinoScriptingEngine
        extends TestCase {
    /** The scripting engine to test. */
    private RhinoScriptingEngine rhinoScriptingEngine = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        super.setUp();
        rhinoScriptingEngine = new RhinoScriptingEngine(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
            throws Exception {
        rhinoScriptingEngine = null;

        super.tearDown();
    }

    /**
     * Test method for
     * 'RhinoScriptingEngine.setVariable()'
     *
     * @see RhinoScriptingEngine#setVariable()
     */
    public void testSetVariable() {
        String name = null;
        String value = null;
        rhinoScriptingEngine.setVariable(name, value);

        name = "name1";
        value = null;
        rhinoScriptingEngine.setVariable(name, value);
        assertNull(rhinoScriptingEngine.getVariable(name));

        name = null;
        value = "value2";
        rhinoScriptingEngine.setVariable(name, value);

        name = "name3";
        value = "value3";
        rhinoScriptingEngine.setVariable(name, value);
        assertEquals(value, rhinoScriptingEngine.getVariable(name));

        value = "value4";
        rhinoScriptingEngine.setVariable(name, value);
        assertEquals(value, rhinoScriptingEngine.getVariable(name));
    }

    /**
     * Test method for
     * 'RhinoScriptingEngine.getVariable()'
     *
     * @see RhinoScriptingEngine#setVariable()
     */
    public void testGetVariable() {
        String name = null;
        assertNull(rhinoScriptingEngine.getVariable(name));

        name = "name1";
        assertNull(rhinoScriptingEngine.getVariable(name));

        String name2 = "name2";
        String value2 = "value2";
        rhinoScriptingEngine.setVariable(name2, value2);
        assertEquals(value2, rhinoScriptingEngine.getVariable(name2));

        String name3 = "name3";
        String value3 = "value3";
        rhinoScriptingEngine.setVariable(name3, value3);
        assertEquals(value2, rhinoScriptingEngine.getVariable(name2));
        assertEquals(value3, rhinoScriptingEngine.getVariable(name3));

        String value4 = "value4";
        rhinoScriptingEngine.setVariable(name3, value4);
        assertEquals(value2, rhinoScriptingEngine.getVariable(name2));
        assertEquals(value4, rhinoScriptingEngine.getVariable(name3));

        String value5 = null;
        rhinoScriptingEngine.setVariable(name3, value5);
        assertEquals(value2, rhinoScriptingEngine.getVariable(name2));
        assertNull(rhinoScriptingEngine.getVariable(name3));
    }

    /**
     * Test method for
     * 'RhinoScriptingEngine.isVariableDefined()'
     *
     * @see RhinoScriptingEngine#isVariableDefined()
     */
    public void testIsVariableDefined() {
        String name1 = null;
        assertFalse(rhinoScriptingEngine.isVariableDefined(name1));

        String name2 = "name2";
        String value2 = "value2";
        rhinoScriptingEngine.setVariable(name2, value2);
        assertFalse(rhinoScriptingEngine.isVariableDefined(name1));
        assertTrue(rhinoScriptingEngine.isVariableDefined(name2));

        String name3 = "name3";
        String value3 = null;
        rhinoScriptingEngine.setVariable(name3, value3);
        assertFalse(rhinoScriptingEngine.isVariableDefined(name1));
        assertTrue(rhinoScriptingEngine.isVariableDefined(name2));
        assertTrue(rhinoScriptingEngine.isVariableDefined(name3));
    }

    /**
     * Test method for
     * 'RhinoScriptingEngine.removeVariable()'
     *
     * @see RhinoScriptingEngine#removeVariable()
     */
    public void testRemoveVariable() {
        String name1 = null;
        rhinoScriptingEngine.removeVariable(name1);

        String name2 = "name2";
        rhinoScriptingEngine.removeVariable(name2);

        String name3 = "name3";
        String value3 = "value3";
        rhinoScriptingEngine.setVariable(name3, value3);
        assertTrue(rhinoScriptingEngine.isVariableDefined(name3));
        rhinoScriptingEngine.removeVariable(name3);
        assertFalse(rhinoScriptingEngine.isVariableDefined(name3));

        String name4 = "name4";
        String value4 = "value4";
        rhinoScriptingEngine.setVariable(name4, value4);
        String name5 = "name5";
        String value5 = "value5";
        rhinoScriptingEngine.setVariable(name5, value5);

        assertTrue(rhinoScriptingEngine.isVariableDefined(name4));
        assertTrue(rhinoScriptingEngine.isVariableDefined(name5));

        rhinoScriptingEngine.removeVariable(name4);
        assertFalse(rhinoScriptingEngine.isVariableDefined(name4));
        assertTrue(rhinoScriptingEngine.isVariableDefined(name5));
        rhinoScriptingEngine.removeVariable(name5);
        assertFalse(rhinoScriptingEngine.isVariableDefined(name5));
        assertFalse(rhinoScriptingEngine.isVariableDefined(name5));
    }
}
