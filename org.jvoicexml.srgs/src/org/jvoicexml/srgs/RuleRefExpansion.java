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

package org.jvoicexml.srgs;

import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;
import org.jvoicexml.srgs.sisr.GrammarContext;

public class RuleRefExpansion implements RuleExpansion {
    private static final Logger LOGGER = Logger
            .getLogger(RuleRefExpansion.class);

    private SrgsRule referencedRule = null;
    private ExecutableSemanticInterpretation executableSI = null;
    private SrgsSisrGrammar externalGrammar = null;

    public RuleRefExpansion(SrgsRule rule) {
        referencedRule = rule;
    }

    public RuleRefExpansion(SrgsSisrGrammar externalGrammar, SrgsRule rule) {
        referencedRule = rule;
        this.externalGrammar = externalGrammar;
    }

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSemanticInterpretation(ExecutableSemanticInterpretation si) {
        executableSI = si;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchConsumption match(List<String> tokens, int offset) {
        MatchConsumption individualResult = referencedRule
                .match(tokens, offset);
        if (individualResult != null) {
            if (externalGrammar != null) {
                if (individualResult.getExecutationCollection().size() != 1)
                    LOGGER.error("SrgsRule does not have one SI component");

                // Replace the current execution component with a
                // GrammarContext, which will run
                // the rule in a new grammar based context
                GrammarContext grammarContext = new GrammarContext(
                        externalGrammar, referencedRule.getId(),
                        individualResult.getExecutationCollection().get(0));
                individualResult.getExecutationCollection().clear();
                individualResult.addExecutableSI(grammarContext);
            } else {
                individualResult.addExecutableSI(executableSI);
            }
            return individualResult;
        }

        return null;
    }

    public void dump(String pad) {
        LOGGER.debug(pad + "RuleRefExpansion(" + referencedRule.getId() + ") ");
        if (executableSI != null)
            executableSI.dump(pad);
    }
}
