/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Test;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;
import org.mockito.Mockito;

/**
 * Test case for {@link PromptStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class TestPromptStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("When you hear the name of the movie you want, just say it.");
        final Audio audio1 = prompt.appendChild(Audio.class);
        audio1.setSrc("godfather.wav");
        audio1.addText("the godfather");
        final Audio audio2 = prompt.appendChild(Audio.class);
        audio2.setSrc("high_fidelity.wav");
        audio2.addText("high fidelity");
        final Audio audio3 = prompt.appendChild(Audio.class);
        audio3.setSrc("raiders.wav");
        audio3.addText("raiders of the lost ark");

        final PromptStrategy strategy = new PromptStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        platform.startPromptQueuing();
        executeTagStrategy(prompt, strategy);
        final CallControlProperties props = new CallControlProperties();
        platform.renderPrompts(null, null, props);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("When you hear the name of the movie you want, just say it.");
        final Audio ssmlAudio1 = speak.appendChild(Audio.class);
        ssmlAudio1.setSrc("godfather.wav");
        ssmlAudio1.addText("the godfather");
        final Audio ssmlAudio2 = speak.appendChild(Audio.class);
        ssmlAudio2.setSrc("high_fidelity.wav");
        ssmlAudio2.addText("high fidelity");
        final Audio ssmlAudio3 = speak.appendChild(Audio.class);
        ssmlAudio3.setSrc("raiders.wav");
        ssmlAudio3.addText("raiders of the lost ark");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable));
    }

    /**
     * Test method for occurrences of a {@code value}-tag within a prompt.
     * 
     * @since 0.7.7
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testExecuteValue() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("this is");
        final Value value = prompt.appendChild(Value.class);
        value.setExpr("foo");
        prompt.addText("times");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression("foo", Object.class)).thenReturn(
                42);

        final PromptStrategy strategy = new PromptStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        platform.startPromptQueuing();
        executeTagStrategy(prompt, strategy);
        final CallControlProperties props = new CallControlProperties();
        platform.renderPrompts(null, null, props);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("this is 42 times");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     */
    @Test
    public void testExecuteCond() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("demo");
        prompt.setCond("3 == 4");

        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(prompt.getCond(), Object.class))
                .thenReturn(false);
        
        final PromptStrategy strategy = new PromptStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        platform.startPromptQueuing();
        executeTagStrategy(prompt, strategy);

        Mockito.verify(platform, Mockito.never()).queuePrompt(Mockito.any());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     * @since 0.7
     */
    @Test
    public void testExecuteTimeout() throws Exception, JVoiceXMLEvent {
        final VoiceXmlInterpreterContext context = getContext();
        context.setProperty("timeout", "20s");
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("test execute timeout");
        prompt.setTimeout("10s");
        final PromptStrategy strategy = new PromptStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        platform.startPromptQueuing();
        executeTagStrategy(prompt, strategy);
        final CallControlProperties props = new CallControlProperties();
        platform.renderPrompts(null, null, props);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("test execute timeout");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                test failed.
     * @exception JVoiceXMLEvent
     *                test failed.
     * @since 0.7
     */
    @Test
    public void testExecuteTimeoutProperty() throws Exception, JVoiceXMLEvent {
        final VoiceXmlInterpreterContext context = getContext();
        context.setProperty("timeout", "20s");
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("test execute timeout");
        final PromptStrategy strategy = new PromptStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        platform.startPromptQueuing();
        executeTagStrategy(prompt, strategy);
        final CallControlProperties props = new CallControlProperties();
        platform.renderPrompts(null, null, props);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText("test execute timeout");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable));
    }
}
