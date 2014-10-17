/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestScriptStrategy.java $
 * Version: $LastChangedRevision: 4175 $
 * Date:    $Date: 2014-05-06 10:37:12 +0200 (Tue, 06 May 2014) $
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

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Application;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.JVoiceXmlApplication;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Script;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4175 $
 * @since 0.6
 */
public final class TestScriptStrategy
        extends TagStrategyTestBase {
    /** The demo script. */
    private static final String SCRIPT;

    static {
        SCRIPT = "var a = 42;"
            + "function factorial(n)"
            + "{"
            + "return (n <= 1)? 1 : n * factorial(n-1);"
            + "}";
    }

    /** The URI of the test script. */
    private URI uri;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final JVoiceXmlDocumentServer server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());
        final URL url = TestScriptStrategy.class.getResource("factorial.js");
        uri = url.toURI();
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecute() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.addCdata(SCRIPT);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        Assert.assertEquals(42, scripting.getVariable("a"));
        Assert.assertEquals(24.0, scripting.eval("factorial(4);"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecuteSrc() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrc(uri);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        Assert.assertEquals(42, scripting.getVariable("a"));
        Assert.assertEquals(24.0, scripting.eval("factorial(4);"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecuteRelativeSrc() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrc("org/jvoicexml/interpreter/tagstrategy/factorial.js");

        final ScriptStrategy strategy = new ScriptStrategy();
        final Application application = new JVoiceXmlApplication(null);
        final File file = new File("unittests/src/test.vxml");
        application.addDocument(file.toURI(), doc);
        getContext().setApplication(application);
        executeTagStrategy(script, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        Assert.assertEquals(42, scripting.getVariable("a"));
        Assert.assertEquals(24.0, scripting.eval("factorial(4);"));
    }
    
    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecuteSrcExpr() throws JVoiceXMLEvent, Exception {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("test", uri.toString());
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrcexpr("test");

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        Assert.assertEquals(42, scripting.getVariable("a"));
        Assert.assertEquals(24.0, scripting.eval("factorial(4);"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteSrcAndSrcExpr() throws JVoiceXMLEvent, Exception {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("test", uri.toString());
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrcexpr("test");
        script.setSrc(uri);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);
    }


    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     * @exception Exception
     *            test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNone() throws JVoiceXMLEvent, Exception {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("test", "'" + uri.toString() + "'");
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);
    }
}
