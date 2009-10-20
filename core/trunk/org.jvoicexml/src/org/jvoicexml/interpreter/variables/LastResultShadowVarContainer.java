/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.variables;

import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard application variables.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
@SuppressWarnings("serial")
public final class LastResultShadowVarContainer
        extends ScriptableObject {
    /** The raw string of words that were recognized for this interpretation. */
    private final String utterance;

    /**
     * The whole utterance confidence level for this interpretation from
     * 0.0-1.0.
     */
    private final float confidence;

    /**
     * For this interpretation,the mode in which user input was provided:
     * dtmf or voice.
     */
    private final String inputmode;

    /** The utterance split in words. */
    private final WordVarContainer[] words;

    /** The semantic interpretation. */
    private final Object interpretation;

    /**
     * Constructs a new object.
     * @param utt the utterance.
     * @param conf the confidence level.
     * @param mode the input mode.
     * @param w the words.
     * @param wordsConfidence the confidence of each word.
     * @param inter the semantic interpretation
     */
    public LastResultShadowVarContainer(final String utt, final float conf,
                                        final String mode, final String[] w,
                                        final float[] wordsConfidence,
                                        final Object inter) {
        utterance = utt;
        confidence = conf;
        inputmode = mode;
        interpretation = inter;

        if (w == null) {
            words = null;
        } else {
            words = new WordVarContainer[w.length];
            for (int i = 0; i < words.length; ++i) {
                words[i] = new WordVarContainer(w[i], wordsConfidence[i]);
            }
        }

        defineProperty("utterance", LastResultShadowVarContainer.class,
                READONLY);
        defineProperty("confidence", LastResultShadowVarContainer.class,
                READONLY);
        defineProperty("inputmode", LastResultShadowVarContainer.class,
                READONLY);
        defineProperty("interpretation", LastResultShadowVarContainer.class,
                READONLY);
        defineProperty("words", LastResultShadowVarContainer.class,
                READONLY);
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * Retrieves the utterance.
     * @return the utterance.
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * Retrieves the utterance.
     * @return the utterance.
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * Retrieves the utterance.
     * @return the utterance.
     */
    public String getInputmode() {
        return inputmode;
    }

    /**
     * Retrieves the words.
     * @return the vector of words.
     * @since 0.7
     */
    public WordVarContainer[] getWords() {
        return words;
    }

    /**
     * Retrieves the semantic interpretation.
     * @return the semantic interpretation
     * @since 0.7.2
     */
    public Object getInterpretation() {
        return interpretation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return LastResultShadowVarContainer.class.getSimpleName();
    }
}
