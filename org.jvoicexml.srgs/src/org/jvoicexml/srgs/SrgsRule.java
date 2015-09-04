package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.Context;
import org.jvoicexml.srgs.sisr.ExecutableSI;
import org.jvoicexml.srgs.sisr.SIBlock;
import org.jvoicexml.xml.srgs.Rule;

public class SrgsRule implements RuleExpansion {
    private static final Logger LOGGER = Logger.getLogger(SrgsRule.class);

    private String id;
    private boolean isPublic = true;

    private SIBlock initialSI = null;
    private RuleExpansion innerRule;

    public SrgsRule(Rule rule) {
        id = rule.getId();

        String scope = rule.getScope();
        if (scope != null && scope.equals("private"))
            isPublic = false;
    }

    public String getId() {
        return id;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setRule(RuleExpansion rule) {
        innerRule = rule;
    }

    public void addInitialSI(String si) {
        if (initialSI == null)
            initialSI = new SIBlock();
        initialSI.append(si);
    }

    public MatchConsumption match(ArrayList<String> tokens, int index) {
        if (getInnerRule() == null)
            return null;

        MatchConsumption result = getInnerRule().match(tokens, index);
        if (result == null)
            return null;

        // Wrap it in a new execution context and return
        Context context = new Context(getId());
        if (getInitialSI() != null)
            context.addExecutableContent(getInitialSI());
        context.addExecutableContent(result.getExecutationCollection());
        return new MatchConsumption(result.getTokensConsumed(), context);
    }

    @Override
    public void dump(String pad) {
        LOGGER.debug(pad + "rule(id=" + id + ")");
        innerRule.dump(pad + " ");
    }

    @Override
    public void setExecutionSI(ExecutableSI si) {
        LOGGER.error("setExecutionSI should never be called on a rule");
    }

    SIBlock getInitialSI() {
        return initialSI;
    }

    RuleExpansion getInnerRule() {
        return innerRule;
    }

}
