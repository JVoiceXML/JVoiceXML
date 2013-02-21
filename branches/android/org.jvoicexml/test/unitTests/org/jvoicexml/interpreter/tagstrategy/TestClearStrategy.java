/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestClearStrategy.java $
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 04:25:46 -0600 (lun, 10 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Clear;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mozilla.javascript.Context;

/**
 * This class provides a test case for the {@link ClearStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2493 $
 * @since 0.6
 */
public final class TestClearStrategy
        extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecute() throws Exception {
        final String var = "test";
        final Block block = createBlock();
        final Clear clear = block.appendChild(Clear.class);
        clear.setNamelist(var);

        getScriptingEngine().setVariable(var, "assigned");
        Assert.assertEquals("assigned", getScriptingEngine().getVariable(var));

        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed
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

        getScriptingEngine().setVariable(var1, "assigned1");
        getScriptingEngine().setVariable(var2, "assigned2");
        getScriptingEngine().setVariable(var3, "assigned3");

        Assert.assertEquals("assigned1",
                getScriptingEngine().getVariable(var1));
        Assert.assertEquals("assigned2",
                getScriptingEngine().getVariable(var2));
        Assert.assertEquals("assigned3",
                getScriptingEngine().getVariable(var3));

        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var1));
        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var2));
        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var3));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception JVoiceXMLEvent test failed
     * @exception Exception test failed
     */
    @Test
    public void testExecuteInputItem() throws JVoiceXMLEvent, Exception {
        final String var = "testfield";
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("testfield");
        final InputItem inputItem = new FieldFormItem(getContext(), field);
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("dummy");
        inputItem.setFormItemVariable(result);
        inputItem.incrementPromptCount();
        /* @todo Check the event counter. */
        final Block block = createBlock(document);
        final Clear clear = block.appendChild(Clear.class);
        clear.setNamelist(var);

        Assert.assertEquals("dummy", getScriptingEngine().getVariable(var));
        Assert.assertEquals(2, inputItem.getPromptCount());

        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var));
        Assert.assertEquals(1, inputItem.getPromptCount());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception JVoiceXMLEvent test failed
     * @exception Exception test failed
     */
    @Test
    public void testExecuteInputEmpty() throws JVoiceXMLEvent, Exception {
        final String var = "testfield";
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("testfield");
        final InputItem inputItem = new FieldFormItem(getContext(), field);
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("dummy2");
        inputItem.setFormItemVariable(result);
        inputItem.incrementPromptCount();
        /* @todo Check the event counter. */
        final Block block = form.appendChild(Block.class);
        final Clear clear = block.appendChild(Clear.class);
        clear.setNamelist("");

        createFia(form);

        Assert.assertEquals("dummy2", getScriptingEngine().getVariable(var));
        Assert.assertEquals(2, inputItem.getPromptCount());

        ClearStrategy strategy = new ClearStrategy();
        try {
            executeTagStrategy(clear, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(Context.getUndefinedValue(), getScriptingEngine()
                .getVariable(var));
        Assert.assertEquals(1, inputItem.getPromptCount());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ClearStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecuteNotDeclared() throws Exception {
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
