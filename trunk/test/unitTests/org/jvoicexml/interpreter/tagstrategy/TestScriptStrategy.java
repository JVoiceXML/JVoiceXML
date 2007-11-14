/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Script;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
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
    protected void setUp() throws Exception {
        super.setUp();

        final DocumentMap map = DocumentMap.getInstance();
        final JVoiceXmlDocumentServer server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());

        uri = map.getUri("/script");
        map.addDocument(uri, SCRIPT);

    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testExecute() throws JVoiceXMLEvent {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.addCdata(SCRIPT);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        assertEquals(42, scripting.getVariable("a"));
        assertEquals(24.0, scripting.eval("factorial(4)"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testExecuteSrc() throws JVoiceXMLEvent {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrc(uri);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final ScriptingEngine scripting = getScriptingEngine();
        assertEquals(42, scripting.getVariable("a"));
        assertEquals(24.0, scripting.eval("factorial(4)"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testExecuteSrcExpr() throws JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("test", "'" + uri.toString() + "'");
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrcexpr("test");

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        assertEquals(42, scripting.getVariable("a"));
        assertEquals(24.0, scripting.eval("factorial(4)"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    public void testExecuteNone() throws JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable("test", "'" + uri.toString() + "'");
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);

        final ScriptStrategy strategy = new ScriptStrategy();
        JVoiceXMLEvent error = null;
        try {
            executeTagStrategy(script, strategy);
        } catch (BadFetchError e) {
            error = e;
        }
        assertNotNull(error);
    }
}
