package org.jvoicexml.srgs;

import java.util.ArrayList;

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
    public void setExecutionSI(ExecutableSemanticInterpretation si) {
        executableSI = si;
    }

    @Override
    public MatchConsumption match(ArrayList<String> tokens, int offset) {
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
