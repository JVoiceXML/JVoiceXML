/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.Application;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.documentserver.schemestrategy.ResourceDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.JVoiceXmlApplication;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.vxml.Script;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test case for {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class TestScriptStrategy extends TagStrategyTestBase {
    /** The demo script. */
    private static final String SCRIPT;

    static {
        SCRIPT = "var a = 42;" + "function factorial(n)" + "{"
                + "return (n <= 1)? 1 : n * factorial(n-1);" + "}";
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
        server.addSchemeStrategy(new ResourceDocumentStrategy());
        uri = new URI("res://factorial.js");
        final VoiceXmlInterpreterContext context = getContext();
        Mockito.when(context.getDocumentServer()).thenReturn(server);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecute() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.addCdata(SCRIPT);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final DataModel model = getDataModel();
        Mockito.verify(model).evaluateExpression(SCRIPT, Object.class);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteSrc() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrc(uri);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final DataModel model = getDataModel();
        final String externalScript = readResource("/factorial.js");
        Mockito.verify(model).evaluateExpression(externalScript, Object.class);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteRelativeSrc() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final URI uri = new URI("res://root.vxml");
        vxml.setApplication(uri);
        final URI base = new URI("res:///");
        vxml.setXmlBase(base);
        final Script script = vxml.appendChild(Script.class);
        script.setSrc("factorial.js");

        final ScriptStrategy strategy = new ScriptStrategy();
        final Application application = new JVoiceXmlApplication(null);
        application.addDocument(uri, doc);
        Mockito.when(getContext().getApplication()).thenReturn(application);
        executeTagStrategy(script, strategy);

        final DataModel model = getDataModel();
        final String externalScript = readResource("/factorial.js");
        Mockito.verify(model).evaluateExpression(externalScript, Object.class);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test
    public void testExecuteSrcExpr() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrcexpr("test");

        final DataModel model = getDataModel();
        Mockito.when(
                model.evaluateExpression(script.getSrcexpr(), Object.class))
                .thenReturn(uri.toString());
        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);

        final String externalScript = readResource("/factorial.js");
        Mockito.verify(model).evaluateExpression(externalScript, Object.class);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteSrcAndSrcExpr() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);
        script.setSrcexpr("test");
        script.setSrc(uri);

        final DataModel model = getDataModel();
        Mockito.when(
                model.evaluateExpression(script.getSrcexpr(), Object.class))
                .thenReturn(uri.toString());

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.ScriptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @throws JVoiceXMLEvent
     *             Test failed.
     * @exception Exception
     *                test failed
     */
    @Test(expected = BadFetchError.class)
    public void testExecuteNone() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Script script = vxml.appendChild(Script.class);

        final ScriptStrategy strategy = new ScriptStrategy();
        executeTagStrategy(script, strategy);
    }
}
