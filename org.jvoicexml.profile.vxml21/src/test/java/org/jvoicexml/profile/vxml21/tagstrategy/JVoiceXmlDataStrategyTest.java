/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.ResourceDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.ccxml.Var;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.JVoiceXmlData;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test cases for {@link JVoiceXmlDataStrategy}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlDataStrategyTest extends TagStrategyTestBase {
    /** The document server. */
    private JVoiceXmlDocumentServer server;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new ResourceDocumentStrategy());
        final VoiceXmlInterpreterContext context = getContext();
        Mockito.when(context.getDocumentServer()).thenReturn(server);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.profile.vxml21.tagstrategy.JVoiceXmlDataStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * 
     * @throws Exception
     *             test failed
     * @throws JVoiceXMLEvent
     *             test failes
     */
    @Test
    public void testExecute() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument doc = createDocument();
        final Vxml vxml = doc.getVxml();
        final Var var = vxml.appendChild(Var.class);
        final String name = "quote";
        var.setName(name);
        final Block block = createBlock(doc);
        final JVoiceXmlData data = block.appendChild(JVoiceXmlData.class);
        data.setSrc("res://address.json");
        data.setName(name);
        data.setType(DocumentDescriptor.MIME_TYPE_JSON);

        final DataModel model = getDataModel();
        Mockito.when(model.existsVariable(name)).thenReturn(Boolean.TRUE);

        final JVoiceXmlDataStrategy strategy = new JVoiceXmlDataStrategy();
        executeTagStrategy(data, strategy);

        final String json = readResource("/address.json");
        Mockito.verify(model).updateVariable(Mockito.eq(name),
                Mockito.eq(json));
    }

}
