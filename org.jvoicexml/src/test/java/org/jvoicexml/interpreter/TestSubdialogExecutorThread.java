/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.ReturnEvent;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;
import org.jvoicexml.mock.MockJvoiceXmlCore;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Return;
import org.jvoicexml.xml.vxml.Subdialog;
import org.jvoicexml.xml.vxml.Var;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test case for {@link org.jvoicexml.interpreter.SubdialogExecutorThread}.
 * 
 * @author Dirk Schnelle-Walka
 */
@Ignore("Unmaintained test is currently failing. TODO: Fix or delete")
public class TestSubdialogExecutorThread {

    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /**
     * Set up the test environment.
     * 
     * @exception Exception
     *                set up failed
     */
    @Before
    public void setUp() throws Exception {
        final MockJvoiceXmlCore jvxml = new MockJvoiceXmlCore();
        final ImplementationPlatform platform = new MockImplementationPlatform();
        final Profile profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);

        final SessionIdentifier id = new UuidSessionIdentifier();
        final JVoiceXmlSession session = new JVoiceXmlSession(platform, jvxml,
                null, profile, id);
        final Configuration configuration = Mockito.mock(Configuration.class);
        context = new VoiceXmlInterpreterContext(session, configuration);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.ObjectExecutorThread#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.ObjectFormItem)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                Test failed.
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Subdialog subdialog = form.appendChild(Subdialog.class);
        subdialog.setName("test");
        final Form subform = vxml.appendChild(Form.class);
        subform.setId("subid");
        final Var var = subform.appendChild(Var.class);
        var.setName("testparam");
        final Block block = subform.appendChild(Block.class);
        final Assign assign = block.appendChild(Assign.class);
        assign.setName("testparam");
        assign.setExpr("testparam * 2");
        final Return ret = block.appendChild(Return.class);
        ret.setNamelist("testparam");
        final SubdialogFormItem item = new SubdialogFormItem(context, subdialog);
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, null, dialog);
        final EventHandler handler = new org.jvoicexml.interpreter.event.JVoiceXmlEventHandler(
                null, null);
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe("", handler);
        handler.collect(context, null, fia, item);

        final URI uri = new URI("#subid");
        final Map<String, Object> params = new java.util.HashMap<String, Object>();
        params.put("testparam", new Integer(4));
        final JVoiceXmlApplication application = new JVoiceXmlApplication(null);
        application.addDocument(new URI("test"), doc);

        final SubdialogExecutorThread executor = new SubdialogExecutorThread(
                uri, context, application, params, eventbus, null);

        executor.start();
        executor.join();
        ReturnEvent event = null;
        try {
            handler.processEvent(item);
        } catch (ReturnEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        final Map<String, Object> variables = event.getVariables();
        Assert.assertEquals(1, variables.size());
        Assert.assertEquals(8, variables.get("testparam"));
    }
}
