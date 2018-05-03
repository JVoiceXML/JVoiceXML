/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs;

import java.util.List;

import org.jvoicexml.srgs.sisr.Context;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
import org.jvoicexml.xml.srgs.Ruleref;

/**
 * Speial rule GARBAGE as defined in https://www.w3.org/TR/speech-grammar/#S2.2.3
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 *
 */
class GarbageRule extends SrgsRule {
    /**
     * Constructs a new object.
     */
    public GarbageRule() {
        super(Ruleref.SPECIAL_VALUE_GARBAGE);
    }
    
    /**
     * @see SrgsRule#match(List, int)
     */
    @Override
    public MatchConsumption match(List<String> tokens, int index) {
        int tokenCount = tokens.size();

        if (tokenCount == 0) {
            return new MatchConsumption(); // empty match
        }

        // If there aren't enough tokens left to match, short circuit the check
        if (tokenCount > tokens.size() - index) {
            return null;
        }

        final MatchConsumption result = new MatchConsumption(tokenCount);
        // Wrap it in a new execution context and return
        final Context context = new Context(getId());
        if (getInitialSemanticInterpretation() != null) {
            final SemanticInterpretationBlock interpretation =
                    getInitialSemanticInterpretation();
            context.addExecutableContent(interpretation);
        }
        context.addExecutableContent(result.getExecutationCollection());
        return new MatchConsumption(result.getTokensConsumed(), context);
    }
}
