/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/LastResultShadowVarContainer.java $
 * Version: $LastChangedRevision: 408 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard application variables.
 *
 * @author Dirk Schnelle
 * @version $Revision: 408 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
@SuppressWarnings("serial")
public final class WordShadowVarContainer
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
    public WordShadowVarContainer(final String w, final float conf) {
        word = w;
        confidence = conf;

        defineProperty("word", WordShadowVarContainer.class,
                READONLY);
        defineProperty("confidence", WordShadowVarContainer.class,
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
        return WordShadowVarContainer.class.getSimpleName();
    }
}
