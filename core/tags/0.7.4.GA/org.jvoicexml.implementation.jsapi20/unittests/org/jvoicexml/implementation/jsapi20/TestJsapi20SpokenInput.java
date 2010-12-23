/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-04-19 20:20:06 +0200 (Mo, 19 Apr 2010) $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.jsapi20;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Locale;

import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.recognition.RecognizerMode;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.jsapi2.sapi.SapiEngineListFactory;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link Jsapi20SynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.4
 */
public class TestJsapi20SpokenInput implements SpokenInputListener {
    /** The test object. */
    private Jsapi20SpokenInput input;

    private final Object lock = new Object();

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        EngineManager.registerEngineListFactory(
                SapiEngineListFactory.class.getCanonicalName());
        System.setProperty("javax.speech.supports.audio.management",
                Boolean.TRUE.toString());
        System.setProperty("javax.speech.supports.audio.capture",
                Boolean.TRUE.toString());
    }

    /**
     * Set up the test environment
     * @throws java.lang.Exception
     *         set up failed
     * @throws JVoiceXMLEvent
     *         set up failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        final RecognizerMode mode = RecognizerMode.DEFAULT;
        input = new Jsapi20SpokenInput(mode, null);
        input.open();
        input.addListener(this);
        input.activate();
    }

    /**
     * Test tear down.
     * @exception Exception
     *            tear down failed
     */
    @After
    public void tearDown() throws Exception {
        input.passivate();
        input.close();
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
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setXmlLang(Locale.US);
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setScope("public");
        rule.setId("root");
        grammar.setRoot(rule);
        rule.addText("This is a test");
        final String xml = document.toString();
        final Reader reader = new StringReader(xml);
        final GrammarImplementation<?> impl =
            input.loadGrammar(reader, GrammarType.SRGS_XML);
        final Collection<GrammarImplementation<?>> implementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        implementations.add(impl);
        input.activateGrammars(implementations);
    }

    @Test
    public void testStartRecognition() throws Exception, JVoiceXMLEvent {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setXmlLang(Locale.US);
        final Rule rule = grammar.appendChild(Rule.class);
        rule.makePublic();
        rule.setId("root");
        grammar.setRoot(rule);
        rule.addText("test");
        final String xml = document.toString();
        final Reader reader = new StringReader(xml);
        final GrammarImplementation<?> impl =
            input.loadGrammar(reader, GrammarType.SRGS_XML);
        final Collection<GrammarImplementation<?>> implementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        implementations.add(impl);
        input.activateGrammars(implementations);
        input.startRecognition();
        synchronized (lock) {
            lock.wait();
        }
    }

    @Override
    public void inputStatusChanged(final SpokenInputEvent event) {
        System.out.println(event);
        synchronized (lock) {
            lock.notifyAll();
        }
        
    }

    @Override
    public void inputError(final ErrorEvent error) {
        System.out.println(error);
    }
}
