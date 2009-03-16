/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi10.grammar;

import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.test.implementation.DummyUserInput;
import org.jvoicexml.test.implementation.Jsapi10DummyUserInput;
import org.jvoicexml.test.interpreter.grammar.GrammarUtil;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Unit tests for the {@link SrgsXml2JsgfGrammarTransformer}.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 */
public final class TestSrgsXml2JsgfGrammarTransformer {
    /**
     * The class, which will be tested.
     */
    private GrammarTransformer transformer;

    /**
     * The Recognizer from which to get the empty rule Grammar.
     */
    private DummyUserInput input;

    /**
     * Set up the test environment.
     * @exception Exception
     *            error in set up
     */
    @Before
    public void setUp() throws Exception {
        /* create a very new transformer */
        transformer = new SrgsXml2JsgfGrammarTransformer();
        input = new Jsapi10DummyUserInput();
    }

    /**
     * Tear down the test environment.
     */
    @After
    public void tearDown() {
        if (input != null) {
            input = null;
        }
    }

    /**
     * Test 1 of the "Implementation Report Plan". This report plan provides 148
     * tests to check the SRGS compliance.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test1() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "1/1_grammar.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("rule1", grammar.getName());
        final Rule rule1 = grammar.getRule("rule1");
        Assert.assertEquals("1", rule1.toString());
    }

    /**
     * Test 2a of the "Implementation Report Plan". This report plan provides
     * 148 tests to check the SRGS compliance.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test2a() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "2/2_grammar_a.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("rule2", grammar.getName());
        final Rule rule2 = grammar.getRule("rule2");
        Assert.assertEquals("1", rule2.toString());
    }

    /**
     * Test 2b of the "Implementation Report Plan". This report plan provides
     * 148 tests to check the SRGS compliance.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test2b() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "2/2_grammar_b.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("ruleb", grammar.getName());
        final Rule ruleb = grammar.getRule("ruleb");
        Assert.assertEquals("2", ruleb.toString());
    }

    /**
     * Test 3 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test3() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "3/3_grammar_a.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("rule3", grammar.getName());
        final Rule rule3 = grammar.getRule("rule3");
        Assert.assertEquals("2", rule3.toString());
    }

    /**
     * Test of conformance 1.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConformance1() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(
                GrammarUtil.BASE_SRGS_10 + "/conformance-1.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("main", grammar.getName());
        final Rule rule = grammar.getRule("main");
        Assert.assertEquals(
                "[please call] ( ( Jean Francois | John Paul | Dominic ) |"
                + " ( Jean Francois | John Paul | Dominic ) ) [thanks]",
                rule.toString());
    }

    /**
     * Test of conformance 3.
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConformance3() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(
                GrammarUtil.BASE_SRGS_10 + "/conformance-3.grxml");

        final GrammarImplementation<RuleGrammar> impl =
            (GrammarImplementation<RuleGrammar>) transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        Assert.assertEquals("main", grammar.getName());
        final Rule rule = grammar.getRule("main");
        Assert.assertNotNull(rule);
        // TODO continue this test.
    }
}
