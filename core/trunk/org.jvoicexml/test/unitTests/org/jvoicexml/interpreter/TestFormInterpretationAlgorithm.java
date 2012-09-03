/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.CancelEvent;
import org.jvoicexml.event.plain.NoinputEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.tagstrategy.GrammarStrategy;
import org.jvoicexml.interpreter.tagstrategy.JVoiceXmlInitializationTagStrategyFactory;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.test.implementation.DummyUserInput;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link FormInterpretationAlgorithm}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */

public final class TestFormInterpretationAlgorithm {
    /** The VoiceXml interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXml interpreter. */
    private VoiceXmlInterpreter interpreter;

    /** The implementation platform. */
    private ImplementationPlatform platform;

    /**
     * Set up the test environment.
     * @exception Exception
     *            set up failed
     */
    @Before
    public void setUp() throws Exception {
        platform = new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
        interpreter = new VoiceXmlInterpreter(context);
        interpreter.init(configuration);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm#processGrammar(org.jvoicexml.xml.srgs.Grammar)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testProcessGrammar() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");

        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final GrammarDocument processed = fia.processGrammar(grammar);
        Assert.assertEquals(grammar.toString(),
                processed.getDocument().toString());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm#visitFieldFormItem(org.jvoicexml.interpreter.formitem.AbstractInputItem)}.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testVisitFieldFormItem() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);

        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final InputItem item = new FieldFormItem(context, field);
        fia.visitFieldFormItem(item);
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        Assert.assertNotNull(input);
        Assert.assertTrue(input.isRecognitionStarted());
    }

