/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.test;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SemanticInterpretation;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Demo implementation of a {@link RecognitionResult}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 */
public final class DummyRecognitionResult
        implements RecognitionResult {
    /** The semantic interpretation of the utterance. */
    private SemanticInterpretation interpretation;

    /** The confidence of the result. */
    private float confidence;

    /** The current mark. */
    private String mark;

    /** The mode. */
    private ModeType mode;

    /** The utterance. */
    private String utterance;

    /** Result accepted. */
    private boolean accepted;

    /** Result rejected. */
    private boolean rejected;

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    public void setMark(final String name) {
        mark = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return mark;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getMode() {
        return mode;
    }

    /**
     * Sets the utterance.
     * @param utt new value for the utterance.
     */
    public void setUtterance(final String utt) {
        utterance = utt;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Sets this result accept status flag.
     * @param value new value for accepted.
     */
    public void setAccepted(final boolean value) {
        accepted = value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * Sets the confidence.
     *
     * @param conf
     *            the confidence to set.
     */
    public void setConfidence(final float conf) {
        confidence = conf;
    }

    /**
     * Sets the mode.
     *
     * @param newMode
     *            the mode to set
     */
    public void setMode(final String newMode) {
        mode = ModeType.valueOf(newMode);
    }

    /**
     * Sets the mode.
     *
     * @param newMode
     *            the mode to set
     */
    public void setMode(final ModeType newMode) {
        mode = newMode;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getWords() {
        if (utterance == null) {
            return null;
        }
        return utterance.split(" ");
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
        String[] words = getWords();
        if (words == null) {
            return null;
        }
        float[] confidences = new float[words.length];
        for (int i = 0; i < confidences.length; i++) {
            confidences[i] = confidence;
        }
        return confidences;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SemanticInterpretation getSemanticInterpretation() {
        return interpretation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSemanticInterpretation(final SemanticInterpretation value) {
        interpretation = value;
    }
}
