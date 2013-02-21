/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/TestParamParser.java $
 * Version: $LastChangedRevision: 2830 $
 * Date:    $Date: 2011-09-23 06:04:56 -0500 (vie, 23 sep 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Param;
import org.jvoicexml.xml.vxml.ParamValueType;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.ParamParser}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2830 $
 * @since 0.6
 */
public final class TestParamParser {
    /** The scripting engine to use. */
    private ScriptingEngine scripting;

    /** Mapped document repository. */
    private DocumentMap map;

    /** The server object to test. */
    private JVoiceXmlDocumentServer server;

    /** the current session id. */
    private Session session;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        scripting = new ScriptingEngine(null);

        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());

        final ImplementationPlatform platform =
                new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        session = new JVoiceXmlSession(platform, jvxml, null);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetParameters() throws Exception, JVoiceXMLEvent {
        String test = "actor";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);

        scripting.setVariable("last", "Buchholz");

        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        final Param param1 = object.appendChild(Param.class);
        param1.setName("firstname");
        param1.setValue("Horst");
        final Param param2 = object.appendChild(Param.class);
        param2.setName("lastname");
        param2.setExpr("last");
        final Param param3 = object.appendChild(Param.class);
        param3.setName("job");
        param3.setValue(uri.toString());
        param3.setValuetype(ParamValueType.REF);
        param3.setType("text/plain");

        final ParamParser parser = new ParamParser(object, scripting, server,
                session);
        final Map<String, Object> params = parser.getParameters();
        Assert.assertEquals("Horst", params.get("firstname"));
        Assert.assertEquals("Buchholz", params.get("lastname"));
        Assert.assertEquals(test, params.get("job"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetParametersErrorBadFetch()
        throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        final Param param1 = object.appendChild(Param.class);
        param1.setName("firstname");
        param1.setValue("Horst");
        object.appendChild(Param.class);

        final ParamParser parser = new ParamParser(object, scripting, server,
                session);
        BadFetchError error = null;
        try {
            parser.getParameters();
        } catch (BadFetchError e) {
            error = e;
        }
        Assert.assertNotNull("ParamParser should have thrown an error.badfetch",
                error);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetParametersInvalidUri()
        throws Exception, JVoiceXMLEvent {
        scripting.setVariable("last", "'Buchholz'");

        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        final Param param1 = object.appendChild(Param.class);
        param1.setName("firstname");
        param1.setValue("Horst");
        final Param param2 = object.appendChild(Param.class);
        param2.setName("lastname");
        param2.setExpr("last");
        final Param param3 = object.appendChild(Param.class);
        param3.setName("job");
        param3.setValue("%invaliduri%");
        param3.setValuetype(ParamValueType.REF);
        param3.setType("text/plain");

        final ParamParser parser = new ParamParser(object, scripting, server,
                session);
        BadFetchError error = null;
        try {
            parser.getParameters();
        } catch (BadFetchError e) {
            error = e;
        }
        Assert.assertNotNull("ParamParser should have thrown an error.badfetch",
                error);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameterValues()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetParameterValues() throws Exception, JVoiceXMLEvent {
        String test = "actor";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);

        scripting.setVariable("last", "Buchholz");

        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        final Param param1 = object.appendChild(Param.class);
        param1.setName("firstname");
        param1.setValue("Horst");
        final Param param2 = object.appendChild(Param.class);
        param2.setName("lastname");
        param2.setExpr("last");
        final Param param3 = object.appendChild(Param.class);
        param3.setName("job");
        param3.setValue(uri.toString());
        param3.setValuetype(ParamValueType.REF);
        param3.setType("text/plain");

        final ParamParser parser = new ParamParser(object, scripting, server,
                session);
        final Collection<Object> params = parser.getParameterValues();
        Assert.assertEquals(3, params.size());
        final Iterator<Object> iterator = params.iterator();
        Assert.assertEquals("Horst", iterator.next());
        Assert.assertEquals("Buchholz", iterator.next());
        Assert.assertEquals(test, iterator.next());
    }
}
