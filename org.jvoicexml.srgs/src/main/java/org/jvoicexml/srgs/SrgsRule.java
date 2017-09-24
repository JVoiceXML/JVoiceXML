/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.srgs.sisr.Context;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
import org.jvoicexml.xml.srgs.Rule;

public class SrgsRule implements RuleExpansion {
    private static final Logger LOGGER = Logger.getLogger(SrgsRule.class);

    private String id;
    private boolean isPublic = true;

    private SemanticInterpretationBlock initialSI;
    private RuleExpansion innerRule;

    public SrgsRule(Rule rule) {
        id = rule.getId();
        final String scope = rule.getScope();
        if (scope != null && scope.equals("private")) {
            isPublic = false;
        }
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
        if (initialSI == null) {
            initialSI = new SemanticInterpretationBlock();
        }
        initialSI.append(si);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchConsumption match(List<String> tokens, int index) {
        final RuleExpansion rule = getInnerRule();
        if (rule == null) {
            return null;
        }

        final MatchConsumption result = rule.match(tokens, index);
        if (result == null) {
            return null;
        }

        // Wrap it in a new execution context and return
        final Context context = new Context(getId());
        if (getInitialSI() != null) {
            context.addExecutableContent(getInitialSI());
        }
        context.addExecutableContent(result.getExecutationCollection());
        return new MatchConsumption(result.getTokensConsumed(), context);
    }

    @Override
    public void dump(String pad) {
        LOGGER.debug(pad + toString());
        innerRule.dump(pad + " ");
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("rule(id=");
        str.append(id);
        str.append(")");
        return str.toString();
    }
    
    @Override
    public void setExecutionSemanticInterpretation(ExecutableSemanticInterpretation si) {
        LOGGER.error("setExecutionSI should never be called on a rule");
    }

    SemanticInterpretationBlock getInitialSI() {
        return initialSI;
    }

    RuleExpansion getInnerRule() {
        return innerRule;
    }

}
