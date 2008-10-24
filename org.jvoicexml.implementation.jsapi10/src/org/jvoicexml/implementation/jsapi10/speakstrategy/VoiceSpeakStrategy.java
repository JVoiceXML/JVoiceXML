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
import javax.speech.synthesis.Voice;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.GenderType;

/**
 * SSML strategy to play back a <code>&lt;voice&gt;</code> node.
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
final class VoiceSpeakStrategy extends SpeakStrategyBase {
    /**
     * {@inheritDoc}
     */
    public void speak(final SynthesizedOutput output,
            final AudioFileOutput file, final SsmlNode node)
        throws NoresourceError, BadFetchError {
        final Jsapi10SynthesizedOutput syn = (Jsapi10SynthesizedOutput) output;
        final Synthesizer synthesizer = syn.getSynthesizer();
        final SynthesizerProperties properties =
            synthesizer.getSynthesizerProperties();
        final Voice voice = properties.getVoice();
        final org.jvoicexml.xml.ssml.Voice voiceNode =
            (org.jvoicexml.xml.ssml.Voice) node;
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

        final Voice newVoice = new Voice(name,
                gender, age, null);
        try {
            properties.setVoice(newVoice);
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        speakChildNodes(output, file, node);

        // Restore the old voice.
        try {
            properties.setVoice(voice);
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

}
