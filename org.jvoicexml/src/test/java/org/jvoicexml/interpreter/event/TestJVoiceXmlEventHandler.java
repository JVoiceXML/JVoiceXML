/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import static org.mockito.Mockito.never;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.GenericVoiceXmlEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.CancelEvent;
import org.jvoicexml.event.plain.HelpEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.grammar.InternalGrammarDocument;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.mock.MockRecognitionResult;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.Catch;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.FilledMode;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Help;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.Log;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.Nomatch;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

/**
 * Test cases for {@link JVoiceXmlEventHandler}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class TestJVoiceXmlEventHandler {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The employed data model. */
    private DataModel model;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /** The profile. */
    private Profile profile;

    /** The event bus to use. */
    private EventBus eventbus;
    
    /**
     * Sets up the test environment.
     * 
     * @throws java.lang.Exception
     *             setup failed.
     */
    @Before
    public void setUp() throws Exception {
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        model = Mockito.mock(DataModel.class);
        Mockito.when(context.getDataModel()).thenReturn(model);
        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(context.getConfiguration()).thenReturn(configuration);
        Mockito.when(context.getProperty("confidencelevel", "0.5")).thenReturn("0.5");

        interpreter = new VoiceXmlInterpreter(context);
        profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        final TagStrategyFactory tagStrategyFactory = Mockito.mock(TagStrategyFactory.class);
        Mockito.when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);
        Mockito.when(profile.getInitializationTagStrategyFactory()).thenReturn(
                tagStrategyFactory);
        Mockito.when(profile.getTagStrategyFactory()).thenReturn(
                tagStrategyFactory);
        eventbus = new EventBus();
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.Dialog)}
     * .
     * <p>
     * Test for dialog level catches.
     * </p>
     * 
     * @exception Exception
     *                test failed.
     */
    @Test
    public void testCollectDialog() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        form.appendChild(Field.class);
        form.appendChild(Filled.class);
        form.appendChild(Noinput.class);
        form.appendChild(Help.class);
        final Catch catchNode = form.appendChild(Catch.class);
        catchNode.setEvent("test");

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                null, eventbus);
        final int numDefaultHandlers = handler.getStrategies().size();
        handler.collect(context, interpreter, dialog);

        final Collection<EventStrategy> strategies = handler.getStrategies();
        Assert.assertEquals(numDefaultHandlers + 3, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.Dialog)}
     * .
     * <p>
     * Test for dialog level catches.
     * </p>
     * 
     * @exception Exception
     *                test failed.
     */
    @Test
    public void testCollectDefault() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final FieldFormItem item = new FieldFormItem(context, field);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                null, eventbus);
        final int numDefaultHandlers = handler.getStrategies().size();
        handler.collect(null, null, null, item);

        final Collection<EventStrategy> strategies = handler.getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type nomatch",
                containsType(strategies, "nomatch"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategies, "cancel"));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     */
    @Test
    public void testCollect() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.appendChild(Filled.class);
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final FieldFormItem item = new FieldFormItem(context, field);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                null, eventbus);
        final int numDefaultHandlers = handler.getStrategies().size();
        handler.collect(context, interpreter, null, item);

        final Collection<EventStrategy> strategies = handler.getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategies, "cancel"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategies, RecognitionEvent.EVENT_TYPE + "."
                        + RecognitionEvent.DETAIL));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * <p>
     * Test for dialog changes.
     * </p>
     * 
     * @exception Exception
     *                test failed.
     */
    @Test
    public void testCollect2FieldsContextChange() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field1 = form.appendChild(Field.class);
        field1.appendChild(Filled.class);
        field1.appendChild(Noinput.class);
        field1.appendChild(Help.class);
        final Catch catchNode1 = field1.appendChild(Catch.class);
        catchNode1.setEvent("test");
        final Field field2 = form.appendChild(Field.class);
        field2.appendChild(Filled.class);
        field2.appendChild(Noinput.class);
        field2.appendChild(Help.class);
        final Catch catchNode2 = field2.appendChild(Catch.class);
        catchNode2.setEvent("test2");

        final ScopeObserver observer = new ScopeObserver();
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                observer, eventbus);
        final int numDefaultHandlers = handler.getStrategies().size();
        observer.enterScope(Scope.DIALOG);

        final FieldFormItem item1 = new FieldFormItem(context, field1);
        handler.collect(context, interpreter, null, item1);

        final Collection<EventStrategy> strategies = handler.getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategies, "cancel"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategies, RecognitionEvent.EVENT_TYPE + "."
                        + RecognitionEvent.DETAIL));

        observer.exitScope(Scope.DIALOG);
        final Collection<EventStrategy> strategiesLeave1 = handler
                .getStrategies();
        Assert.assertEquals(numDefaultHandlers, strategiesLeave1.size());

        observer.enterScope(Scope.DIALOG);
        final FieldFormItem item2 = new FieldFormItem(context, field2);
        handler.collect(null, null, null, item2);

        final Collection<EventStrategy> strategiesEnter2 = handler
                .getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategiesEnter2.size());
        Assert.assertTrue("expected to find type test2",
                containsType(strategiesEnter2, "test2"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategiesEnter2, "noinput"));
        Assert.assertTrue("expected to find type help",
                containsType(strategiesEnter2, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategiesEnter2, "cancel"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategiesEnter2, RecognitionEvent.EVENT_TYPE + "."
                        + RecognitionEvent.DETAIL));
        observer.exitScope(Scope.DIALOG);
        final Collection<EventStrategy> strategiesLeave2 = handler
                .getStrategies();
        Assert.assertEquals(numDefaultHandlers, strategiesLeave2.size());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     */
    @Test
    public void testCollectFieldReenter() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.appendChild(Filled.class);
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final FieldFormItem item = new FieldFormItem(context, field);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                null, eventbus);
        final int numDefaultHandlers = handler.getStrategies().size();
        handler.collect(context, interpreter, null, item);

        final Collection<EventStrategy> strategies = handler.getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type nomatch",
                containsType(strategies, "nomatch"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategies, "cancel"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategies, RecognitionEvent.EVENT_TYPE + "."
                        + RecognitionEvent.DETAIL));

        // Second run
        handler.collect(context, interpreter, null, item);
        final Collection<EventStrategy> strategiesSecond = handler
                .getStrategies();
        Assert.assertEquals(numDefaultHandlers + 6, strategiesSecond.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategiesSecond, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategiesSecond, "noinput"));
        Assert.assertTrue("expected to find type nomatch",
                containsType(strategiesSecond, "nomatch"));
        Assert.assertTrue("expected to find type help",
                containsType(strategiesSecond, "help"));
        Assert.assertTrue("expected to find type cancel",
                containsType(strategiesSecond, "cancel"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategiesSecond, RecognitionEvent.EVENT_TYPE + "."
                        + RecognitionEvent.DETAIL));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFieldLevelFilled() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final String name = "testfield1";
        field.setName(name);
        final FieldFormItem item = new FieldFormItem(context, field);
        addInputRule(item, field, "this is a field level test");
        final Filled filled = field.appendChild(Filled.class);
        final Log log = filled.appendChild(Log.class);
        log.setExpr("'test: ' + " + name);
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, fia, item);

        Mockito.when(context.getProperty("confidencelevel", "0.5")).thenReturn(
                "0.5");

        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        final String utterance = "this is a field level test";
        Mockito.when(result.getUtterance()).thenReturn(utterance);
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(1.0f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);
        handler.processEvent(item, event);

        Mockito.verify(model).updateVariable(name, utterance);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFieldLevelNomatch() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final String name = "testfield1";
        field.setName(name);
        final FieldFormItem item = new FieldFormItem(context, field);
        addInputRule(item, field, "this is a field level test");
        final Filled filled = field.appendChild(Filled.class);
        final Log log = filled.appendChild(Log.class);
        log.setExpr("'test: filled ' + " + name);
        final Nomatch nomatch = field.appendChild(Nomatch.class);
        final Log log2 = nomatch.appendChild(Log.class);
        log2.setTextContent("test: nomatch");
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, fia, item);

        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "this is a field level test";
        result.setUtterance(utterance);
        result.setAccepted(false);
        result.setConfidence(0.2f);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);
        handler.processEvent(item, event);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFormLevelFilled() throws Exception, JVoiceXMLEvent {
        final String name = "testfield2";
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filled = form.appendChild(Filled.class);
        final Log log = filled.appendChild(Log.class);
        log.setExpr("'test: ' + " + name);
        final Field field = form.appendChild(Field.class);
        field.setName(name);
        final FieldFormItem item = new FieldFormItem(context, field);
        addInputRule(item, field, "this is a form level test");
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, null, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, document);
        handler.collect(context, interpreter, fia, item);

        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "this is a form level test";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);
        handler.processEvent(item, event);

        Mockito.verify(model).updateVariable(name, utterance);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFormLevelNomatch() throws Exception, JVoiceXMLEvent {
        final String name = "testfield2";
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filled = form.appendChild(Filled.class);
        final Log log = filled.appendChild(Log.class);
        log.setExpr("'test: filled ' + " + name);
        final Nomatch nomatch = form.appendChild(Nomatch.class);
        final Log log2 = nomatch.appendChild(Log.class);
        log2.setExpr("'test: nomatch ' + " + name);
        final Field field = form.appendChild(Field.class);
        field.setName(name);
        final FieldFormItem item = new FieldFormItem(context, field);
        addInputRule(item, field, "this is a form level test");
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, dialog);
        handler.collect(context, interpreter, fia, item);

        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "this is a form level test";
        result.setUtterance(utterance);
        result.setAccepted(false);
        result.setConfidence(0.2f);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);
        handler.processEvent(item, event);

        Mockito.verify(model, never()).updateVariable(name, utterance);
        // The nomatch will not be processed since there is no related FIA.
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFormLevelFilledAll() throws Exception,
            JVoiceXMLEvent {
        final String name1 = "testfield1";
        final String name2 = "testfield2";
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filled = form.appendChild(Filled.class);
        filled.setMode(FilledMode.ALL);
        final Log log1 = filled.appendChild(Log.class);
        log1.setExpr("'test: ' + " + name1);
        final Log log2 = filled.appendChild(Log.class);
        log2.setExpr("'test: ' + " + name2);
        final Initial initial = form.appendChild(Initial.class);
        final Field field1 = form.appendChild(Field.class);
        field1.setName(name1);
        field1.appendChild(Noinput.class);
        field1.appendChild(Help.class);
        final FieldFormItem item1 = new FieldFormItem(context, field1);
        addInputRule(item1, field1, "input1");
        final Field field2 = form.appendChild(Field.class);
        field2.setName(name2);
        field2.appendChild(Noinput.class);
        field2.appendChild(Help.class);
        final FieldFormItem item2 = new FieldFormItem(context, field2);
        addInputRule(item2, field2, "input2");
        final Catch catchNode = field2.appendChild(Catch.class);
        catchNode.setEvent("test");
        final TokenList namelist = new TokenList();
        namelist.add(field1.getName());
        namelist.add(field2.getName());
        filled.setNamelist(namelist);

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        final InitialFormItem initialItem = new InitialFormItem(context,
                initial);
        handler.collect(context, interpreter, fia, initialItem);

        final String utterance1 = "input1";
        final String utterance2 = "input2";
        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn(utterance1);
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(1.0f);
        Mockito.when(result.getSemanticInterpretation(model)).thenReturn("out");
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        Mockito.when(
                model.readVariable("application.lastresult$.interpretation."
                        + field1.getName(), Object.class)).thenReturn(
                utterance1);
        Mockito.when(
                model.existsVariable("application.lastresult$.interpretation."
                        + field1.getName())).thenReturn(true);
        Mockito.when(
                model.readVariable("application.lastresult$.interpretation."
                        + field2.getName(), Object.class)).thenReturn(
                utterance2);
        Mockito.when(
                model.existsVariable("application.lastresult$.interpretation."
                        + field2.getName())).thenReturn(true);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);

        handler.processEvent(item2, event);
        Mockito.verify(model).updateVariable(name1, utterance1);
        Mockito.verify(model).updateVariable(name2, utterance2);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFormLevelFilledAllAny() throws Exception,
            JVoiceXMLEvent {
        final String name1 = "testfield1";
        final String name2 = "testfield2";
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filledAny = form.appendChild(Filled.class);
        filledAny.setMode(FilledMode.ANY);
        final Log logAny = filledAny.appendChild(Log.class);
        logAny.setExpr("'test: " + name1 + "'");
        final Filled filledAll = form.appendChild(Filled.class);
        filledAll.setMode(FilledMode.ALL);
        final Log logAll = filledAll.appendChild(Log.class);
        logAll.setExpr("'test: " + name2 + "'");
        final Initial initial = form.appendChild(Initial.class);
        final Field field1 = form.appendChild(Field.class);
        field1.setName(name1);
        field1.appendChild(Noinput.class);
        field1.appendChild(Help.class);
        final FieldFormItem item1 = new FieldFormItem(context, field1);
        addInputRule(item1, field1, "input1");
        final Field field2 = form.appendChild(Field.class);
        field2.setName(name2);
        field2.appendChild(Noinput.class);
        field2.appendChild(Help.class);
        final FieldFormItem item2 = new FieldFormItem(context, field2);
        addInputRule(item2, field2, "input2");
        final Catch catchNode = field2.appendChild(Catch.class);
        catchNode.setEvent("test");
        final TokenList namelist = new TokenList();
        namelist.add(field1.getName());
        namelist.add(field2.getName());
        filledAny.setNamelist(namelist);
        filledAll.setNamelist(namelist);

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        final InitialFormItem initialItem = new InitialFormItem(context,
                initial);
        handler.collect(context, null, fia, initialItem);

        final String utterance = "input2";
        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn(utterance);
        Mockito.when(result.isAccepted()).thenReturn(true);
        Mockito.when(result.getConfidence()).thenReturn(1.0f);
        Mockito.when(result.getSemanticInterpretation(model)).thenReturn("out");
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);

        Mockito.when(
                model.readVariable("application.lastresult$.interpretation."
                        + field1.getName(), Object.class))
                .thenReturn(utterance);
        Mockito.when(
                model.existsVariable("application.lastresult$.interpretation."
                        + field1.getName())).thenReturn(true);

        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);

        handler.processEvent(item2, event);
        Mockito.verify(model).updateVariable(name1, utterance);
        Mockito.verify(model, never()).updateVariable(name2, utterance);
    }

    /**
     * Adds a simple rule to the grammar taking the given input.
     * 
     * @param item
     *            the corresponding item
     * @param field
     *            the field where to add the grammar
     * @param input
     *            the expected input
     * @return created grammar.
     * @throws IOException
     *             Error creating the grammar implementation.
     * @throws SAXException
     *             Error creating the grammar implementation.
     * @throws ParserConfigurationException
     *             Error creating the grammar implementation.
     */
    private Grammar addInputRule(final FieldFormItem item, final Field field,
            final String input) throws ParserConfigurationException,
            SAXException, IOException {
        final Grammar grammar = field.appendChild(Grammar.class);
        grammar.setRoot("rule");
        final Rule rule1 = grammar.appendChild(Rule.class);
        rule1.setId(grammar.getRoot());
        rule1.addText(input);

        final GrammarDocument document = new InternalGrammarDocument(grammar);
        item.addGrammar(document);
        return grammar;
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFormLevelUnknown() throws Exception, JVoiceXMLEvent {
        final String name = "testfield3";
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filled = form.appendChild(Filled.class);
        final Log log = filled.appendChild(Log.class);
        log.setExpr("'test: ' + " + name);
        final Field field = form.appendChild(Field.class);
        field.setName(name);
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final FieldFormItem item = new FieldFormItem(context, field);
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, fia, item);

        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "this is a form level test";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        final JVoiceXMLEvent event = new GenericVoiceXmlEvent("dummy");
        handler.onEvent(event);
        JVoiceXMLEvent error = null;
        try {
            handler.processEvent(item, event);
        } catch (JVoiceXMLEvent e) {
            error = e;
        }
        Assert.assertEquals(event, error);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.CatchContainer)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testProcessFieldLevelHelp() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final String name = "testfieldhelp";
        field.setName(name);
        final FieldFormItem item = new FieldFormItem(context, field);
        addInputRule(item, field, "this is a field level test");
        field.appendChild(Filled.class);
        field.appendChild(Noinput.class);
        final Help help = field.appendChild(Help.class);
        final Log log = help.appendChild(Log.class);
        log.setExpr("'test: help'");
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia = new FormInterpretationAlgorithm(
                context, interpreter, dialog);
        fia.initialize(profile, null);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.collect(context, interpreter, fia, item);

        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "Zu Hülf!";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        result.setSemanticInterpretation("help");
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        handler.onEvent(event);
        handler.processEvent(item, event);
    }

    /**
     * Test method for {@link JVoiceXmlEventHandler#onEvent(JVoiceXMLEvent)}
     * .
     * 
     * @since 0.7.4
     */
    @Test(timeout = 1000)
    public void testNotifyEvent() {
        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "test";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.onEvent(event);
        final JVoiceXMLEvent waitEvent = handler.waitEvent();
        Assert.assertEquals(event.getEventType(), waitEvent.getEventType());
    }

    /**
     * Test method for {@link JVoiceXmlEventHandler#onEvent(JVoiceXMLEvent)}
     * .
     * 
     * @since 0.7.4
     */
    @Test(timeout = 1000)
    public void testNotifyEventCancel() {
        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "Bloß nicht";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        result.setSemanticInterpretation("cancel");
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.onEvent(event);
        final JVoiceXMLEvent waitEvent = handler.waitEvent();
        Assert.assertEquals(CancelEvent.EVENT_TYPE, waitEvent.getEventType());
    }

    /**
     * Test method for {@link JVoiceXmlEventHandler#onEvent(JVoiceXMLEvent)}
     * .
     * 
     * @since 0.7.4
     */
    @Test(timeout = 1000)
    public void testNotifyEventHelp() {
        final MockRecognitionResult result = new MockRecognitionResult();
        final String utterance = "Zu Hülf!";
        result.setUtterance(utterance);
        result.setAccepted(true);
        result.setConfidence(1.0f);
        result.setSemanticInterpretation("help");
        final RecognitionEvent event = new RecognitionEvent(null, null, result);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(model,
                context.getScopeObserver(), eventbus);
        handler.onEvent(event);
        final JVoiceXMLEvent waitEvent = handler.waitEvent();
        Assert.assertEquals(HelpEvent.EVENT_TYPE, waitEvent.getEventType());
    }

    /**
     * Checks if the given type has a corresponding entry in the list of
     * strategies.
     * 
     * @param strategies
     *            the strategies to check
     * @param type
     *            the type to look for
     * @return <code>true</code> if the type is contained in the list.
     */
    private boolean containsType(final Collection<EventStrategy> strategies,
            final String type) {
        for (EventStrategy strategy : strategies) {
            final String currentType = strategy.getEventType();
            if (type.equals(currentType)) {
                return true;
            }
        }
        return false;
    }
}
