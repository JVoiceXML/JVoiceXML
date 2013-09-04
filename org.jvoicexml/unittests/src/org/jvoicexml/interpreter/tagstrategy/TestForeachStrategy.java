/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestValueStrategy.java $
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 11:25:46 +0100 (Mo, 10 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.xml.ccxml.Script;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Foreach;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;

/**
 * This class provides a test case for the {@link ForeachTagStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2493 $
 * @since 0.6
 */
public final class TestForeachStrategy extends TagStrategyTestBase
    implements SynthesizedOutputListener {
    /** The queued speakable. */
    private List<SpeakableText> queuedSpeakables;

    /**
     * Set up the test environment.
     * 
     * @since 0.7.6
     */
    @Before
    public void setUp() {
        queuedSpeakables = new java.util.ArrayList<SpeakableText>();
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final String expr = "var " + names + " = new Array(3);"
                + "names[0] = \"Hans\";"
                + "names[1] = \"Gabi\";"
                + "names[2] = \"Erna\";";
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval(expr);
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        final Foreach foreach = prompt.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        foreach.addText("Current name is ");
        final Value value = foreach.appendChild(Value.class);
        value.setExpr("name");
        setSystemOutputListener(this);
        final TagStrategy strategy = new PromptStrategy();
        executeTagStrategy(prompt, strategy);
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Current name is Hans Current name is Gabi "
                + "Current name is Erna");
        final SpeakableText speakable = new SpeakableSsmlText(doc);
        Assert.assertEquals(1, queuedSpeakables.size());
        Assert.assertEquals(speakable, queuedSpeakables.get(0));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void testExecuteInBlock() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final String expr = "var " + names + " = new Array(3);"
                + "names[0] = \"Hans\";"
                + "names[1] = \"Gabi\";"
                + "names[2] = \"Erna\";";
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval(expr);
        final Block block = createBlock();
        final Foreach foreach = block.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        foreach.addText("Current name is ");
        final Value value = foreach.appendChild(Value.class);
        value.setExpr("name");
        setSystemOutputListener(this);
        final TagStrategy strategy = new ForeachTagStrategy();
        executeTagStrategy(foreach, strategy);
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Current name is Hans Current name is Gabi "
                + "Current name is Erna");
        final SpeakableText speakable = new SpeakableSsmlText(doc);
        Assert.assertEquals(6, queuedSpeakables.size());
        Assert.assertEquals(speakable, queuedSpeakables.get(0));
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     * @since 0.7.6
     */
    @Test
    public void testExecuteScript() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final String expr = "var " + names + " = new Array(3);"
                + "names[0] = \"Hans\";"
                + "names[1] = \"Gabi\";"
                + "names[2] = \"Erna\";";
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval(expr);
        scripting.eval("var count = 0;");
        final Block block = createBlock();
        final Foreach foreach = block.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        final Script script = foreach.appendChild(Script.class);
        script.addText(" count = count + 2;");
        setSystemOutputListener(this);
        final TagStrategy strategy = new ForeachTagStrategy();
        executeTagStrategy(foreach, strategy);
        Assert.assertEquals(0, queuedSpeakables.size());
        Assert.assertEquals(new Double(6.0f), scripting.getVariable("count"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        final int id = event.getEvent();
        if (id == SynthesizedOutputEvent.OUTPUT_STARTED) {
            final OutputStartedEvent started = (OutputStartedEvent) event;
            final SpeakableText speakable = started.getSpeakable();
            queuedSpeakables.add(speakable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
    }

}
