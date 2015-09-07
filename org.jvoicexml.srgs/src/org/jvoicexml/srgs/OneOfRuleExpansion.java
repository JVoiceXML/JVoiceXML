package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;

public class OneOfRuleExpansion implements RuleExpansion {
    private static final Logger LOGGER = Logger
            .getLogger(OneOfRuleExpansion.class);
    private ArrayList<RuleExpansion> subRules = new ArrayList<RuleExpansion>();
    private ExecutableSemanticInterpretation executableSI = null;
    private SemanticInterpretationBlock initialSI = null;

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSI(ExecutableSemanticInterpretation si) {
        executableSI = si;
    }

    public void addSubRule(RuleExpansion rule) {
        subRules.add(rule);
    }

    public void addInitialSI(String si) {
        if (initialSI == null)
            initialSI = new SemanticInterpretationBlock();
        initialSI.append(si);
    }

    @Override
    public MatchConsumption match(ArrayList<String> tokens, int offset) {
        if (subRules.isEmpty()) { // Not allowed per DTD, but not validating
            return new MatchConsumption(executableSI);
        }

        // Return first match, or a failure
        for (RuleExpansion rule : subRules) {
            MatchConsumption individualResult = rule.match(tokens, offset);
            if (individualResult != null) {
                if (initialSI != null)
                    individualResult.addExecutableSI(initialSI);

                individualResult.addExecutableSI(executableSI);
                return individualResult;
            }
        }

        return null;
    }

    public void dump(String pad) {
        LOGGER.debug(pad + "one-of");

        for (RuleExpansion rule : subRules) {
            rule.dump(pad + " ");
        }
        if (executableSI != null)
            executableSI.dump(pad);
    }

}
