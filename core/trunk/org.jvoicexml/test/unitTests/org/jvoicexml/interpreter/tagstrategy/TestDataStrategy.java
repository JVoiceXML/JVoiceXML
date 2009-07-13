/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestScriptStrategy.java $
 * Version: $LastChangedRevision: 956 $
 * Date:    $Date: 2008-06-10 09:27:44 +0200 (Di, 10 Jun 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.ccxml.Var;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Data;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 956 $
 * @since 0.7.1
 */
public final class TestDataStrategy
        extends TagStrategyTestBase {
    /** The demo script. */
    private static final String XML;

    static {
        XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<quote xmlns=\"http://www.example.org\">"
            + "  <ticker>F</ticker>"
            + "  <name>Ford Motor Company</name>"
            + "  <change>1.00</change>"
            + "  <last>30.00</last>"
            + "</quote>";
    }

    /** The URI of the test script. */
    private URI uri;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final DocumentMap map = DocumentMap.getInstance();
        final JVoiceXmlDocumentServer server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());

        uri = map.getUri("/xml");
        map.addDocument(uri, XML);

    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testExecute() throws JVoiceXMLEvent {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrc(uri);
        data.setName(name);
        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        Assert.assertTrue((Boolean) scripting.eval(
            "\"30.00\" == quote.documentElement.getElementsByTagNameNS("
            + "\"http://www.example.org\", \"last\").item(0).firstChild.data"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testExecuteExpr() throws JVoiceXMLEvent {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Var varSrcexpr = vxml.appendChild(Var.class);
        final String srcexpr = "scexpr";
        varSrcexpr.setName(name);
        final Block block = createBlock(doc);
        final Data data = block.appendChild(Data.class);
        data.setSrcexpr(srcexpr);
        data.setName(name);
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable(srcexpr, uri.toString());

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);

        Assert.assertTrue((Boolean) scripting.eval(
            "\"30.00\" == quote.documentElement.getElementsByTagNameNS("
            + "\"http://www.example.org\", \"last\").item(0).firstChild.data"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNoneSpecified() throws JVoiceXMLEvent {
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
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.DataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteBoth() throws JVoiceXMLEvent {
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
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable(srcexpr, uri.toString());

        final DataStrategy strategy = new DataStrategy();
        executeTagStrategy(data, strategy);
    }
}