    /**
     * Test method to activate a grammar with a field scope.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testActivateFieldGrammars() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Grammar grammar = field.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent event = new CancelEvent();
        handler.notifyEvent(event);
    }

    /**
     * Test method to activate a grammar with a field scope with reentrance
     * after a noinput.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testReentrantActivateFieldGrammars()
            throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Grammar grammar = field.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
                (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent noinput = new NoinputEvent();
        handler.notifyEvent(noinput);
        input.waitRecognitionStarted();
        Collection<GrammarDocument> grammars = input.getActiveGrammars();
        for (GrammarDocument document : grammars) {
            System.out.println(document);
            System.out.println(document.hashCode());
        }
        Assert.assertEquals(1, activeGrammars.size());
        final JVoiceXMLEvent cancel = new CancelEvent();
        handler.notifyEvent(cancel);
    }

    /**
     * Test method to activate a grammar with a field scope with two fields.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testTwoFieldsActivateFieldGrammars()
            throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field1 = form.appendChild(Field.class);
        field1.setName("field1");
        final Grammar grammar1 = field1.appendChild(Grammar.class);
        grammar1.setVersion("1.0");
        grammar1.setType(GrammarType.SRGS_XML);
        final Rule rule1 = grammar1.appendChild(Rule.class);
        final OneOf oneof1 = rule1.appendChild(OneOf.class);
        final Item item11 = oneof1.appendChild(Item.class);
        item11.addText("visa");
        final Item item12 = oneof1.appendChild(Item.class);
        item12.addText("mastercard");
        final Item item13 = oneof1.appendChild(Item.class);
        item13.addText("american express");
        final Field field2 = form.appendChild(Field.class);
        field2.setName("field2");
        final Grammar grammar2 = field2.appendChild(Grammar.class);
        grammar2.setVersion("1.0");
        grammar2.setType(GrammarType.SRGS_XML);
        final Rule rule2 = grammar2.appendChild(Rule.class);
        final OneOf oneof2 = rule2.appendChild(OneOf.class);
        final Item item21 = oneof2.appendChild(Item.class);
        item21.addText("euro");
        final Item item22 = oneof2.appendChild(Item.class);
        item22.addText("dollar");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
                (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("visa");
        result.setAccepted(true);
        result.setConfidence(1.0f);
        final JVoiceXMLEvent recognitionEvent = new RecognitionEvent(result);
        handler.notifyEvent(recognitionEvent);
        input.waitRecognitionStarted();
        Assert.assertEquals(1, activeGrammars.size());
        final JVoiceXMLEvent cancel = new CancelEvent();
        handler.notifyEvent(cancel);
    }

    /**
     * Test method to activate a grammar with a form scope.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testActivateFormGrammars() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        form.appendChild(Field.class);
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent event = new CancelEvent();
        handler.notifyEvent(event);
    }

    /**
     * Test method to activate a grammar with a form scope with reentrance
     * after a noinput.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testReentrantActivateFormGrammars()
            throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        form.appendChild(Field.class);
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent noinput = new NoinputEvent();
        handler.notifyEvent(noinput);
        input.waitRecognitionStarted();
        Assert.assertEquals(1, activeGrammars.size());
        final JVoiceXMLEvent cancel = new CancelEvent();
        handler.notifyEvent(cancel);
    }

    /**
     * Test method to activate a grammar with a field scope and a form scope.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testActivateFormFieldGrammars() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final Field field = form.appendChild(Field.class);
        final Grammar fieldGrammar = field.appendChild(Grammar.class);
        fieldGrammar.setVersion("1.0");
        fieldGrammar.setType(GrammarType.SRGS_XML);
        final Rule fieldRule = fieldGrammar.appendChild(Rule.class);
        final OneOf fieldOneof = fieldRule.appendChild(OneOf.class);
        final Item fieldItem1 = fieldOneof.appendChild(Item.class);
        fieldItem1.addText("euro");
        final Item fieldItem2 = fieldOneof.appendChild(Item.class);
        fieldItem2.addText("dollar");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(2, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent event = new CancelEvent();
        handler.notifyEvent(event);
    }

    /**
     * Test method to activate a grammar with a field scope and a form scope
     * after a noinput
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testReentrantActivateFormFieldGrammars()
            throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final Field field = form.appendChild(Field.class);
        final Grammar fieldGrammar = field.appendChild(Grammar.class);
        fieldGrammar.setVersion("1.0");
        fieldGrammar.setType(GrammarType.SRGS_XML);
        final Rule fieldRule = fieldGrammar.appendChild(Rule.class);
        final OneOf fieldOneof = fieldRule.appendChild(OneOf.class);
        final Item fieldItem1 = fieldOneof.appendChild(Item.class);
        fieldItem1.addText("euro");
        final Item fieldItem2 = fieldOneof.appendChild(Item.class);
        fieldItem2.addText("dollar");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(2, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent noinput = new NoinputEvent();
        handler.notifyEvent(noinput);
        input.waitRecognitionStarted();
        Assert.assertEquals(2, activeGrammars.size());
        final JVoiceXMLEvent cancel = new CancelEvent();
        handler.notifyEvent(cancel);
    }

    /**
     * Test method to activate a grammar with a field scope and a form scope
     * with an initial tag.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testActivateInitialFieldGrammars()
            throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final Initial initial = form.appendChild(Initial.class);
        initial.setName("initial");
        final Field card = form.appendChild(Field.class);
        card.setName("cardfield");
        card.setSlot("card");
        final Field field = form.appendChild(Field.class);
        field.setName("field");
        final Grammar fieldGrammar = field.appendChild(Grammar.class);
        fieldGrammar.setVersion("1.0");
        fieldGrammar.setType(GrammarType.SRGS_XML);
        final Rule fieldRule = fieldGrammar.appendChild(Rule.class);
        final OneOf fieldOneof = fieldRule.appendChild(OneOf.class);
        final Item fieldItem1 = fieldOneof.appendChild(Item.class);
        fieldItem1.addText("euro");
        final Item fieldItem2 = fieldOneof.appendChild(Item.class);
        fieldItem2.addText("dollar");
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        final JVoiceXmlInitializationTagStrategyFactory factory =
                new JVoiceXmlInitializationTagStrategyFactory();
        final Map<String, TagStrategy> strategies =
                new java.util.HashMap<String, TagStrategy>();
        strategies.put(Grammar.TAG_NAME, new GrammarStrategy());
        factory.setTagStrategies(strategies);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    fia.initialize(factory);
                    fia.mainLoop();
                } catch (JVoiceXMLEvent e) {
                    Assert.fail(e.getMessage());
                }
            };
        };
        thread.start();
        final DummyUserInput input =
            (DummyUserInput) platform.getUserInput();
        input.waitRecognitionStarted();
        final Collection<GrammarDocument> activeGrammars =
                input.getActiveGrammars();
        Assert.assertEquals(1, activeGrammars.size());
        final EventHandler handler = context.getEventHandler();
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("visa");
        final ScriptingEngine scripting = new ScriptingEngine(null);
        scripting.enterScope(null, Scope.APPLICATION);
        scripting.eval("var out = new Object();");
        scripting.eval("out.card = 'visa';");
        final Object out = scripting.getVariable("out");
        result.setSemanticInterpretation(out);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        final JVoiceXMLEvent recognitionEvent = new RecognitionEvent(result);
        handler.notifyEvent(recognitionEvent);
        input.waitRecognitionStarted();
        Assert.assertEquals(2, activeGrammars.size());
        final JVoiceXMLEvent cancel = new CancelEvent();
        handler.notifyEvent(cancel);
    }

    /**
     * Test method for {@link FormInterpretationAlgorithm#setJustFilled(InputItem)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testSetJustFilled() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("name");
        final FieldFormItem item = new FieldFormItem(context, field);
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter,
                    executableForm);
        Assert.assertFalse(fia.isJustFilled(item));
        fia.setJustFilled(item);
        Assert.assertTrue(fia.isJustFilled(item));
    }
}
