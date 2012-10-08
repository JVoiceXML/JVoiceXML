/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link AbstractFormItem}.
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
     * @throws Exception
     *         set up failed
     * @throws JVoiceXMLEvent 
     *         set up failed
     * @since 0.7.6
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang("en");
        final Form form = vxml.appendChild(Form.class);
        field = form.appendChild(Field.class);
        field.setName("testfield");
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final ImplementationPlatform platform =
                new DummyImplementationPlatform();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
        item = new FieldFormItem(context, field);
        final ScriptingEngine scripting = context.getScriptingEngine();
        item.init(scripting);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getName()}.
     */
    @Test
    public void testGetName() {
        Assert.assertEquals(field.getName(), item.getName());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getCondition()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetConditionNoCondition() throws JVoiceXMLEvent {
        Assert.assertTrue(item.getCondition());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getCondition()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetConditionFalseNumbers() throws JVoiceXMLEvent {
        field.setCond("1 == 0");
        Assert.assertFalse(item.getCondition());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getCondition()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetConditionFalseStrings() throws JVoiceXMLEvent {
        field.setCond(item.getName() + " == 'hallo'");
        Assert.assertFalse(item.getCondition());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#getCondition()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetConditionTrueStrings() throws JVoiceXMLEvent {
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(field.getName(), "hallo");
        field.setCond(item.getName() + " == 'hallo'");
        Assert.assertTrue(item.getCondition());
    }
    
    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testIsSelectable() throws JVoiceXMLEvent {
        Assert.assertTrue(item.isSelectable());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testIsSelectableFalseNumbers() throws JVoiceXMLEvent {
        field.setCond("1 == 0");
        Assert.assertFalse(item.isSelectable());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testIsSelectableFalseString() throws JVoiceXMLEvent {
        field.setCond(item.getName() + " == 'hallo'");
        Assert.assertFalse(item.isSelectable());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.AbstractFormItem#isSelectable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testIsSelectableTrueString() throws JVoiceXMLEvent {
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(field.getName() + "test", "hallo");
        field.setCond(item.getName() + "test" + " == 'hallo'");
        Assert.assertTrue(item.isSelectable());
    }
}
