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
package org.jvoicexml.interpreter;

import java.net.URI;
import java.util.Map;

import junit.framework.TestCase;

import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Param;
import org.jvoicexml.xml.vxml.ParamValueType;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.ParamParser}.
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
public final class TestParamParser
        extends TestCase {
    /** The scripting engine to use. */
    private ScriptingEngine scripting;

    /** Mapped document repository. */
    private DocumentMap map;

    /** The server object to test. */
    private JVoiceXmlDocumentServer server;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        scripting = new ScriptingEngine(null);

        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    public void testGetParameters() throws Exception, JVoiceXMLEvent {
        String test = "actor";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);

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
        param3.setValue(uri.toString());
        param3.setValuetype(ParamValueType.REF);
        param3.setType("text/plain");

        final ParamParser parser = new ParamParser(object, scripting, server);
        final Map<String, Object> params = parser.getParameters();
        assertEquals("Horst", params.get("firstname"));
        assertEquals("Buchholz", params.get("lastname"));
        assertEquals(test, params.get("job"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
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

        final ParamParser parser = new ParamParser(object, scripting, server);
        BadFetchError error = null;
        try {
            parser.getParameters();
        } catch (BadFetchError e) {
            error = e;
        }
        assertNotNull("ParamParser should have thrown an error.badfetch",
                error);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ParamParser#getParameters()}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
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

        final ParamParser parser = new ParamParser(object, scripting, server);
        BadFetchError error = null;
        try {
            parser.getParameters();
        } catch (BadFetchError e) {
            error = e;
        }
        assertNotNull("ParamParser should have thrown an error.badfetch",
                error);
    }
}
