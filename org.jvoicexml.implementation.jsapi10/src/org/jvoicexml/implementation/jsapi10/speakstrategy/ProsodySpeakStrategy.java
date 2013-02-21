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

package org.jvoicexml.implementation.jsapi10.speakstrategy;

import java.beans.PropertyVetoException;

import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerProperties;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.Prosody;

/**
 * SSML strategy to play back a <code>&lt;prosody&gt;</code> node.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class ProsodySpeakStrategy extends SpeakStrategyBase {
    /**
     * {@inheritDoc}
     */
    public void speak(final SynthesizedOutput output,
            final AudioFileOutput file, final SsmlNode node)
        throws NoresourceError, BadFetchError {
        final Prosody prosody = (Prosody) node;
        final Jsapi10SynthesizedOutput syn = (Jsapi10SynthesizedOutput) output;
        final Synthesizer synthesizer = syn.getSynthesizer();
        final SynthesizerProperties properties =
            synthesizer.getSynthesizerProperties();
        final SynthesizerProperties oldProperties =
            new JVoiceXmlSynthesizerProperties(properties);
        final SynthesizerProperties newProperties =
            new JVoiceXmlSynthesizerProperties(properties);
        try {
            if (prosody.getRate() != null) {
                newProperties.setSpeakingRate(
                        (properties.getSpeakingRate() / 100.0f)
                        * prosody.getRateFloat());
            }
            if (prosody.getPitch() != null) {
                newProperties.setPitch(prosody.getPitchFloat());
            }
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        changeProperties(output, properties, newProperties);

        // TODO evaluate the remaining attributes.
        speakChildNodes(output, file, node);
        changeProperties(output, properties, oldProperties);
    }

    /**
     * Changes the properties of the synthesizer and waits until they are
     * applied. 
     * @param output the output to use.
     * @param properties current synthesizer properties
     * @param newProperties the properties to apply
     * @throws NoresourceError
     *         error accessing the synthesizer
     * @since 0.7.2
     */
    private void changeProperties(
            final SynthesizedOutput output,
            final SynthesizerProperties properties,
            final SynthesizerProperties newProperties)
            throws NoresourceError {
        waitQueueEmpty(output);
        MultiPropertyChangeListener listener = null;
        try {
            listener = new MultiPropertyChangeListener();
            properties.addPropertyChangeListener(listener);
            if (properties.getSpeakingRate()
                    != newProperties.getSpeakingRate()) {
                listener.addProperty(MultiPropertyChangeListener.SPEAKING_RATE);
                properties.setSpeakingRate(newProperties.getSpeakingRate());
            }
            if (properties.getPitch() != newProperties.getPitch()) {
                listener.addProperty(MultiPropertyChangeListener.PITCH);
                properties.setPitch(newProperties.getPitch());
            }
            listener.waitChanged();
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            return;
        } finally {
            properties.removePropertyChangeListener(listener);
        }
    }

}
