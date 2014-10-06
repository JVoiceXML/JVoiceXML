/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.LastResult;
import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard application variables.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class LastResultShadowVarContainer extends ScriptableObject {
    /** The serial version UID. */
    private static final long serialVersionUID = 1801108654062869631L;

    /** The encapsulated last result. */
    private final LastResult lastresult;

    /** The utterance split in words. */
    private final WordVarContainer[] words;

    /**
     * Constructs a new object.
     * 
     * @param w
     *            the words.
     * @param wordsConfidence
     *            the confidence of each word, the size of this array must match
     *            the size of the word array
     */
    public LastResultShadowVarContainer(final LastResult result,
            final String[] w, final float[] wordsConfidence) {
        lastresult = result;

        if (w == null) {
            words = null;
        } else {
            if ((wordsConfidence == null)
                    || (w.length != wordsConfidence.length)) {
                throw new IllegalArgumentException(
                        "Word length does not match word confidence length!");
            }
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
        defineProperty("words", LastResultShadowVarContainer.class, READONLY);
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getUtterance() {
        return lastresult.getUtterance();
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public float getConfidence() {
        return lastresult.getConfidence();
    }

    /**
     * Retrieves the utterance.
     * 
     * @return the utterance.
     */
    public String getInputmode() {
        return lastresult.getInputmode();
    }

    /**
     * Retrieves the words.
     * 
     * @return the vector of words.
     * @since 0.7
     */
    public WordVarContainer[] getWords() {
        return words;
    }

    /**
     * Retrieves the semantic interpretation.
     * 
     * @return the semantic interpretation
     * @since 0.7.2
     */
    public Object getInterpretation() {
        return lastresult.getInterpretation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return LastResultShadowVarContainer.class.getSimpleName();
    }
}
