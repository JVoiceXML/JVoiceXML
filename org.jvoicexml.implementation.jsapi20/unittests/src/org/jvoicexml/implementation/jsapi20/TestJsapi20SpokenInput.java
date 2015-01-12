/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Locale;

import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.recognition.RecognizerMode;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.mock.TestProperties;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link Jsapi20SynthesizedOutput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class TestJsapi20SpokenInput implements SpokenInputListener {
    /** The test object. */
    private Jsapi20SpokenInput input;

    /** Semaphore to notify a recognition result. */
    private final Object monitor = new Object();

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     * @throws IOException
     *         error reading the test properties file
     */
    @BeforeClass
    public static void init() throws EngineException, IOException {
        final TestProperties properties = new TestProperties();
        final String factory = properties.get("jsapi2.asr.engineListFactory");
        EngineManager.registerEngineListFactory(factory);
    }

    /**
     * Set up the test environment.
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
     * Writes the given grammar to a file.
     * @param file the file to write to
     * @param grammar the grammar to write
     * @return URI of the file
     * @throws IOException
     *          error writing
     * @since 0.7.7
     */
    private URI writeToFile(final File file, final String grammar)
            throws IOException {
        final FileWriter writer = new FileWriter(file);
        writer.write(grammar);
        writer.close();
        return file.toURI();
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
        final File file = File.createTempFile("jvxmltest", "srgs");
        final URI uri = writeToFile(file, xml);
        final GrammarImplementation<?> impl =
            input.loadGrammar(uri, GrammarType.SRGS_XML);
        final Collection<GrammarImplementation<?>> implementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        implementations.add(impl);
        input.activateGrammars(implementations);
    }

    /**
     * Test case for {@link Jsapi20SpokenInput#startRecognition(org.jvoicexml.SpeechRecognizerProperties, org.jvoicexml.DtmfRecognizerProperties)}.
     * @throws Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     */
    @Test(timeout = 4000)
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
        final File file = File.createTempFile("jvxmltest", "srgs");
        final URI uri = writeToFile(file, xml);
        final GrammarImplementation<?> impl =
            input.loadGrammar(uri, GrammarType.SRGS_XML);
        final Collection<GrammarImplementation<?>> implementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
        implementations.add(impl);
        input.activateGrammars(implementations);
        input.startRecognition(null, null);
        synchronized (monitor) {
            monitor.wait();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputStatusChanged(final SpokenInputEvent event) {
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputError(final ErrorEvent error) {
        System.out.println(error);
    }
}
