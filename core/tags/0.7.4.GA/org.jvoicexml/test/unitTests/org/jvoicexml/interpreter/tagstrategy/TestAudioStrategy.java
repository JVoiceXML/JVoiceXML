/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.tagstrategy;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.Application;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.interpreter.JVoiceXmlApplication;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link PromptStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestAudioStrategy extends TagStrategyTestBase
    implements SynthesizedOutputListener {
    /** The queued speakable. */
    private SpeakableText queuedSpeakable;

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

        setSystemOutputListener(this);
        final AudioTagStrategy strategy = new AudioTagStrategy();
        executeTagStrategy(audio, strategy);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        final Audio ssmlAudio = speak.appendChild(Audio.class);
        ssmlAudio.setSrc("godfather.wav");
        ssmlAudio.addText("the godfather");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Assert.assertEquals(speakable, queuedSpeakable);
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

        setSystemOutputListener(this);
        final Application application = new JVoiceXmlApplication(null);
        final VoiceXmlInterpreterContext ctx = getContext();
        ctx.process(application);
        final URI uri = new URI("http://acme.com/start.vxml");
        application.addDocument(uri, document);
        final AudioTagStrategy strategy = new AudioTagStrategy();
        executeTagStrategy(audio, strategy);

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        final Audio ssmlAudio = speak.appendChild(Audio.class);
        ssmlAudio.setSrc("http://acme.com/godfather.wav");
        ssmlAudio.addText("the godfather");

        final SpeakableSsmlText speakable = new SpeakableSsmlText(ssml);
        Assert.assertEquals(speakable, queuedSpeakable);
    }

    /**
     * {@inheritDoc}
     */
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        final int id = event.getEvent();
        if (id == SynthesizedOutputEvent.OUTPUT_STARTED) {
            final OutputStartedEvent started = (OutputStartedEvent) event;
            queuedSpeakable = started.getSpeakable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
    }
}
