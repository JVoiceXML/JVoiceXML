/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jvoicexml.event.JVoiceXMLEvent;
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
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * This class provides tests for {@link SsmlParser}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSsmlParser
        extends TestCase {
    /** The test VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = new VoiceXmlInterpreterContext(null);
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
    public void testGetDocument() throws Exception, JVoiceXMLEvent {
        final Prompt prompt = createPrompt();
        prompt.addText("This is a test");

        SsmlParser parser = new SsmlParser(prompt, context);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("This is a test");

        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
    public void testGetDocumentValue() throws Exception, JVoiceXMLEvent {
        final String testVar = "testvalue";
        final String testValue = "hurz";
        scripting.setVariable(testVar, testValue);

        final Prompt prompt = createPrompt();
        prompt.addText("This is a test");
        final Value value = prompt.appendChild(Value.class);
        value.setExpr(testVar);

        SsmlParser parser = new SsmlParser(prompt, context);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("This is a test");
        speak.addText(testValue);

        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
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
        submit.setMethod("post");
        submit.setNamelist("maincourse");

        SsmlParser parser = new SsmlParser(prompt, context);
        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("Please select an entree. Today, we are featuring");
        speak.addText("swordfish;roast beef;frog legs");
        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
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
        scripting.eval("prompts=GetMovieList()");
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
        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.SsmlParser#getDocument()}.
     * @exception Exception
     *            Test failed.
     * @throws JVoiceXMLEvent
     *            Test failed.
     */
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
        speak.addText("This is a test");
        final P ssmlp1 = speak.appendChild(P.class);
        ssmlp1.addText("Text within P");
        final P ssmlp2 = speak.appendChild(P.class);
        final Audio ssmlAudio = ssmlp2.appendChild(Audio.class);
        ssmlAudio.setSrc("src.wav");
        ssmlAudio.addText(testValue);
        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }
}
