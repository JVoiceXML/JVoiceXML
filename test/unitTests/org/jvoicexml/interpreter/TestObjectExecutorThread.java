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

import junit.framework.TestCase;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.event.ObjectTagEventStrategy;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link org.jvoicexml.interpreter.ObjectExecutorThread}.
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
public final class TestObjectExecutorThread
        extends TestCase {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        final DummyJvoiceXmlCore jvxml = new DummyJvoiceXmlCore();

        final JVoiceXmlSession session = new JVoiceXmlSession(null, jvxml);
        context = new VoiceXmlInterpreterContext(session);
    }

    /**
     * Test method to call.
     * @return dummy result.
     */
    public String invoke() {
        return "dummy value";
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ObjectExecutorThread#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.ObjectFormItem)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    public void testExecute()throws Exception, JVoiceXMLEvent  {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        object.setName("test");
        object.setClassid(TestObjectExecutorThread.class);

        final ObjectFormItem item = new ObjectFormItem(context, object);


        final EventHandler handler = new org.jvoicexml.interpreter.event.
            JVoiceXmlEventHandler();
        // We need at least a handler to process the recognition result.
        final ObjectTagEventStrategy event = new ObjectTagEventStrategy(
                context, null, null, item);

        handler.addStrategy(event);
        final ObjectExecutorThread executor =
            new ObjectExecutorThread(context, item, handler);

        executor.run();
        final ScriptingEngine scripting = context.getScriptingEngine();
        handler.processEvent(item);
        assertEquals("dummy value", scripting.getVariable("test"));
    }

}
