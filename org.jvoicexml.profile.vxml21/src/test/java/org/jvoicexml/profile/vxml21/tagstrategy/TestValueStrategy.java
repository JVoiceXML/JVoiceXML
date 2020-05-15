/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.profile.vxml21.VoiceXml21Profile;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Value;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link ValueStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4233 $
 * @since 0.6
 */
public final class TestValueStrategy extends TagStrategyTestBase {
    /**
     * Set up the test environment.
     * 
     * @since 0.7.6
     */
    @Before
    public void setUp() {
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(factory.getParsingStrategy(Mockito.isA(Value.class)))
                .thenReturn(new ValueStrategy());
        final VoiceXmlInterpreterContext context = getContext();
        final Profile profile = context.getProfile();
        final TagStrategyFactory tagfactory = Mockito
                .mock(TagStrategyFactory.class);
        Mockito.when(tagfactory.getTagStrategy(Mockito.isA(Value.class)))
                .thenReturn(new ValueStrategy());
        Mockito.when(tagfactory.getTagStrategy(Mockito.isA(Text.class)))
                .thenReturn(new TextStrategy());
        final VoiceXml21Profile vxml21Profile = (VoiceXml21Profile) profile;
        vxml21Profile.setTagStrategyFactory(tagfactory);
    }

    /**
     * Test method for
     * {@link ValueStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                Test failed.
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test(expected = SemanticError.class)
    public void testExecuteUndefined() throws Exception, JVoiceXMLEvent {
        final String name = "test";
        final Block block = createBlock();
        final Value value = block.appendChild(Value.class);
        value.setExpr(name);

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(name, Object.class))
            .thenThrow(new SemanticError(name + " is not defined!"));
        
        final ValueStrategy strategy = new ValueStrategy();
        executeTagStrategy(value, strategy);
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
    public void testExecuteExpr() throws Exception, JVoiceXMLEvent {
        final String name = "test";
        final String val = "this is a test";
        final Block block = createBlock();
        final Value value = block.appendChild(Value.class);
        value.setExpr(name);

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(name, Object.class)).thenReturn(
                val);
        final ValueStrategy strategy = new ValueStrategy();
        executeTagStrategy(value, strategy);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("this is a test");
        final SpeakableText speakable = new SpeakableSsmlText(ssml);
        final ImplementationPlatform platform = getContext()
                .getImplementationPlatform();
        Mockito.verify(platform).queuePrompt(speakable);
    }
}
