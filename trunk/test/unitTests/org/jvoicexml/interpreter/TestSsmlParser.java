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
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
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
    /** The scripting engine. */
    private ScriptingEngine scripting;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        scripting = new ScriptingEngine(null);
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

        SsmlParser parser = new SsmlParser(prompt, scripting);

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

        SsmlParser parser = new SsmlParser(prompt, scripting);

        SsmlDocument ssml = new SsmlDocument();
        Speak speak = ssml.getSpeak();
        speak.addText("This is a test");
        speak.addText(testValue);

        assertTrue(speak.isEqualNode(parser.getDocument().getSpeak()));
    }
}
