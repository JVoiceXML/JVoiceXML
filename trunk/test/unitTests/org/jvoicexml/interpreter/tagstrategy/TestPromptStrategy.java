/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SystemOutputListener;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Prompt;

/**
 * Test case for {@link PromptStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class TestPromptStrategy extends TagStrategyTestBase
    implements SystemOutputListener {
    /** The queued speakable. */
    private SpeakableText queuedSpeakable;

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    public void testExecute() throws Exception {
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText(
                "When you hear the name of the movie you want, just say it.");
        final Audio audio1 = prompt.appendChild(Audio.class);
        audio1.setSrc("godfather.wav");
        audio1.addText("the godfather");
        final Audio audio2 = prompt.appendChild(Audio.class);
        audio2.setSrc("high_fidelity.wav");
        audio2.addText("high fidelity");
        final Audio audio3 = prompt.appendChild(Audio.class);
        audio3.setSrc("raiders.wav");
        audio3.addText("raiders of the lost ark");

        setSystemOutputListener(this);
        final PromptStrategy strategy = new PromptStrategy();
        try {
            executeTagStrategy(prompt, strategy);
        } catch (JVoiceXMLEvent e) {
            fail(e.getMessage());
        }

        final SsmlDocument ssml = new SsmlDocument();
        final Speak speak = ssml.getSpeak();
        speak.addText(
                "When you hear the name of the movie you want, just say it.");
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
        assertEquals(speakable, queuedSpeakable);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.PromptStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    public void testExecuteCond() throws Exception {
        final Block block = createBlock();
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("demo");
        prompt.setCond("3 == 4");

        setSystemOutputListener(this);
        final PromptStrategy strategy = new PromptStrategy();
        try {
            executeTagStrategy(prompt, strategy);
        } catch (JVoiceXMLEvent e) {
            fail(e.getMessage());
        }

        assertNull("wrong evaluation of the cond attribute", queuedSpeakable);
    }

    /**
     * {@inheritDoc}
     */
    public void markerReached(final String mark) {
    }

    /**
     * {@inheritDoc}
     */
    public void outputEnded(final SpeakableText speakable) {
    }

    /**
     * {@inheritDoc}
     */
    public void outputQueueEmpty() {
    }

    /**
     * {@inheritDoc}
     */
    public void outputStarted(final SpeakableText speakable) {
        queuedSpeakable = speakable;
    }
}
