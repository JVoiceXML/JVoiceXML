/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestSubmitStrategy.java $
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

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.ccxml.Var;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link SubmitStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7
 */
public final class TestSubmitStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
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
     * Test method for
     * {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
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
        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(expr, Object.class)).thenReturn(
                next.toString());
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
     * Test method for
     * {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNone() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final SubmitStrategy strategy = new SubmitStrategy();
        executeTagStrategy(submit, strategy);
    }

    /**
     * Test method for
     * {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
     */
    @Test
    public void testExecuteParameters() throws Exception, JVoiceXMLEvent {
        final String name1 = "test";
        final String value1 = "Horst Buchholz";
        final String name2 = "test2";
        final String value2 = "Walter Giller";
        final String name3 = "test3";
        final File file = new File("test/test.wav");
        final String value3 = file.toURI().toString();

        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final URI next = new URI("http://www.jvoicexml.org");
        submit.setNextUri(next);
        final TokenList tokens = new TokenList();
        tokens.add(name1);
        tokens.add(name2);
        tokens.add(name3);
        submit.setNameList(tokens);

        final DataModel model = getDataModel();
        Mockito.when(model.readVariable(name1, Object.class))
                .thenReturn(value1);
        Mockito.when(model.readVariable(name2, Object.class))
                .thenReturn(value2);
        Mockito.when(model.readVariable(name3, Object.class))
                .thenReturn(value3);
        final DataModelObjectSerializer serializer = Mockito
                .mock(DataModelObjectSerializer.class);
        Mockito.when(model.getSerializer()).thenReturn(serializer);
        Mockito.when(serializer.serialize(model, name1, value1)).thenReturn(
                Arrays.asList(new KeyValuePair(name1, value1)));
        Mockito.when(serializer.serialize(model, name2, value2)).thenReturn(
                Arrays.asList(new KeyValuePair(name2, value2)));
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
        final Collection<KeyValuePair> parameters = descriptor.getParameters();
        Assert.assertTrue(parameters.contains(new KeyValuePair(name1, value1)));
        Assert.assertTrue(parameters.contains(new KeyValuePair(name2, value2)));
        Assert.assertTrue(parameters.contains(new KeyValuePair(name3, new File(
                value3))));
    }

    /**
     * Test method for
     * {@link SubmitStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
     */
    @Test(expected = SemanticError.class)
    public void testExecuteParametersUndefined() throws Exception,
            JVoiceXMLEvent {
        final String name1 = "test";
        final String value1 = "Horst Buchholz";
        final String name2 = "test2";
        final String value2 = "Walter Giller";
        final String name3 = "test3";
        final File file = new File("test/test.wav");
        final String value3 = file.toURI().toString();

        final Block block = createBlock();
        final Submit submit = block.appendChild(Submit.class);
        final URI next = new URI("http://www.jvoicexml.org");
        submit.setNextUri(next);
        final TokenList tokens = new TokenList();
        tokens.add(name1);
        tokens.add(name2);
        tokens.add(name3);
        tokens.add("test4");
        submit.setNameList(tokens);

        final DataModel model = getDataModel();
        Mockito.when(model.readVariable(name1, Object.class))
                .thenReturn(value1);
        Mockito.when(model.readVariable(name2, Object.class))
                .thenReturn(value2);
        Mockito.when(model.readVariable(name3, Object.class))
                .thenReturn(value3);
        Mockito.when(model.readVariable("test4", Object.class)).thenThrow(
                new SemanticError("no test 4"));
        final DataModelObjectSerializer serializer = Mockito
                .mock(DataModelObjectSerializer.class);
        Mockito.when(model.getSerializer()).thenReturn(serializer);
        Mockito.when(serializer.serialize(model, name1, value1)).thenReturn(
                Arrays.asList(new KeyValuePair(name1, value1)));
        Mockito.when(serializer.serialize(model, name2, value2)).thenReturn(
                Arrays.asList(new KeyValuePair(name2, value2)));
        final SubmitStrategy strategy = new SubmitStrategy();
        executeTagStrategy(submit, strategy);
    }
}
