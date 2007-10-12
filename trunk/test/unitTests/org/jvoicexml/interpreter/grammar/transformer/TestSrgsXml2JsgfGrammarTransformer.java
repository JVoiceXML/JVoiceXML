/*
 * File:    $RCSfile: TestSrgsXmlGrammarTransformer.java,v $
 * Version: $Revision: 479 $
 * Date:    $Date: 2007-10-09 10:11:56 +0200 (Di, 09 Okt 2007) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
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
package org.jvoicexml.interpreter.grammar.transformer;

import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import junit.framework.TestCase;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.interpreter.grammar.GrammarUtil;
import org.jvoicexml.test.implementationplatform.DummyUserInput;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Unit tests for the {@link SrgsXml2JsgfGrammarTransformer}.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision: 479 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSrgsXml2JsgfGrammarTransformer extends TestCase {
    /**
     * The class, which will be tested.
     */
    private GrammarTransformer transformer;

    /**
     * The Recognizer from which to get the empty rule Grammar.
     */
    private UserInput input;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        /* create a very new transformer */
        transformer = new SrgsXml2JsgfGrammarTransformer();
        input = new DummyUserInput();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() {
        if (input != null) {
            input.close();
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
    public void test1() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "1/1_grammar.grxml");

        final GrammarImplementation<RuleGrammar> impl = transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        assertEquals("rule1", grammar.getName());
        final Rule rule1 = grammar.getRule("rule1");
        assertEquals("1", rule1.toString());
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
    public void test2a() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "2/2_grammar_a.grxml");

        final GrammarImplementation<RuleGrammar> impl = transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        assertEquals("rule2", grammar.getName());
        final Rule rule2 = grammar.getRule("rule2");
        assertEquals("1", rule2.toString());
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
    public void test2b() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "2/2_grammar_b.grxml");

        final GrammarImplementation<RuleGrammar> impl = transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        assertEquals("ruleb", grammar.getName());
        final Rule ruleb = grammar.getRule("ruleb");
        assertEquals("2", ruleb.toString());
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
    public void test3() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(GrammarUtil.BASE21
                + "3/3_grammar_a.grxml");

        final GrammarImplementation<RuleGrammar> impl = transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        assertEquals("rule3", grammar.getName());
        final Rule rule3 = grammar.getRule("rule3");
        assertEquals("2", rule3.toString());
    }

    /**
     * Test 5 of the "VXML 2.1 Implementation Report Plan".
     *
     * @throws Exception
     *             Test failed.
     * @throws JVoiceXMLEvent
     *             Test failed.
     */
    public void testConformance1() throws Exception, JVoiceXMLEvent {
        GrammarDocument doc = GrammarUtil.getGrammarFromFile(
                GrammarUtil.BASE_SRGS_10 + "/conformance-1.grxml");

        final GrammarImplementation<RuleGrammar> impl = transformer
                .createGrammar(input, doc, GrammarType.SRGS_XML);
        assertEquals(GrammarType.JSGF, impl.getMediaType());
        final RuleGrammar grammar = impl.getGrammar();
        assertEquals("rule3", grammar.getName());
        final Rule rule3 = grammar.getRule("rule3");
        assertEquals("2", rule3.toString());
    }
}
