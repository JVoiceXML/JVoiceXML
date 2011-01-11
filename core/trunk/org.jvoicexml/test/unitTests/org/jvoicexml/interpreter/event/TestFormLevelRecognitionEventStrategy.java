/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test cases for {@link FormLevelRecognitionEventStrategy}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class TestFormLevelRecognitionEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /**
     * Prepares the testing environment.
     * @exception Exception
     *            error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
        interpreter = new VoiceXmlInterpreter(context);
        interpreter.init(configuration);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.FormLevelRecognitionEventStrategy#process(org.jvoicexml.event.JVoiceXMLEvent)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testProcess() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Initial initial = form.appendChild(Initial.class);
        initial.setName("start");
        final Field field1 = form.appendChild(Field.class);
        field1.setName("drink");
        field1.setSlot("order.drink");
        final Field field2 = form.appendChild(Field.class);
        field2.setName("food");
        field2.setSlot("order.food");
        final Dialog dialog = new ExecutablePlainForm(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter, dialog);
        final FormLevelRecognitionEventStrategy strategy =
            new FormLevelRecognitionEventStrategy(context, interpreter, fia,
                    dialog);
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        final String drink = "Cola";
        final String food = "Pizza";
        result.setUtterance(drink + " and " + food);

        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.eval("out = new Object(); out.order = new Object();"
                    + "out.order." + field1.getName() + "='" + drink + "';"
                    + "out.order." + field2.getName() + "='" + food + "';");
        final ScriptableObject interpretation = 
            (ScriptableObject) scripting.getVariable("out");
        result.setSemanticInterpretation(interpretation);
        final RecognitionEvent event = new RecognitionEvent(result);
        strategy.process(event);
        Assert.assertEquals(drink, scripting.getVariable(field1.getName()));
        Assert.assertEquals(food, scripting.getVariable(field2.getName()));
        Assert.assertEquals(Boolean.TRUE,
                scripting.getVariable(initial.getName()));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.FormLevelRecognitionEventStrategy#process(org.jvoicexml.event.JVoiceXMLEvent)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testProcessOneFilled() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Initial initial = form.appendChild(Initial.class);
        initial.setName("start");
        final Field field1 = form.appendChild(Field.class);
        field1.setName("drink");
        field1.setSlot("order.drink");
        final Field field2 = form.appendChild(Field.class);
        field2.setName("food");
        field2.setSlot("order.food");
        final Dialog dialog = new ExecutablePlainForm(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter, dialog);
        final FormLevelRecognitionEventStrategy strategy =
            new FormLevelRecognitionEventStrategy(context, interpreter, fia,
                    dialog);
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        final String food = "Pizza";
        result.setUtterance("I want " + food);

        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.eval("out = new Object(); out.order = new Object();"
                    + "out.order." + field2.getName() + "='" + food + "';");
        final ScriptableObject interpretation = 
            (ScriptableObject) scripting.getVariable("out");
        result.setSemanticInterpretation(interpretation);
        final RecognitionEvent event = new RecognitionEvent(result);
        strategy.process(event);
        Assert.assertNull(scripting.getVariable(field1.getName()));
        Assert.assertEquals(food, scripting.getVariable(field2.getName()));
        Assert.assertEquals(Boolean.TRUE,
                scripting.getVariable(initial.getName()));
    }
}
