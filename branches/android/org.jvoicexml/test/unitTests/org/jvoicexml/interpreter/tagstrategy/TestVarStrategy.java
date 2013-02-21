/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestVarStrategy.java $
 * Version: $LastChangedRevision: 2498 $
 * Date:    $Date: 2011-01-11 22:24:24 -0600 (mar, 11 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.interpreter.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Var;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class provides a test case for the {@link VarStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2498 $
 * @since 0.6
 */
public final class TestVarStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteUndefined() throws Exception {
        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);

        final VarStrategy strategy = new VarStrategy();
        try {
            executeTagStrategy(var, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(org.mozilla.javascript.Undefined.instance,
                getScriptingEngine().getVariable(name));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteExpr() throws Exception {
        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);
        var.setExpr("'testvalue'");

        final VarStrategy strategy = new VarStrategy();
        try {
            executeTagStrategy(var, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("testvalue",
                getScriptingEngine().getVariable(name));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteIntegerExpr() throws Exception {
        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);
        var.setExpr("42");

        final VarStrategy strategy = new VarStrategy();
        try {
            executeTagStrategy(var, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(new Integer(42),
                getScriptingEngine().getVariable(name));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteComplexExpr() throws Exception {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("a", 42);

        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);
        var.setExpr("a + 1");

        final VarStrategy strategy = new VarStrategy();
        try {
            executeTagStrategy(var, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(new Double(43),
                getScriptingEngine().getVariable(name));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteJavascriptObject() throws Exception {
        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);
        var.setExpr("new Object()");

        final VarStrategy strategy = new VarStrategy();
        try {
            executeTagStrategy(var, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(getScriptingEngine().getVariable(name)
                instanceof ScriptableObject);
    }
}
