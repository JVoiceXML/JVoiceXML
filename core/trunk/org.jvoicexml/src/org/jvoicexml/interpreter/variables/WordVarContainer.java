/*
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
 * Component that provides a container for the words.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class WordVarContainer
        extends ScriptableObject {
    /** The raw string of words that were recognized for this interpretation. */
    private final String word;

    /**
     * The whole utterance confidence level for this interpretation from
     * 0.0-1.0.
     */
    private final float confidence;

    /**
     * Constructs a new object.
     * @param w the word.
     * @param conf the confidence level of this word.
     */
    public WordVarContainer(final String w, final float conf) {
        word = w;
        confidence = conf;

        defineProperty("word", WordVarContainer.class,
                READONLY);
        defineProperty("confidence", WordVarContainer.class,
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
    public String getWord() {
        return word;
    }

    /**
     * Retrieves the utterance.
     * @return the utterance.
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return WordVarContainer.class.getSimpleName();
    }
}
