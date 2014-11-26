/*

 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestClearStrategy.java $
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
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Clear;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link ClearStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.6
 */
public final class TestClearStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecute() throws Exception {
        final String var = "test";
        final Block block = createBlock();
        final Clear clear = block.appendChild(Clear.class);
        clear.setNamelist(var);

        final DataModel model = getDataModel();
        Mockito.when(model.existsVariable(var)).thenReturn(true);
        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Mockito.verify(model).updateVariable(var, null);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteMultiple() throws Exception {
        final String var1 = "test1";
        final String var2 = "test2";
        final String var3 = "test3";

        final Block block = createBlock();
        final Clear clear = block.appendChild(Clear.class);
        final TokenList names = new TokenList();
        names.add(var1);
        names.add(var2);
        names.add(var3);
        clear.setNamelist(names);

        final DataModel model = getDataModel();
        Mockito.when(model.existsVariable(var1)).thenReturn(true);
        Mockito.when(model.existsVariable(var2)).thenReturn(true);
        Mockito.when(model.existsVariable(var3)).thenReturn(true);
        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Mockito.verify(model).updateVariable(var1, null);
        Mockito.verify(model).updateVariable(var2, null);
        Mockito.verify(model).updateVariable(var3, null);
    }
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @throws SemanticError 
     */
    @Test
    public void testExecuteNotDeclared() throws Exception, SemanticError {
        final String var = "test";
        final Block block = createBlock();
        final Clear clear = block.appendChild(Clear.class);
        clear.setNamelist(var);

        JVoiceXMLEvent failure = null;
        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (SemanticError e) {
            failure = e;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNotNull(failure);
    }
}
