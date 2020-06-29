/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestForeachStrategy.java $
 * Version: $LastChangedRevision: 4233 $
 * Date:    $Date: 2014-09-02 09:14:31 +0200 (Tue, 02 Sep 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.profile.TagStrategy;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.profile.vxml21.VoiceXml21Profile;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ccxml.Script;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Foreach;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link ForeachTagStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4233 $
 * @since 0.6
 */
public final class TestForeachStrategy extends TagStrategyTestBase {
    /**
     * Set up the test environment.
     * 
     * @since 0.7.6
     */
    @Before
    public void setUp() {
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(factory.getParsingStrategy(Mockito.isA(Foreach.class)))
                .thenReturn(new ForeachTagStrategy());
        Mockito.when(factory.getParsingStrategy(Mockito.isA(Value.class)))
                .thenReturn(new ValueStrategy());
        final VoiceXmlInterpreterContext context = getContext();
        final Profile profile = context.getProfile();
        final TagStrategyFactory tagfactory = Mockito
                .mock(TagStrategyFactory.class);
        Mockito.when(tagfactory.getTagStrategy(Mockito.isA(Foreach.class)))
                .thenReturn(new ForeachTagStrategy());
        Mockito.when(tagfactory.getTagStrategy(Mockito.isA(Value.class)))
                .thenReturn(new ValueStrategy());
        Mockito.when(tagfactory.getTagStrategy(Mockito.isA(Text.class)))
                .thenReturn(new TextStrategy());
        final VoiceXml21Profile vxml21Profile = (VoiceXml21Profile) profile;
        vxml21Profile.setTagStrategyFactory(tagfactory);

        final ImplementationPlatform platform = Mockito
                .mock(ImplementationPlatform.class);
        Mockito.when(context.getImplementationPlatform()).thenReturn(platform);
    }

    /**
     * Test method for
     * {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        final Foreach foreach = prompt.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        foreach.addText("Current name is ");
        final Value value = foreach.appendChild(Value.class);
        value.setExpr("name");

        final DataModel model = getDataModel();
        final String[] namesarray = new String[] { "Hans", "Gabi", "Erna" };
        Mockito.when(model.readVariable(names, Object[].class)).thenReturn(
                namesarray);
        Mockito.when(model.evaluateExpression("name", Object.class))
                .thenReturn(namesarray[0], namesarray[1], namesarray[2]);

        final TagStrategy strategy = new PromptStrategy();
        executeTagStrategy(prompt, strategy);

        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Current name is Hans Current name is Gabi "
                + "Current name is Erna");
        final SpeakableText speakable = new SpeakableSsmlText(doc);
        final ImplementationPlatform platform = getContext()
                .getImplementationPlatform();
        Mockito.verify(platform).queuePrompt(speakable);
    }

    /**
     * Test method for
     * {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                test failed
     * @since 0.7.6
     */
    @Test
    public void testExecuteInBlock() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final Block block = createBlock();
        final Foreach foreach = block.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        foreach.addText("Current name is ");
        final Value value = foreach.appendChild(Value.class);
        value.setExpr("name");

        final DataModel model = getDataModel();
        final String[] namesarray = new String[] { "Hans", "Gabi", "Erna" };
        Mockito.when(model.readVariable(names, Object[].class)).thenReturn(
                namesarray);
        Mockito.when(model.evaluateExpression("name", Object.class))
                .thenReturn(namesarray[0], namesarray[1], namesarray[2]);

        final TagStrategy strategy = new ForeachTagStrategy();
        executeTagStrategy(foreach, strategy);

        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Current name is Hans Current name is Gabi "
                + "Current name is Erna");
        final SpeakableText speakable = new SpeakableSsmlText(doc);
        final ImplementationPlatform platform = getContext()
                .getImplementationPlatform();
        Mockito.verify(platform, Mockito.times(3)).queuePrompt(speakable);
    }

    /**
     * Test method for
     * {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                test failed
     * @since 0.7.6
     */
    @Test
    public void testExecuteScript() throws Exception, JVoiceXMLEvent {
        final String names = "names";
        final Block block = createBlock();
        final Foreach foreach = block.appendChild(Foreach.class);
        foreach.setArray(names);
        foreach.setItem("name");
        final Script script = foreach.appendChild(Script.class);
        script.addText(" count = count + 2;");

        final DataModel model = getDataModel();
        final String[] namesarray = new String[] { "Hans", "Gabi", "Erna" };
        Mockito.when(model.readVariable(names, Object[].class)).thenReturn(
                namesarray);
        Mockito.when(model.evaluateExpression("name", Object.class))
                .thenReturn(namesarray[0], namesarray[1], namesarray[2]);

        final TagStrategy strategy = new ForeachTagStrategy();
        executeTagStrategy(foreach, strategy);

        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Current name is Hans Current name is Gabi "
                + "Current name is Erna");
        final SpeakableText speakable = new SpeakableSsmlText(doc);
        final ImplementationPlatform platform = getContext()
                .getImplementationPlatform();
        Mockito.verify(platform, Mockito.times(3)).queuePrompt(speakable);
        Mockito.verify(model, Mockito.times(3)).evaluateExpression(
                script.getTextContent(), Object.class);
    }
}
