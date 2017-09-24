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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;

public class ItemRuleExpansion implements RuleExpansion {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(ItemRuleExpansion.class);

    private int minRepeat = 1;
    private int maxRepeat = 1;
    private List<RuleExpansion> subRules = new java.util.ArrayList<RuleExpansion>();
    private SemanticInterpretationBlock initialSemanticInterpretation;
    private ExecutableSemanticInterpretation executableSemanticInterpretation;

    /**
     * Define minimum and maximum repeats.
     * 
     * @param min
     *            Minimum number of times token set must match
     * @param max
     *            Set to -1 for an unlimited match potential
     */
    public void setRepeat(final int min, final int max) {
        minRepeat = min;
        maxRepeat = max;
    }

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSemanticInterpretation(final ExecutableSemanticInterpretation si) {
        executableSemanticInterpretation = si;
    }

    public void appendInitialSI(String si) {
        if (initialSemanticInterpretation == null) {
            initialSemanticInterpretation = new SemanticInterpretationBlock();
        }
        initialSemanticInterpretation.append(si);
    }

    public void addSubRule(RuleExpansion rule) {
        subRules.add(rule);
    }

    List<RuleExpansion> getSubItems() {
        return subRules;
    }

    SemanticInterpretationBlock getInitialSemanticInterpretation() {
        return initialSemanticInterpretation;
    }

    int getMinRepeat() {
        return minRepeat;
    }

    int getMaxRepeat() {
        return maxRepeat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchConsumption match(final List<String> tokens, final int offset) {
        if (subRules.isEmpty()) {
            return new MatchConsumption(executableSemanticInterpretation);
        }

        MatchConsumption summationResult = new MatchConsumption();
        if (initialSemanticInterpretation != null) {
            summationResult.addExecutableSemanticInterpretation(initialSemanticInterpretation);
        }
        int matchCount = 0;
        int tokensConsumed = 0;
        while (maxRepeat == -1 || matchCount < maxRepeat) {
            MatchConsumption fullIterationResult = new MatchConsumption();
            boolean fullIterationMatched = true;

            // Check all items. All have to match for it to be successful.
            for (RuleExpansion rule : subRules) {
                MatchConsumption individualResult = rule.match(tokens, offset
                        + tokensConsumed);
                if (individualResult == null) {
                    fullIterationMatched = false;
                    break;
                }

                // This rule matched, add to the iteration collection
                fullIterationResult.add(individualResult);
                tokensConsumed += individualResult.getTokensConsumed();
            }

            // The pass failed, break out and return results, if we matched
            // enough below
            if (!fullIterationMatched) {
                break;
            }

            ++matchCount;
            summationResult.add(fullIterationResult);
        }

        if (matchCount >= minRepeat) {
            summationResult.addExecutableSemanticInterpretation(executableSemanticInterpretation);
            return summationResult;
        }
        return null;
    }

    @Override
    public void dump(final String pad) {
        LOGGER.debug(pad + "item(minRepeat=" + minRepeat + ", maxRepeat="
                + maxRepeat + ") ");
        for (RuleExpansion rule : subRules) {
            rule.dump(pad + " ");
        }
        if (executableSemanticInterpretation != null) {
            executableSemanticInterpretation.dump(pad);
        }
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("item(minRepeat=");
        str.append(minRepeat);
        str.append(", maxRepeat=");
        str.append(maxRepeat);
        str.append(")[");
        final Iterator<RuleExpansion> iterator = subRules.iterator();
        while (iterator.hasNext()) {
            final RuleExpansion rule = iterator.next();
            str.append(rule);
            if (iterator.hasNext()) {
                str.append(',');
            }
        }
        str.append("]");
        return str.toString();
    }
}
