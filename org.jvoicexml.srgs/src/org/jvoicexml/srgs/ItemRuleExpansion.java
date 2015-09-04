package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSI;
import org.jvoicexml.srgs.sisr.SIBlock;

public class ItemRuleExpansion implements RuleExpansion {
    private static final Logger LOGGER = Logger
            .getLogger(ItemRuleExpansion.class);

    private int minRepeat = 1, maxRepeat = 1;
    private ArrayList<RuleExpansion> subRules = new ArrayList<RuleExpansion>();
    private SIBlock initialSI = null;
    private ExecutableSI executableSI = null;

    /**
     * Define minimum and maximum repeats
     * 
     * @param min
     *            Minimum number of times token set must match
     * @param max
     *            Set to -1 for an unlimited match potential
     */
    public void setRepeat(int min, int max) {
        minRepeat = min;
        maxRepeat = max;
    }

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSI(ExecutableSI si) {
        executableSI = si;
    }

    public void appendInitialSI(String si) {
        if (initialSI == null)
            initialSI = new SIBlock();
        initialSI.append(si);
    }

    public void addSubRule(RuleExpansion rule) {
        subRules.add(rule);
    }

    ArrayList<RuleExpansion> getSubItems() {
        return subRules;
    }

    SIBlock getInitialSI() {
        return initialSI;
    }

    int getMinRepeat() {
        return minRepeat;
    }

    int getMaxRepeat() {
        return maxRepeat;
    }

    @Override
    public MatchConsumption match(ArrayList<String> tokens, int offset) {
        if (subRules.isEmpty()) {
            return new MatchConsumption(executableSI);
        }

        MatchConsumption summationResult = new MatchConsumption();
        if (initialSI != null)
            summationResult.addExecutableSI(initialSI);
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
            summationResult.addExecutableSI(executableSI);
            return summationResult;
        }
        return null;
    }

    @Override
    public void dump(String pad) {
        LOGGER.debug(pad + "item(minRepeat=" + minRepeat + ", maxRepeat="
                + maxRepeat + ") ");
        for (RuleExpansion rule : subRules) {
            rule.dump(pad + " ");
        }
        if (executableSI != null)
            executableSI.dump(pad);
    }

}
