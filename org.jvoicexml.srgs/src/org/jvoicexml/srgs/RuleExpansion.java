package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.jvoicexml.srgs.sisr.ExecutableSI;

public interface RuleExpansion {
    MatchConsumption match(ArrayList<String> tokens, int offset);

    public void dump(String pad);

    public void setExecutionSI(ExecutableSI si);
}
