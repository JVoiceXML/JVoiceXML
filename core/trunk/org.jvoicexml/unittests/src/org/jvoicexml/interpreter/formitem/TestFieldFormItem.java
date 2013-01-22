/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Iterator;

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
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Option;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test cases for {@link FieldFormItem}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class TestFieldFormItem {
    /** The VoiceXmlInterpreterContext to use. */
    private VoiceXmlInterpreterContext context;
    /** The document containing the field. */
    private VoiceXmlDocument document;
    /** The field for that field form item to test. */
    private Field field;
    /** The field form item to test. */
    private FieldFormItem item;

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
        final OptionConverter converter = new SrgsXmlOptionConverter();
        item.setOptionConverter(converter);
    }

    /**
     * Test case for {@link FieldFormItem#getFormItemVariable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void setFormItemVariable() throws JVoiceXMLEvent {
        final String value = "test";
        item.setFormItemVariable(value);
        Assert.assertEquals(value, item.getFormItemVariable());
        final ScriptingEngine scripting = context.getScriptingEngine();
        final String name = item.getName();
        Assert.assertEquals(value, scripting.getVariable(name));
        Assert.assertEquals(value, scripting.eval(name + "$.utterance;"));
        scripting.setVariable(name, "hurz");
        Assert.assertEquals("hurz", item.getFormItemVariable());
    }

    /**
     * Test case for {@link FieldFormItem#getFormItemVariable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void setFormItemVariableRecognitionResult() throws JVoiceXMLEvent {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        result.setUtterance("hans");
        item.setFormItemVariable(result);
        Assert.assertEquals(result.getUtterance(), item.getFormItemVariable());
        final String name = item.getName();
        final ScriptingEngine scripting = context.getScriptingEngine();
        Assert.assertEquals(result.getUtterance(),
                scripting.eval(name + "$.utterance;"));
        Assert.assertEquals(result.getUtterance(), scripting.getVariable(name));
    }

    /**
     * Test case for {@link FieldFormItem#getFormItemVariable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void setFormItemVariableRecognitionResultSemanticInterpretation()
            throws JVoiceXMLEvent {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        result.setUtterance("yeah");
        result.setSemanticInterpretation("'yes'");
        item.setFormItemVariable(result);
        Assert.assertEquals(result.getSemanticInterpretation(),
                item.getFormItemVariable());
        final ScriptingEngine scripting = context.getScriptingEngine();
        final String name = item.getName();
        Assert.assertEquals(result.getSemanticInterpretation(),
                scripting.getVariable(name));
    }

    /**
     * Test case for {@link FieldFormItem#getFormItemVariable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void setFormItemVariableRecognitionResultSemanticInterpretationCompoundObject()
            throws JVoiceXMLEvent {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        result.setUtterance("yeah");
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.eval("var out = new Object();");
        scripting.eval("out.number = 3;");
        scripting.eval("out.size = 'large';");
        final ScriptableObject interpretation =
                (ScriptableObject) scripting.getVariable("out");
        result.setSemanticInterpretation(interpretation);
        field.setName("size");
        item.setFormItemVariable(result);
        final String name = item.getName();
        Assert.assertEquals("large", item.getFormItemVariable());
        Assert.assertEquals("large", scripting.getVariable(name));

        field.setSlot("pizza.topping");
        item.setFormItemVariable(result);
        Assert.assertNull(scripting.getVariable(name));
    }

    /**
     * Test case for {@link FieldFormItem#getFormItemVariable()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void setFormItemVariableRecognitionResultSemanticInterpretationCompoundObjectSlot()
            throws JVoiceXMLEvent {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setAccepted(true);
        result.setUtterance("yeah");
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.eval("var out = new Object();");
        scripting.eval("out.pizza = new Object();");
        scripting.eval("out.pizza.number = 3;");
        scripting.eval("out.pizza.size = 'large';");
        final ScriptableObject interpretation =
                (ScriptableObject) scripting.getVariable("out");
        result.setSemanticInterpretation(interpretation);
        field.setSlot("pizza.size");
        item.setFormItemVariable(result);
        final String name = item.getName();
        Assert.assertEquals("large", item.getFormItemVariable());
        Assert.assertEquals("large", scripting.getVariable(name));

        field.setSlot("pizza.topping");
        item.setFormItemVariable(result);
        Assert.assertNull(scripting.getVariable(name));
    }

    /**
     * Test case for {@link FieldFormItem#getGrammars()}.
     * @throws Exception
     *         test failed.
     * @since 0.7.1
     */
    @Test
    public void testGetGrammarsTypeBoolean() throws Exception {
        field.setName("lo_fat_meal");
        field.setType("boolean");
        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Do you want a low fat meal on this flight?");

        final Collection<Grammar> grammars = item.getGrammars();
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar dtmfGrammar = iterator.next();
        final Vxml vxml = document.getVxml();
        Assert.assertEquals("builtin:dtmf/boolean", dtmfGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), dtmfGrammar.getXmlLang());
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals("builtin:voice/boolean", voiceGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), voiceGrammar.getXmlLang());
    }

    /**
     * Test case for {@link FieldFormItem#getGrammars()}.
     * @throws Exception
     *         test failed.
     * @since 0.7.1
     */
    @Test
    public void testGetGrammarsTypeDigits() throws Exception {
        field.setName("numbers");
        field.setType("digits?minlength=3;maxlength=8");
        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Please enter a number");

        final Collection<Grammar> grammars = item.getGrammars();
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar dtmfGrammar = iterator.next();
        final Vxml vxml = document.getVxml();
        Assert.assertEquals("builtin:dtmf/digits?minlength=3;maxlength=8",
                dtmfGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), dtmfGrammar.getXmlLang());
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals("builtin:voice/digits?minlength=3;maxlength=8",
                voiceGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), voiceGrammar.getXmlLang());
    }

    /**
     * Test case for {@link FieldFormItem#getGrammars()}.
     * @throws Exception
     *         test failed.
     * @since 0.7.1
     */
    @Test
    public void testGetGrammarsWithOptions() throws Exception {
        field.setName("lo_fat_meal");
        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Do you want a low fat meal on this flight?");
        final Option option1 = field.appendChild(Option.class);
        option1.addText("yes");
        option1.setDtmf("1");
        final Option option2 = field.appendChild(Option.class);
        option2.addText("no");
        option2.setDtmf("2");
        final Collection<Grammar> grammars = item.getGrammars();
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        final Vxml vxml = document.getVxml();
        Assert.assertEquals(vxml.getXmlLang(), voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
    }

    /**
     * Test case for {@link FieldFormItem#getSlot()}.
     * @exception Exception
     *            test failed
     * @since 0.7.2
     */
    @Test
    public void testGetSlot() throws Exception {
        final VoiceXmlDocument document1 = new VoiceXmlDocument();
        final Vxml vxml1 = document1.getVxml();
        final Form form1 = vxml1.appendChild(Form.class);
        final Field field1 = form1.appendChild(Field.class);
        field1.setName("lo_fat_meal");
        final FieldFormItem item1 = new FieldFormItem(context, field1);
        Assert.assertEquals(field1.getName(), item1.getSlot());

        final VoiceXmlDocument document2 = new VoiceXmlDocument();
        final Vxml vxml2 = document2.getVxml();
        final Form form2 = vxml2.appendChild(Form.class);
        final Field field2 = form2.appendChild(Field.class);
        field2.setName("lo_fat_meal");
        field2.setSlot("meal");
        final FieldFormItem item2 = new FieldFormItem(context, field2);
        Assert.assertEquals(field2.getSlot(), item2.getSlot());
    }
}
