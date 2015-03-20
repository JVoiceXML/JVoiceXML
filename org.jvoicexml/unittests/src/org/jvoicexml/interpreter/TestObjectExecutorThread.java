/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.interpreter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Session;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Param;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test case for {@link org.jvoicexml.interpreter.ObjectExecutorThread}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestObjectExecutorThread implements EventSubscriber {
    /** The received event. */
    private JVoiceXMLEvent event;

    /** Synchronization lock. */
    private Object lock;

    /** Test return value. */
    private static final String STRING_VALUE = "dummy value";

    /** Test return value. */
    private static final Long LONG_VALUE = new Long(42);

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
        lock = new Object();
        event = null;

        // Create mock objects for the tests
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        final EventBus bus = new EventBus();
        Mockito.when(context.getEventBus()).thenReturn(bus);
        final Session session = Mockito.mock(Session.class);
        Mockito.when(context.getSession()).thenReturn(session);
    }

    /**
     * Test method to call.
     * 
     * @return dummy result.
     */
    public String invoke() {
        return STRING_VALUE;
    }

    /**
     * Other test method to call.
     * 
     * @return dummy result.
     */
    public Long anotherMethod() {
        return LONG_VALUE;
    }

    /**
     * Other test method to call.
     * 
     * @param value
     *            test argument.
     * @return dummy result.
     */
    public int increment(final Integer value) {
        return value + 1;
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
    @Test(timeout = 2000)
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class);
        final ObjectFormItem item = new ObjectFormItem(context, object);
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe("", this);

        final ObjectExecutorThread executor = new ObjectExecutorThread(context,
                item);

        executor.start();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertNotNull("no event received", event);
        Assert.assertTrue("expected an object result",
                event instanceof ObjectTagResultEvent);
        final ObjectTagResultEvent result = (ObjectTagResultEvent) event;
        Assert.assertEquals(STRING_VALUE, result.getInputResult());
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
    @Test(timeout = 2000)
    public void testExecuteMethodName() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class, "anotherMethod");

        final ObjectFormItem item = new ObjectFormItem(context, object);
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe("", this);

        final ObjectExecutorThread executor = new ObjectExecutorThread(context,
                item);

        executor.start();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertNotNull("no event received", event);
        Assert.assertTrue("expected an object result",
                event instanceof ObjectTagResultEvent);
        final ObjectTagResultEvent result = (ObjectTagResultEvent) event;
        Assert.assertEquals(LONG_VALUE, result.getInputResult());
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
    @Test(timeout = 2000)
    public void testExecuteParam() throws Exception, JVoiceXMLEvent {
        final DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(model.evaluateExpression("testvalue", Object.class))
                .thenReturn(new Integer(1));
        Mockito.when(context.getDataModel()).thenReturn(model);

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
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe("", this);

        final ObjectExecutorThread executor = new ObjectExecutorThread(context,
                item);

        executor.start();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertNotNull("no event received", event);
        Assert.assertTrue("expected an object result",
                event instanceof ObjectTagResultEvent);
        final ObjectTagResultEvent result = (ObjectTagResultEvent) event;
        Assert.assertEquals(2, result.getInputResult());
    }

    @Override
    public void onEvent(final JVoiceXMLEvent e) {
        synchronized (lock) {
            event = e;
            lock.notifyAll();
        }
    }

}
