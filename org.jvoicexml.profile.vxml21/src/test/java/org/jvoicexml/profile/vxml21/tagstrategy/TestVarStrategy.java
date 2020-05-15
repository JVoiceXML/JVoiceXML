/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestVarStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Var;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link VarStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 4080 $
 * @since 0.6
 */
public final class TestVarStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
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

        final DataModel model = getDataModel();
        Mockito.verify(model).createVariable(name);
    }

    /**
     * Test method for
     * {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testExecuteExpr() throws Exception, JVoiceXMLEvent {
        final String name = "test";
        final Block block = createBlock();
        final Var var = block.appendChild(Var.class);
        var.setName(name);
        var.setExpr("'testvalue'");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(var.getExpr(), Object.class))
                .thenReturn(var.getExpr());

        final VarStrategy strategy = new VarStrategy();
        executeTagStrategy(var, strategy);

        Mockito.verify(model).createVariable(name, var.getExpr());
    }
}
