/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Param;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.ObjectExecutorThread}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public final class TestObjectExecutorThread {
    /**
     * Test return value.
     */
    private static final String STRING_VALUE = "dummy value";

    /**
     * Test return value.
     */
    private static final Long LONG_VALUE = new Long(42);

    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /**
     * Set up the test environment
     * @exception Exception
     *            set up failed
     */
    @Before
    public void setUp() throws Exception {
        final DummyJvoiceXmlCore jvxml = new DummyJvoiceXmlCore();

        final JVoiceXmlSession session =
            new JVoiceXmlSession(null, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
    }

    /**
     * Test method to call.
     * @return dummy result.
     */
    public String invoke() {
        return STRING_VALUE;
    }

    /**
     * Other test method to call.
     * @return dummy result.
     */
    public Long anotherMethod() {
        return LONG_VALUE;
    }

    /**
     * Other test method to call.
     * @param value test argument.
     * @return dummy result.
     */
    public int increment(final Integer value) {
        return value + 1;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ObjectExecutorThread#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.ObjectFormItem)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecute()throws Exception, JVoiceXMLEvent  {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class);
        final ObjectFormItem item = new ObjectFormItem(context, object);
        final Dialog dialog = new ExecutablePlainForm(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, null, dialog);
        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(null);
        handler.collect(context, null, fia, item);

        final ObjectExecutorThread executor =
            new ObjectExecutorThread(context, item, handler);

        executor.start();
        executor.join();
        final ScriptingEngine scripting = context.getScriptingEngine();
        handler.processEvent(item);
        Assert.assertEquals(STRING_VALUE, scripting.getVariable("test"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ObjectExecutorThread#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.ObjectFormItem)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecuteMethodName()throws Exception, JVoiceXMLEvent  {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class, "anotherMethod");

        final ObjectFormItem item = new ObjectFormItem(context, object);
        final Dialog dialog = new ExecutablePlainForm(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, null, dialog);

        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(null);
        handler.collect(context, null, fia, item);

        final ObjectExecutorThread executor =
            new ObjectExecutorThread(context, item, handler);

        executor.start();
        executor.join();
        final ScriptingEngine scripting = context.getScriptingEngine();
        handler.processEvent(item);
        Assert.assertEquals(LONG_VALUE, scripting.getVariable("test"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ObjectExecutorThread#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.ObjectFormItem)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecuteParam()throws Exception, JVoiceXMLEvent  {
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable("testvalue", new Integer(1));

        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class, "increment");
        final Param param = object.appendChild(Param.class);
        param.setName("value");
        param.setExpr("testvalue");

        final ObjectFormItem item = new ObjectFormItem(context, object);
        final Dialog dialog = new ExecutablePlainForm(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, null, dialog);

        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler(null);
        handler.collect(context, null, fia, item);

        final ObjectExecutorThread executor =
            new ObjectExecutorThread(context, item, handler);

        executor.start();
        executor.join();
        handler.processEvent(item);
        Assert.assertEquals(2, scripting.getVariable("test"));
    }
}
