/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.mock.MockRecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test cases for {@link FormLevelRecognitionEventStrategy}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class TestFormLevelRecognitionEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The employed data model. */
    private DataModel model;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /**
     * Prepares the testing environment.
     * 
     * @exception Exception
     *                error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        model = Mockito.mock(DataModel.class);
        Mockito.when(context.getDataModel()).thenReturn(model);
        interpreter = new VoiceXmlInterpreter(context);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.FormLevelRecognitionEventStrategy#process(org.jvoicexml.event.JVoiceXMLEvent)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @exception JVoiceXMLEvent
     *                test failed
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
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        final FormLevelRecognitionEventStrategy strategy = new FormLevelRecognitionEventStrategy(
                context, interpreter, fia, dialog);

        final String drink = "Cola";
        final String food = "Pizza";
        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn(drink + " and " + food);
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(0.55f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);
        Mockito.when(result.getSemanticInterpretation(context.getDataModel()))
                .thenReturn(drink + " and " + food);
        Mockito.when(
                model.readVariable("application.lastresult$.interpretation."
                        + field1.getSlot(), Object.class)).thenReturn(drink);
        Mockito.when(
                model.readVariable("application.lastresult$.interpretation."
                        + field2.getSlot(), Object.class)).thenReturn(food);

        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        strategy.process(event);
        Mockito.verify(model).updateVariable(field1.getName(), drink);
        Mockito.verify(model).updateVariable(field2.getName(), food);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.FormLevelRecognitionEventStrategy#process(org.jvoicexml.event.JVoiceXMLEvent)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @exception JVoiceXMLEvent
     *                test failed
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
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        final FormLevelRecognitionEventStrategy strategy = new FormLevelRecognitionEventStrategy(
                context, interpreter, fia, dialog);
        final MockRecognitionResult result = new MockRecognitionResult();
        result.setAccepted(true);
        final String food = "Pizza";
        result.setUtterance("I want " + food);

        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.eval("var out = new Object(); out.order = new Object();"
                + "out.order." + field2.getName() + "='" + food + "';");
        final ScriptableObject interpretation = (ScriptableObject) scripting
                .getVariable("out");
        result.setSemanticInterpretation(interpretation);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        strategy.process(event);
        Assert.assertNull(scripting.getVariable(field1.getName()));
        Assert.assertEquals(food, scripting.getVariable(field2.getName()));
        Assert.assertEquals(Boolean.TRUE,
                scripting.getVariable(initial.getName()));
    }
}
