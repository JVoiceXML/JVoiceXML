/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
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

package org.jvoicexml;

/**
 * This variable container holds information about the last recognition to occur
 * within this application.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class LastResult {

    /** The raw string of words that were recognized for this interpretation. */
    private final String utterance;

    /**
     * The whole utterance confidence level for this interpretation from
     * 0.0-1.0.
     */
    private final float confidence;

    /**
     * For this interpretation,the mode in which user input was provided: dtmf
     * or voice.
     */
    private final String inputmode;

    /** The semantic interpretation as a JSON formatted string. */
    private final Object interpretation;

    /**
     * Constructs a new object.
     * 
     * @param utt
     *            the utterance.
     * @param conf
     *            the confidence level.
     * @param mode
     *            the input mode.
     * @param w
     *            the words.
     * @param wordsConfidence
     *            the confidence of each word, the size of this array must match
     *            the size of the word array
     * @param inter
     *            the semantic interpretation
     */
    public LastResult(final String utt, final float conf, final String mode,
            final Object inter) {
        utterance = utt;
        confidence = conf;
        inputmode = mode;
        interpretation = inter;
    }

    /**
     * Constructs a new object.
     */
    public LastResult() {
        utterance = null;
        confidence = 0;
        inputmode = null;
        interpretation = null;
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getInputmode() {
        return inputmode;
    }

    /**
     * Retrieves the semantic interpretation.
     * 
     * @return the semantic interpretation
     */
    public Object getInterpretation() {
        return interpretation;
    }
}
