/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.kinect;

import java.util.List;

import org.jvoicexml.xml.srgs.ModeType;

/**
 * A recognition result as it comes from the Kinect.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 *
 */
public class KinectRecognitionResult
    implements org.jvoicexml.RecognitionResult {
    /** The rrecieved recognition result. */
    private final SmlInterpretationExtractor extractor;

    private String mark;

    /**
     * Constructs a new object.
     */
    public KinectRecognitionResult(
            final SmlInterpretationExtractor smlextrator) {
        extractor = smlextrator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation() {
        final List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        final String[] tags;
        int i = 0;
        final String utteranceTag = extractor.getUtteranceTag();
        final String utterance = getUtterance();
        if (utteranceTag.isEmpty()
                || utteranceTag.equals(utterance)) {
            tags = new String[interpretations.size()];
        } else {
            tags = new String[interpretations.size() + 1];
            tags[i] = utteranceTag;
            i++;
        }
        for (SmlInterpretation interpretation : interpretations) {
            final String tag = interpretation.getTag();
            final String value = interpretation.getValue();
            
            // SRGS-tags like <tag>FOO</tag>
            tags[i] = tag; 
            
            // SRGS-tags like <tag>FOO="bar"</tag>
            if (!(value != null) && !value.isEmpty()) {
                    tags[i] += "=" + value; 
            }
            i++;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUtterance() {
        return extractor.getUtterance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getWords() {
        final String utterance = getUtterance();
        return utterance.split(" ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getConfidence() {
        return extractor.getConfidence();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float[] getWordsConfidence() {
        final float confidence = getConfidence();
        final String[] words = getWords();
        final float[] confidences = new float[words.length];
        for (int i=0; i< confidences.length; i++) {
            confidences[i] = confidence;
        }
        return confidences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getMode() {
        return ModeType.VOICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccepted() {
        final String utterance = getUtterance();
        // Check if there is only garbage recognized
        return !utterance.equals("...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRejected() {
        return !isAccepted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMark(String value) {
        mark = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMark() {
        return mark;
    }
}
