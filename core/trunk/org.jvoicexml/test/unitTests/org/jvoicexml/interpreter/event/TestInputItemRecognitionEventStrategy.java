/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link InputItemRecognitionEventStrategy}.
 * 
 * @author Markus Baumgart
 * @author Dirk Schnelle-Walkaa
 * @version $Revision$
 * @since 0.7.4
 */
public class TestInputItemRecognitionEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;
    
    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = JVoiceXmlConfiguration.getInstance();
        context = new VoiceXmlInterpreterContext(session, configuration);
        interpreter = new VoiceXmlInterpreter(context, configuration);
    }

    /**
     * Test case for {@link InputItemRecognitionEventStrategy#handleEvent(org.jvoicexml.interpreter.formitem.FieldFormItem, JVoiceXMLEvent)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test
    public void testHandleEvent() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        
        final Field field = form.appendChild(Field.class);
        field.setName("field");
        
        context.setProperty("confidencelevel", "0.5");
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("hello world");
        result.setConfidence(0.55f);
        result.setAccepted(true);
        final ScriptingEngine scripting = context.getScriptingEngine();
        final FieldFormItem formItem = new FieldFormItem(context, field);
        formItem.init(scripting);
        final InputItemRecognitionEventStrategy strategy =
            new InputItemRecognitionEventStrategy(context, interpreter, null,
                    formItem);
        final JVoiceXMLEvent event = new RecognitionEvent(result);
        final boolean handled = strategy.handleEvent(formItem, event);
        Assert.assertTrue("event should be handled", handled);
        context.setProperty("confidencelevel", "0.6");
        final boolean handled2 = strategy.handleEvent(formItem, event);
        Assert.assertFalse("event should not be handled", handled2);
    }
}
