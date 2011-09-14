/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Foreach;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Option;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.xml.sax.InputSource;

/**
 * This class provides tests for {@link SsmlParser}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestSsmlParser {
    /** The test VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore core = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, core, null);
        context = new VoiceXmlInterpreterContext(session, null);
        scripting = context.getScriptingEngine();
    }

    /**
     * Creates an empty prompt.
     * @return the created prompt
     * @throws ParserConfigurationException
     *         Error creating the document.
     */
    private Prompt createPrompt() throws ParserConfigurationException {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang(Locale.US);
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        final Prompt prompt = block.appendChild(Prompt.class);
        return prompt;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetDocument() throws Exception, JVoiceXMLEvent {
        final Prompt prompt = createPrompt();
        prompt.addText("This is a test");

        SsmlParser parser = new SsmlParser(prompt, context);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("This is a test");
        Assert.assertEquals(ssml.toString(), parser.getDocument().toString());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetDocumentValue() throws Exception, JVoiceXMLEvent {
        final String testVar = "testvalue";
        final String testValue = "hurz";
        scripting.setVariable(testVar, testValue);

        final Prompt prompt = createPrompt();
        prompt.addText("This is a test");
        final Value value = prompt.appendChild(Value.class);
        value.setExpr(testVar);
        prompt.addText("with an inserted value");

        SsmlParser parser = new SsmlParser(prompt, context);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("This is a test");
        speak.addText(testValue);
        speak.addText("with an inserted value");

        Assert.assertEquals(ssml.toString(), parser.getDocument().toString());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetDocumentEnumerate() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();

        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("maincourse");
        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Please select an entree. Today, we are featuring");
        prompt.appendChild(Enumerate.class);
        final Option option1 = field.appendChild(Option.class);
        option1.setDtmf("1");
        option1.setValue("fish");
        option1.addText("swordfish");
        final Option option2 = field.appendChild(Option.class);
        option2.setDtmf("2");
        option2.setValue("beef");
        option2.addText("roast beef");
        final Option option3 = field.appendChild(Option.class);
        option3.setDtmf("3");
        option3.setValue("chicken");
        option3.addText("frog legs");
        final Filled filled = field.appendChild(Filled.class);
        final Submit submit = filled.appendChild(Submit.class);
        submit.setNext("/cgi-bin/maincourse.cgi");
        submit.setMethod(RequestMethod.POST);
        submit.setNamelist("maincourse");

        SsmlParser parser = new SsmlParser(prompt, context);
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("Please select an entree. Today, we are featuring");
        speak.addText("swordfish;roast beef;frog legs");
        Assert.assertEquals(ssml.toString(), parser.getDocument().toString());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetDocumentForEach() throws Exception, JVoiceXMLEvent {
        scripting.eval("function GetMovieList()"
                + "{"
                + "var movies = new Array(3);"
                + "movies[0] = new Object();"
                + "movies[0].audio = \"godfather.wav\";"
                + "movies[0].tts = \"the godfather\";"
                + "movies[1] = new Object();"
                + "movies[1].audio = \"high_fidelity.wav\";"
                + "movies[1].tts = \"high fidelity\";"
                + "movies[2] = new Object();"
                + "movies[2].audio = \"raiders.wav\";"
                + "movies[2].tts = \"raiders of the lost ark\";"
                + "return movies;"
                + "}");
        scripting.eval("var prompts=GetMovieList();");
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();

        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("movie");
        final Prompt prompt = field.appendChild(Prompt.class);
        final String baseUri = "http://localhost/audiofiles/";
        prompt.setXmlBase(baseUri);
        prompt.addText(
                "When you hear the name of the movie you want, just say it.");
        final Foreach foreach = prompt.appendChild(Foreach.class);
        foreach.setArray("prompts");
        foreach.setItem("thePrompt");
        final Audio audio = foreach.appendChild(Audio.class);
        audio.setExpr("thePrompt.audio");
        final Value value = audio.appendChild(Value.class);
        value.setExpr("thePrompt.tts");

        final SsmlParser parser = new SsmlParser(prompt, context);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText(
                "When you hear the name of the movie you want, just say it.");
        final Audio audio1 = speak.appendChild(Audio.class);
        audio1.setSrc(baseUri + "godfather.wav");
        audio1.addText("the godfather");
        final Audio audio2 = speak.appendChild(Audio.class);
        audio2.setSrc(baseUri + "high_fidelity.wav");
        audio2.addText("high fidelity");
        final Audio audio3 = speak.appendChild(Audio.class);
        audio3.setSrc(baseUri + "raiders.wav");
        audio3.addText("raiders of the lost ark");
        Assert.assertEquals(ssml.toString(), parser.getDocument().toString());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testGetDocumentDeepClone() throws Exception, JVoiceXMLEvent {
        final String testVar = "testvalue";
        final String testValue = "hurz";
        scripting.setVariable(testVar, testValue);

        final Prompt prompt = createPrompt();
        prompt.addText("This is a test");
        final P p1 = prompt.appendChild(P.class);
        p1.addText("Text within P");
        final P p2 = prompt.appendChild(P.class);
        final Audio audio = p2.appendChild(Audio.class);
        audio.setSrc("src.wav");
        final Value value = audio.appendChild(Value.class);
        value.setExpr(testVar);

        SsmlParser parser = new SsmlParser(prompt, context);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.setXmlLang(Locale.US);
        speak.addText("This is a test");
        final P ssmlp1 = speak.appendChild(P.class);
        ssmlp1.addText("Text within P");
        final P ssmlp2 = speak.appendChild(P.class);
        final Audio ssmlAudio = ssmlp2.appendChild(Audio.class);
        ssmlAudio.setSrc("src.wav");
        ssmlAudio.addText(testValue);
        Assert.assertEquals(ssml.toString(), parser.getDocument().toString());
    }
    
    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testDocumentNamespaces() throws Exception, JVoiceXMLEvent {
        final InputStream in = TestSsmlParser.class.getResourceAsStream(
                "SsmlParserNSExample.vxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument doc = new VoiceXmlDocument(source);
        final Vxml vxml = doc.getVxml();
        final Collection<Form> forms = vxml.getChildNodes(Form.class);
        final Form form = forms.iterator().next();
        final Collection<Block> blocks = form.getChildNodes(Block.class);
        final Block block = blocks.iterator().next();
        final Collection<Prompt> prompts = block.getChildNodes(Prompt.class);
        final Prompt prompt = prompts.iterator().next();
        final SsmlParser parser = new SsmlParser(prompt, context);
        System.out.println(parser.getDocument());
    }
}
