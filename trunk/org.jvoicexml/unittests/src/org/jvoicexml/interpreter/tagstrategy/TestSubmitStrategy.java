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

import java.io.File;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.ccxml.Var;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * This class provides a test case for the {@link SubmitStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestSubmitStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final URI next = new URI("http://www.jvoicexml.org");
        submit.setNextUri(next);
        final SubmitStrategy strategy = new SubmitStrategy();
        SubmitEvent event = null;
        try {
            executeTagStrategy(submit, strategy);
        } catch (SubmitEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        final DocumentDescriptor descriptor = event.getDocumentDescriptor();
        Assert.assertEquals(next, descriptor.getUri());
        Assert.assertEquals(RequestMethod.GET, descriptor.getMethod());
    }

    /**
     * Test method for {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecuteExpr() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var varSrcexpr = vxml.appendChild(Var.class);
        final String expr = "expr";
        varSrcexpr.setName(expr);
        final Block block = createBlock(doc);
        final Submit submit = block.appendChild(Submit.class);
        submit.setExpr(expr);
        final URI next = new URI("http://www.jvoicexml.org");
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable(expr, next.toString());
        final SubmitStrategy strategy = new SubmitStrategy();
        SubmitEvent event = null;
        try {
            executeTagStrategy(submit, strategy);
        } catch (SubmitEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        final DocumentDescriptor descriptor = event.getDocumentDescriptor();
        Assert.assertTrue(descriptor.isForceLoad());
        Assert.assertEquals(next, descriptor.getUri());
        Assert.assertEquals(RequestMethod.GET, descriptor.getMethod());
    }

    /**
     * Test method for {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNone() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final SubmitStrategy strategy = new SubmitStrategy();
        executeTagStrategy(submit, strategy);
    }

    /**
     * Test method for {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecuteParameters() throws Exception, JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        final String name1 = "test";
        final String value1 = "Horst Buchholz";
        scripting.setVariable(name1, value1);
        final String name2 = "test2";
        final String value2 = "Walter Giller";
        scripting.setVariable(name2, value2);
        final String name3 = "test3";
        final File file = new File("test/test.wav");
        final String value3 = file.toURI().toString();
        scripting.setVariable(name3, value3);

        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final URI next = new URI("http://www.jvoicexml.org");
        submit.setNextUri(next);
        final TokenList tokens = new TokenList();
        tokens.add(name1);
        tokens.add(name2);
        tokens.add(name3);
        submit.setNameList(tokens);
        final SubmitStrategy strategy = new SubmitStrategy();
        SubmitEvent event = null;
        try {
            executeTagStrategy(submit, strategy);
        } catch (SubmitEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        final DocumentDescriptor descriptor = event.getDocumentDescriptor();
        Assert.assertTrue(descriptor.isForceLoad());
        Assert.assertEquals(next, descriptor.getUri());
        Assert.assertEquals(RequestMethod.GET, descriptor.getMethod());
        Assert.assertEquals(value1, descriptor.getParameters().get(name1));
        Assert.assertEquals(value2, descriptor.getParameters().get(name2));
        final File expectedFile = new File(file.toURI().toString());
        final File actualFile = (File) descriptor.getParameters().get(name3);
        Assert.assertEquals(0, expectedFile.compareTo(actualFile));
    }

    /**
     * Test method for {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecuteParametersCompoundObject()
        throws Exception, JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval("var A = new Object();");
        scripting.eval("A.B = 'test';");
        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final URI next = new URI("http://www.jvoicexml.org");
        submit.setNextUri(next);
        final TokenList tokens = new TokenList();
        tokens.add("A");
        submit.setNameList(tokens);
        final SubmitStrategy strategy = new SubmitStrategy();
        SubmitEvent event = null;
        try {
            executeTagStrategy(submit, strategy);
        } catch (SubmitEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        final DocumentDescriptor descriptor = event.getDocumentDescriptor();
        Assert.assertTrue(descriptor.isForceLoad());
        Assert.assertEquals(next, descriptor.getUri());
        Assert.assertEquals(RequestMethod.GET, descriptor.getMethod());
        Assert.assertEquals(scripting.getVariable("A"),
                descriptor.getParameters().get("A"));
    }
}
