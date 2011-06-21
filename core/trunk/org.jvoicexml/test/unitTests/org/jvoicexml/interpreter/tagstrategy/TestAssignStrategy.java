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

package org.jvoicexml.interpreter.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Block;

/**
 * This class provides a test case for the {@link AssignStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestAssignStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link AssignStrategy#newInstance()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testNewInstance() throws Exception {
        final AssignStrategy strategy = new AssignStrategy();
        AssignStrategy clonedStrategy1 = (AssignStrategy)
            strategy.newInstance();
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        getScriptingEngine().setVariable(var, "");

        try {
            executeTagStrategy(assign, clonedStrategy1);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("assigned", getScriptingEngine().getVariable(var));

        AssignStrategy clonedStrategy2 = (AssignStrategy)
        strategy.newInstance();
        Assert.assertEquals("assigned", clonedStrategy1.getAttribute(
                Assign.ATTRIBUTE_EXPR));
        Assert.assertNull(clonedStrategy2.getAttribute(Assign.ATTRIBUTE_EXPR));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecute() throws Exception {
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        getScriptingEngine().setVariable(var, "");

        AssignStrategy strategy = new AssignStrategy();
        try {
            executeTagStrategy(assign, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("assigned", getScriptingEngine().getVariable(var));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testExecuteCompoundObject() throws Exception, JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval("var A=new Object();");
        final String var = "A.B";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        AssignStrategy strategy = new AssignStrategy();
        try {
            executeTagStrategy(assign, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals("assigned", scripting.eval(var + ";"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testExecuteNotCreated() throws Exception {
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        AssignStrategy strategy = new AssignStrategy();
        SemanticError error = null;
        try {
            executeTagStrategy(assign, strategy);
        } catch (SemanticError se) {
            error = se;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull("A semantic error should have been thrown", error);
    }
}
