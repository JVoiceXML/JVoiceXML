/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.ccxml.Var;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Data;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;
import org.w3c.dom.Document;

/**
 * Test case for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
public final class TestDataStrategy extends TagStrategyTestBase {
    /** The demo script. */
    private static final String XML;

    static {
        XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<quote xmlns=\"http://www.example.org\">"
                + "  <ticker>F</ticker>" + "  <name>Ford Motor Company</name>"
                + "  <change>1.00</change>" + "  <last>30.00</last>"
                + "</quote>";
    }

    /** The URI of the test script. */
    private URI uri;

    /** The document server. */
    private JVoiceXmlDocumentServer server;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final DocumentMap map = DocumentMap.getInstance();
        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());

        uri = map.getUri("/xml");
        map.addDocument(uri, XML);
        final VoiceXmlInterpreterContext context = getContext();
        Mockito.when(context.getDocumentServer()).thenReturn(server);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteVariableExists() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrc(uri);
        data.setName(name);

        final DataModel model = getDataModel();
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.TRUE);

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).updateVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteVariableUndefined()
            throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrc(uri);
        data.setName(name);

        final DataModel model = getDataModel();
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.FALSE);

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).createVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteExprVariableExists()
            throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Var varSrcexpr = vxml.appendChild(Var.class);
        final String srcexpr = "myexpr";
        varSrcexpr.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrcexpr(srcexpr);
        data.setName(name);

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(srcexpr, Object.class))
                .thenReturn(uri.toString());
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.TRUE);

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).updateVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteExprVariableUndefined()
            throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Var varSrcexpr = vxml.appendChild(Var.class);
        final String srcexpr = "myexpr";
        varSrcexpr.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrcexpr(srcexpr);
        data.setName(name);

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(srcexpr, Object.class))
                .thenReturn(uri.toString());
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.FALSE);

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).createVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNoneSpecified() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed
     * @exception Exception
     *                test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteBoth() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Var varSrcexpr = vxml.appendChild(Var.class);
        final String srcexpr = "srcexpr";
        varSrcexpr.setName(srcexpr);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrcexpr(srcexpr);
        data.setSrc(uri);
        data.setName(name);

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(srcexpr, Object.class))
                .thenReturn(uri.toString());

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed.
     * @throws Exception
     *             test failed
     */
    @Test
    public void testExecuteNamelistVariableExists()
            throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrc(uri);
        data.setName(name);
        final String name1 = "actor";
        final TokenList namelist = new TokenList();
        namelist.add(name1);
        data.setNameList(namelist);

        final DataModel model = getDataModel();
        Mockito.when(model.readVariable(name1, Object.class))
                .thenReturn("Horst Buchholz");
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.TRUE);
        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).updateVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
        // TODO check that the namelist was submitted
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             test failed.
     * @throws Exception
     *             test failed
     */
    @Test
    public void testExecuteNamelistVariableUndefined()
            throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrc(uri);
        data.setName(name);
        final String name1 = "actor";
        final TokenList namelist = new TokenList();
        namelist.add(name1);
        data.setNameList(namelist);

        final DataModel model = getDataModel();
        Mockito.when(model.readVariable(name1, Object.class))
                .thenReturn("Horst Buchholz");
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.FALSE);
        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Mockito.verify(model).createVariable(Mockito.eq(name),
                Mockito.isA(Document.class));
        // TODO check that the namelist was submitted
    }
}
