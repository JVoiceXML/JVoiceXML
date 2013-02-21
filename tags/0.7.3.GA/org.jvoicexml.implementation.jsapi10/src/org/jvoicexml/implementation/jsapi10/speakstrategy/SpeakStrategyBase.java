/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.speakstrategy;

import javax.speech.synthesis.Synthesizer;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.SSMLSpeakStrategy;
import org.jvoicexml.implementation.jsapi10.SSMLSpeakStrategyFactory;
import org.jvoicexml.xml.SsmlNode;
import org.w3c.dom.NodeList;

/**
 * Base strategy functionality to play back a node of a SSML document via JSAPI.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
abstract class SpeakStrategyBase
        implements SSMLSpeakStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SpeakStrategyBase.class);

    /** Delay to wait for changes in the synthesizer or audio file output. */
    private static final int SLEEP_DELAY = 100;

    /** The factory to produce new speak strategies. */
    private SSMLSpeakStrategyFactory factory;

    /**
     * Constructs a new object.
     */
    public SpeakStrategyBase() {
    }

    /**
     * Sets the factory to produce new speak strategies.
     * @param strategyFactory the factory.
     */
    public void setSSMLSpeakStrategyFactory(
            final SSMLSpeakStrategyFactory strategyFactory) {
        factory = strategyFactory;
    }

    /**
     * Calls the speak method for all child nodes of the given node.
     * @param synthesizer The synthesized output to use.
     * @param file the audio file output.
     * @param node The current node.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    protected void speakChildNodes(final Jsapi10SynthesizedOutput synthesizer,
                                   final AudioFileOutput file,
                                   final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength()
            && !synthesizer.isOutputCanceled(); i++) {
            final SsmlNode child = (SsmlNode) children.item(i);
            // Determine how the current child has to be processed and
            // speak it.
            final SSMLSpeakStrategy strategy =
                    factory.getSpeakStrategy(child);
            if (strategy != null) {
                strategy.speak(synthesizer, file, child);
            }
        }
        if (LOGGER.isDebugEnabled() && synthesizer.isOutputCanceled()) {
            LOGGER.debug("output of current SSML cancelled");
        }
    }

    /**
     * Waits until the input queue of the synthesizer is empty.
     * <p>
     * This method uses the encapsulated {@link Synthesizer} to look at the
     * current queue regardless of what is coming from the voice browser.
     * </p>
     * @param output the current synthesized output
     * @throws NoresourceError
     *         error waiting for an empty queue
     * @since 0.7.2
     */
    protected void waitQueueEmpty(final Jsapi10SynthesizedOutput output)
        throws NoresourceError {
        final Synthesizer synthesizer = output.getSynthesizer();
        try {
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (IllegalArgumentException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        final AudioFileOutput audioFileOutput = output.getAudioFileOutput();
        if (audioFileOutput != null) {
            while (audioFileOutput.isBusy()) {
                try {
                    Thread.sleep(SLEEP_DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
