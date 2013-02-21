/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/speakstrategy/SpeakStrategyBase.java $
 * Version: $LastChangedRevision: 2583 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mobicents.speakstrategy;

import javax.speech.synthesis.Synthesizer;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.implementation.mobicents.SSMLSpeakStrategy;
import org.jvoicexml.implementation.mobicents.SSMLSpeakStrategyFactory;
import org.jvoicexml.implementation.mobicents.MobicentsSynthesizedOutput;
import org.w3c.dom.NodeList;

/**
 * Base strategy functionality to play back a node of a SSML document via Mobicents.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2583 $
 * @since 0.5
 */
abstract class SpeakStrategyBase
        implements SSMLSpeakStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SpeakStrategyBase.class);

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
     * @param node The current node.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    protected void speakChildNodes(final MobicentsSynthesizedOutput synthesizer,
                                   final SsmlNode node)
            throws NoresourceError, BadFetchError 
    {
        LOGGER.debug(" synthesizer:"+synthesizer);
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength()
            && !synthesizer.isOutputCanceled(); i++) {
            final SsmlNode child = (SsmlNode) children.item(i);
            // Determine how the current child has to be processed and
            // speak it.
            final SSMLSpeakStrategy strategy =
                    factory.getSpeakStrategy(child);
            if (strategy != null) {
                strategy.speak(synthesizer, child);
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
    protected void waitQueueEmpty(final MobicentsSynthesizedOutput output)
        throws NoresourceError {
        final Synthesizer synthesizer = output.getSynthesizer();
        try {
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (IllegalArgumentException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }
}
