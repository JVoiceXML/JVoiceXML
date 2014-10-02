/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 4242 $
 * Date:    $Date: 2014-09-03 11:42:42 +0200 (Wed, 03 Sep 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * An external recognition result that was filled by some other modality
 * component.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class MMIRecognitionResult implements RecognitionResult {
    /** The semantic interpretation of the utterance. */
    private final Object interpretation;

    /** The received utterance. */
    private final String utterance;
    
    /** The confidence of the interpretation. */
    private final float confidence;

    /**
     * Constructs a new object.
     * @param utt the received utterance
     * @param semanticInterpretation the semantic interpretation of the utterance
     * @param conf the confidence of the interpretation
     */
    public MMIRecognitionResult(final String utt,
            final Object semanticInterpretation, final float conf) {
        utterance = utt;
        interpretation = semanticInterpretation;
        confidence = conf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation() {
        return interpretation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUtterance() {
        return utterance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getWords() {
        if (utterance == null) {
            return null;
        }
        return utterance.split(" ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getConfidence() {
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float[] getWordsConfidence() {
        final String[] words = getWords();
        final float[] confidences = new float[words.length];
        for (int i = 0; i < confidences.length; i++) {
            confidences[i] = confidence;
        }
        return confidences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getMode() {
        return ModeType.EXTERNAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccepted() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMark(final String mark) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMark() {
        return null;
    }
}
