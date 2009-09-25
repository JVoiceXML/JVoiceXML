/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.implementation.jsapi10;

import java.io.StringReader;
import java.util.Collection;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleGrammar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4EngineCentral;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4RecognizerModeDesc;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Test cases for {@link Jsapi10SpokenInput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public class TestJsapi10SpokenInput {
    /** The recognizer to test. */
    private Jsapi10SpokenInput recognizer;

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        Central.registerEngineCentral(Sphinx4EngineCentral.class.getName());
    }

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         setup failed
     * @throws JVoiceXMLEvent
     *         setup failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        final RecognizerModeDesc desc = new Sphinx4RecognizerModeDesc();
        recognizer = new Jsapi10SpokenInput(desc);
        recognizer.open();
        recognizer.activate();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.Jsapi10SpokenInput#loadGrammar(java.io.Reader, org.jvoicexml.xml.srgs.GrammarType)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testLoadGrammar() throws Exception, JVoiceXMLEvent {
        final String lf = System.getProperty("line.separator");
        final String grammar = "#JSGF V1.0;" + lf
            + "grammar test;" + lf
            + "public <test> = a|b|c;";
        final StringReader reader = new StringReader(grammar);
        final RuleGrammarImplementation impl = (RuleGrammarImplementation)
            recognizer.loadGrammar(reader, GrammarType.JSGF);
        Assert.assertNotNull(impl);
        final RuleGrammar ruleGrammar = impl.getGrammar();
        Assert.assertEquals("test", ruleGrammar.getName());
        final RuleAlternatives alternatives = (RuleAlternatives)
            ruleGrammar.getRule("test");
        Assert.assertNotNull(alternatives);
        final Rule[] rules = alternatives.getRules();
        Assert.assertEquals(3, rules.length);
        Assert.assertEquals("a", rules[0].toString());
        Assert.assertEquals("b", rules[1].toString());
        Assert.assertEquals("c", rules[2].toString());
    }

    /**
     * Test case for {@link Jsapi10SpokenInput#activateGrammars(Collection)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testActivateGrammars() throws Exception, JVoiceXMLEvent {
        final String lf = System.getProperty("line.separator");
        final String grammar = "#JSGF V1.0;" + lf
            + "grammar test;" + lf
            + "public <test> = a|b|c;";
        final StringReader reader = new StringReader(grammar);
        final RuleGrammarImplementation impl = (RuleGrammarImplementation)
            recognizer.loadGrammar(reader, GrammarType.JSGF);
        final Collection<GrammarImplementation<?>> implementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        implementations.add(impl);
        recognizer.activateGrammars(implementations);
    }
}
