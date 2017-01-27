/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.formitem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test cases for {@link AbstractFormItem}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestAbstractFormItem {

    /** The VoiceXmlInterpreterContext to use. */
    private VoiceXmlInterpreterContext context;
    /** The document containing the field. */
    private VoiceXmlDocument document;
    /** The field for that field form item to test. */
    private Field field;
    /** The field form item to test. */
    private AbstractFormItem item;

    /**
     * Set up the test environment.
     * 
     * @throws Exception
     *             set up failed
     * @throws JVoiceXMLEvent
     *             set up failed
     * @since 0.7.6
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        // Create a test field
        document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang("en");
        final Form form = vxml.appendChild(Form.class);
        field = form.appendChild(Field.class);
        field.setName("testfield");

        // Create mock objects for the tests
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        item = new FieldFormItem(context, field);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getName()}.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals(field.getName(), item.getName());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#evaluateCondition()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testGetConditionNoCondition() throws JVoiceXMLEvent {
        Assert.assertTrue(item.evaluateCondition());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#evaluateCondition()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testGetConditionFalse() throws JVoiceXMLEvent {
        final DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(
                model.evaluateExpression(item.getName() + " == 'hallo'",
                        Boolean.class)).thenReturn(false);
        Mockito.when(context.getDataModel()).thenReturn(model);
        field.setCond(item.getName() + " == 'hallo'");
        Assert.assertFalse(item.evaluateCondition());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#evaluateCondition()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testGetConditionTrue() throws JVoiceXMLEvent {
        final DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(
                model.evaluateExpression(item.getName() + " == 'hallo'",
                        Boolean.class)).thenReturn(true);
        Mockito.when(context.getDataModel()).thenReturn(model);
        field.setCond(item.getName() + " == 'hallo'");
        Assert.assertTrue(item.evaluateCondition());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testIsSelectable() throws JVoiceXMLEvent {
        final DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(model.readVariable(item.getName(), Object.class))
                .thenReturn(null);
        Mockito.when(context.getDataModel()).thenReturn(model);
        Assert.assertTrue(item.isSelectable());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testIsSelectableVariableSet() throws JVoiceXMLEvent {
        final DataModel model = Mockito.mock(DataModel.class);
        Mockito.when(model.readVariable(item.getName(), Object.class))
                .thenReturn("somevalue");
        Mockito.when(model.toString("somevalue")).thenReturn("somevalue");
        Mockito.when(context.getDataModel()).thenReturn(model);
        Assert.assertFalse(item.isSelectable());
    }

}
