/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.grammar;

import java.net.URI;

/**
 * A grammar evaluator processes a given utterance into a recognition result.
 * Usually, one {@link GrammarEvaluator} is responsible to evaluate input for
 * the associated active grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.8
 */
public interface GrammarEvaluator {
    /**
     * Retrieves the base URI of this grammar.
     * @return the URI
     */
    URI getURI();
    
    /**
     * Processes the given utterance and retrieves the corresponding semantic
     * interpretation. If there is no semantic interpretation but the utterances
     * is considered a valid input, the utterance is returned as semantic
     * interpretation. 
     * <p>
     * This method is also called to check if the associated grammar accepts the
     * given utterance.
     * </p>
     * @param utterance the current utterance
     * @return the determined semantic interpretation, or {@code null} if
     *         the utterance is not valid
     */
    Object getSemanticInterpretation(final String utterance);
}
