/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.GenderType;

/**
 * SSML strategy to play back a <code>&lt;voice&gt;</code> node.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class VoiceSpeakStrategy extends SpeakStrategyBase {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(VoiceSpeakStrategy.class);
    /**
     * {@inheritDoc}
     */
    public void speak(final Jsapi10SynthesizedOutput output,
            final SsmlNode node)
        throws NoresourceError, BadFetchError {
        final Jsapi10SynthesizedOutput syn = output;
        final Synthesizer synthesizer = syn.getSynthesizer();
        final SynthesizerProperties properties =
            synthesizer.getSynthesizerProperties();
        final Voice voice = properties.getVoice();
        final org.jvoicexml.xml.ssml.Voice voiceNode =
            (org.jvoicexml.xml.ssml.Voice) node;
        final String name = voiceNode.getName();

        final Voice newVoice = createVoice(voiceNode);
        if (!hasVoice(synthesizer, newVoice)) {
            throw new NoresourceError(
                    "The synthesizer does not support the voice '" + name
                    + "'!");
        }
        if (!voice.match(newVoice)) { 
            waitQueueEmpty(output);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("changing voice to '" + name + "'");
            }
            final MultiPropertyChangeListener listener =
                new MultiPropertyChangeListener();
            properties.addPropertyChangeListener(listener);
            try {
                listener.addProperty(MultiPropertyChangeListener.VOICE);
                properties.setVoice(newVoice);
                listener.waitChanged();
            } catch (InterruptedException e) {
                return;
            } catch (PropertyVetoException e) {
                throw new NoresourceError(e.getMessage(), e);
            } finally {
                properties.removePropertyChangeListener(listener);
            }
        }

        speakChildNodes(output, node);

        // Restore the old voice.
        if (!voice.match(newVoice)) { 
            waitQueueEmpty(output);
            final MultiPropertyChangeListener listener =
                new MultiPropertyChangeListener();
            properties.addPropertyChangeListener(listener);
            try {
                listener.addProperty(MultiPropertyChangeListener.VOICE);
                properties.setVoice(voice);
                listener.waitChanged();
            } catch (InterruptedException e) {
                return;
            } catch (PropertyVetoException e) {
                throw new NoresourceError(e.getMessage(), e);
            } finally {
                properties.removePropertyChangeListener(listener);
            }
        }
    }

    /**
     * Creates a new voice using the properties of the given voice node.
     * @param voiceNode the voice node
     * @return created voice
     * @since 0.7.2
     */
    private Voice createVoice(final org.jvoicexml.xml.ssml.Voice voiceNode) {
        final String name = voiceNode.getName();
        int age = voiceNode.getAgeAsInt();
        if (age < 0) {
            age = Voice.AGE_DONT_CARE;
        }
        final GenderType genderType = voiceNode.getGender();
        final int gender;
        if (genderType == GenderType.MALE) {
            gender = Voice.GENDER_MALE;
        } else if (genderType == GenderType.FEMALE) {
            gender = Voice.GENDER_FEMALE;
        } else if (genderType == GenderType.NEUTRAL) {
            gender = Voice.GENDER_NEUTRAL;
        } else {
            gender = Voice.GENDER_DONT_CARE;
        }

        return new Voice(name, gender, age, null);
    }

    /**
     * Checks if the synthesizer supports the requested voice.
     * @param synthesizer the synthesizer
     * @param requestedVoice the requested voice.
     * @return <code>true</code> if the synthesizer supports the
     *          requested voice.
     * @since 0.7.2
     */
    private boolean hasVoice(final Synthesizer synthesizer,
            final Voice requestedVoice) {
        final SynthesizerModeDesc desc =
            (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
        final Voice[] voices = desc.getVoices();
        for (Voice voice : voices) {
            if (voice.match(requestedVoice)) {
                return true;
            }
        }
        return false;
    }
}

