package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;

public interface RuleExpansion {
    MatchConsumption match(ArrayList<String> tokens, int offset);

    void setExecutionSI(ExecutableSemanticInterpretation si);

    void dump(String pad);
}
