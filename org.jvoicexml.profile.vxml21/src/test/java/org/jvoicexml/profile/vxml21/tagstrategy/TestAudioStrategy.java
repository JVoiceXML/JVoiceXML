/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import org.junit.Test;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlApplication;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test case for {@link PromptStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4233 $
 * @since 0.6
 */
public final class TestAudioStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Audio audio = block.appendChild(Audio.class);
        audio.setSrc("godfather.wav");
        audio.addText("the godfather");

        final AudioTagStrategy strategy = new AudioTagStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        executeTagStrategy(audio, strategy);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        final Audio ssmlAudio = speak.appendChild(Audio.class);
        ssmlAudio.setSrc("godfather.wav");
        ssmlAudio.addText("the godfather");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        final VoiceXmlInterpreterContext context = getContext();
        final DocumentServer server = context.getDocumentServer();
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable), 
                Mockito.eq(server));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    @Test
    public void testExecuteBase() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlBase("http://acme.com/");
        final Block block = createBlock(document);
        final Audio audio = block.appendChild(Audio.class);
        audio.setSrc("godfather.wav");
        audio.addText("the godfather");

        final Application application = new JVoiceXmlApplication(null);
        final VoiceXmlInterpreterContext ctx = getContext();
        Mockito.when(ctx.getApplication()).thenReturn(application);
        final URI uri = new URI("http://acme.com/start.vxml");
        application.addDocument(uri, document);
        final AudioTagStrategy strategy = new AudioTagStrategy();
        final ImplementationPlatform platform = getImplementationPlatform();
        executeTagStrategy(audio, strategy);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        final Audio ssmlAudio = speak.appendChild(Audio.class);
        ssmlAudio.setSrc("http://acme.com/godfather.wav");
        ssmlAudio.addText("the godfather");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        final VoiceXmlInterpreterContext context = getContext();
        final DocumentServer server = context.getDocumentServer();
        Mockito.verify(platform).queuePrompt(Mockito.eq(speakable), 
                Mockito.eq(server));
    }
}
