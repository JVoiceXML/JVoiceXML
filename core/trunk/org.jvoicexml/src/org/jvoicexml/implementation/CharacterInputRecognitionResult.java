/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Result of a DTMF recognition process.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
class CharacterInputRecognitionResult
        implements RecognitionResult {
    /** The recognized DTMF string. */
    private final String utterance;

    /** The last reached marker. */
    private String marker;

    /** <code>true</code> if the result is accepted. */
    private boolean accepted;

    /**
     * Constructs a new accepted result..
     * @param dtmf the recognized DTMF string.
     */
    public CharacterInputRecognitionResult(final String dtmf) {
        utterance = dtmf;
    }

    /**
     * Constructs a new object.
     * @param dtmf the recognized DTMF string.
     * @param isAccepted <code>true</code> if the result is accepted.
     * @since 0.7
     */
    public CharacterInputRecognitionResult(final String dtmf,
            final boolean isAccepted) {
        utterance = dtmf;
        accepted = isAccepted;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return marker;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * Marks the result as accepted.
     * @param isAccepted <code>true</code>  if the result is accepted.
     * @since 0.7
     */
    public void setAccepted(final boolean isAccepted) {
        accepted = isAccepted;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return !accepted;
    }

    /**
     * {@inheritDoc}
    */
    public void setMark(final String mark) {
        marker = mark;
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return 1.0f;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
        final float[] wordsConfidence = new float[1];
        wordsConfidence[0] = 1.0f;
        return wordsConfidence;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getWords() {
        final String[] words = new String[1];
        words[0] = utterance;
        return words;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getMode() {
        return ModeType.DTMF;
    }
}
