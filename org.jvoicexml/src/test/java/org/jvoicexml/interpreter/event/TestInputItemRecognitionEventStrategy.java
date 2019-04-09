/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test cases for {@link InputItemRecognitionEventStrategy}.
 * 
 * @author Markus Baumgart
 * @author Dirk Schnelle-Walkaa
 * @version $Revision$
 * @since 0.7.4
 */
public final class TestInputItemRecognitionEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /**
     * Set up the test environment.
     * 
     * @throws Exception
     *             set up failed
     */
    @Before
    public void setUp() throws Exception {
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(context.getDataModel()).thenReturn(model);
        interpreter = new VoiceXmlInterpreter(context);
    }

    /**
     * Test case for
     * {@link InputItemRecognitionEventStrategy#handleEvent(org.jvoicexml.interpreter.formitem.FieldFormItem, JVoiceXMLEvent)}
     * .
     * 
     * @throws Exception
     *             test failed
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testHandleEvent() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);

        final Field field = form.appendChild(Field.class);
        field.setName("field");

        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn("hello world");
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(0.55f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        Mockito.when(context.getProperty("confidencelevel", "0.5")).thenReturn(
                "0.5");

        final FieldFormItem formItem = new FieldFormItem(context, field);
        final InputItemRecognitionEventStrategy strategy = new InputItemRecognitionEventStrategy(
                context, interpreter, null, formItem);
        final JVoiceXMLEvent event = new RecognitionEvent(null, null, result);
        final boolean handled = strategy.handleEvent(formItem, event);
        Assert.assertTrue("event should have been handled", handled);
        Mockito.verify(context.getDataModel()).createVariable(
                "lastresult$.utterance", "hello world", Scope.APPLICATION);
    }

    /**
     * Test case for
     * {@link InputItemRecognitionEventStrategy#handleEvent(org.jvoicexml.interpreter.formitem.FieldFormItem, JVoiceXMLEvent)}
     * .
     * 
     * @throws Exception
     *             test failed
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test
    public void testHandleEventNotHandledNoMatch() throws Exception,
            JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);

        final Field field = form.appendChild(Field.class);
        field.setName("field");

        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn("hello world");
        Mockito.when(result.isAccepted()).thenReturn(false);
        Mockito.when(result.getConfidence()).thenReturn(0.55f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        Mockito.when(context.getProperty("confidencelevel", "0.5")).thenReturn(
                "0.5");

        final FieldFormItem formItem = new FieldFormItem(context, field);
        final InputItemRecognitionEventStrategy strategy = new InputItemRecognitionEventStrategy(
                context, interpreter, null, formItem);
        final JVoiceXMLEvent event = new RecognitionEvent(null, null, result);
        final boolean handled = strategy.handleEvent(formItem, event);
        Assert.assertFalse("event should not have been handled", handled);
        Mockito.verify(context.getDataModel()).createVariable(
                "lastresult$.utterance", "hello world", Scope.APPLICATION);
    }

    /**
     * Test case for
     * {@link InputItemRecognitionEventStrategy#handleEvent(org.jvoicexml.interpreter.formitem.FieldFormItem, JVoiceXMLEvent)}
     * .
     * 
     * @throws Exception
     *             test failed
     * @throws JVoiceXMLEvent
     *             test failed
     */
    @Test(expected = NomatchEvent.class)
    public void testHandleEventNotHandledBelowConfidence() throws Exception,
            JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);

        final Field field = form.appendChild(Field.class);
        field.setName("field");

        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn("hello world");
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(0.55f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        Mockito.when(context.getProperty("confidencelevel", "0.5")).thenReturn(
                "0.6");

        final FieldFormItem formItem = new FieldFormItem(context, field);
        final InputItemRecognitionEventStrategy strategy = new InputItemRecognitionEventStrategy(
                context, interpreter, null, formItem);
        final JVoiceXMLEvent event = new RecognitionEvent(null, null, result);
        strategy.handleEvent(formItem, event);
    }
}
