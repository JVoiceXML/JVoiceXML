/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestAssignStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.TagStrategy;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Block;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link AssignStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.6
 */
public final class TestAssignStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link AssignStrategy#newInstance()}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testNewInstance() throws Exception {
        final AssignStrategy strategy = new AssignStrategy();
        final TagStrategy strategy2 = strategy.newInstance();
        Assert.assertTrue(strategy2 instanceof AssignStrategy);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @throws SemanticError
     *             test failed
     */
    @Test
    public void testExecute() throws Exception, SemanticError {
        final AssignStrategy strategy = new AssignStrategy();
        AssignStrategy clonedStrategy1 = (AssignStrategy) strategy
                .newInstance();
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression("'assigned'", Object.class))
                .thenReturn("'assigned'");
        Mockito.when(model.toString("'assigned'")).thenReturn("'assigned'");
        try {
            executeTagStrategy(assign, clonedStrategy1);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Mockito.verify(model).evaluateExpression("'assigned'", Object.class);
        Mockito.verify(model).updateVariable(var, "'assigned'");
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .<br/>
     * Test assignment of null
     * 
     * @exception Exception
     *                Test failed.
     * @throws SemanticError
     *             test failed
     */
    @Test
    public void testExecuteNull() throws Exception, SemanticError {
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("null");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression("null", Object.class))
                .thenReturn(null);
        AssignStrategy strategy = new AssignStrategy();
        try {
            executeTagStrategy(assign, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(model).evaluateExpression("null", Object.class);
        Mockito.verify(model).updateVariable(var, null);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.AssignStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @throws SemanticError
     *             test failed
     */
    @Test
    public void testExecuteUndefined() throws Exception, SemanticError {
        final String var = "test";
        final Block block = createBlock();
        final Assign assign = block.appendChild(Assign.class);
        assign.setName(var);
        assign.setExpr("'assigned'");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression("'assigned'", Object.class))
                .thenThrow(new SemanticError("mock not created"));
        Mockito.when(model.toString("'assigned'")).thenReturn("'assigned'");
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
