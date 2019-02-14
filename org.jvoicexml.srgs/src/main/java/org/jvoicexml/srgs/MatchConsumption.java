/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Returned when a match of a rule expansion is successful. It informs the
 * caller how many tokens were actually consumed and the resulting SI that needs
 * to be executed to generate a result.
 * 
 * @author Jim Rush
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class MatchConsumption {
    private static final Logger LOGGER = Logger
            .getLogger(MatchConsumption.class);
    private int tokensConsumed;
    private List<String> tokens = new java.util.ArrayList<String>();
    private ExecutableSemanticInterpretation globalExecutation;
    private List<ExecutableSemanticInterpretation> executationCollection =
            new java.util.ArrayList<ExecutableSemanticInterpretation>();

    static {
        if (!ContextFactory.hasExplicitGlobal()) {
            // Initialize GlobalFactory with custom factory
            final ContextFactory factory = new SrgsContextFactory();
            ContextFactory.initGlobal(factory);
        }
    }

    /**
     * Constructs a new object.
     */
    public MatchConsumption() {
    }

    public MatchConsumption(int tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public MatchConsumption(int tokensConsumed,
            List<ExecutableSemanticInterpretation> executationCollection) {
        this.tokensConsumed = tokensConsumed;
        this.executationCollection = executationCollection;
    }

    public MatchConsumption(int tokensConsumed, ExecutableSemanticInterpretation si) {
        this.tokensConsumed = tokensConsumed;
        this.executationCollection.add(si);
    }

    public MatchConsumption(ExecutableSemanticInterpretation executableSI) {
        executationCollection.add(executableSI);
    }

    public int getTokensConsumed() {
        return tokensConsumed;
    }

    public void setTokensConsumed(int tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public List<ExecutableSemanticInterpretation> getExecutationCollection() {
        return executationCollection;
    }

    public void setExecutationCollection(List<ExecutableSemanticInterpretation> newValue) {
        executationCollection = newValue;
    }

    public void addExecutableSemanticInterpretation(ExecutableSemanticInterpretation si) {
        if (si != null) {
            executationCollection.add(si);
        }
    }

    public void setGlobalExecutableSemanticInterpretation(ExecutableSemanticInterpretation si) {
        globalExecutation = si;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void addTokens(List<String> tokens) {
        this.tokens.addAll(tokens);
    }

    public void addToken(String token) {
        this.tokens.add(token);
    }

    // Used to add a match result to an existing collection
    public void add(MatchConsumption additionalResult) {
        tokensConsumed += additionalResult.getTokensConsumed();
        tokens.addAll(additionalResult.getTokens());
        executationCollection.addAll(additionalResult
                .getExecutationCollection());
    }

    public void addExecutableSI(List<ExecutableSemanticInterpretation> si) {
        executationCollection.addAll(si);
    }

    public void dump() {
        dump(true);
    }

    public void dump(boolean dumpSI) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : tokens) {
            if (first)
                first = false;
            else
                sb.append(' ');
            sb.append(s);
        }

        LOGGER.debug("MatchConsumption(tokensConsumed=" + tokensConsumed
                + ", tokens=" + sb + ")");
        if (dumpSI) {
            for (ExecutableSemanticInterpretation si : executationCollection) {
                si.dump("");
            }
        }
    }

    public Object executeSisr() {
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_DEFAULT);
        final Scriptable globalScope = context.initStandardObjects();

        if (globalExecutation != null) {
            globalExecutation.execute(context, globalScope);
        }
        final ScriptableObject scriptable = (ScriptableObject) globalScope;
        scriptable.sealObject();

        // Set up working scope - note out initialized at this level, but
        // shouldn't be used
        Scriptable workingScope = context.newObject(globalScope);
        context.evaluateString(
                workingScope,
                "var out=new Object();\nvar rules=new Object();\n"
                        + "var meta={current: function() {return {text:'', score:1.0};}};\n",
                "SISR init from MatchConsumer", 0, null);

        if (executationCollection.size() != 1) {
            LOGGER.error("Execution collection was not 1: "
                    + executationCollection.size());
        }

        // At the top level, there should only be one item, a context for the
        // rule being fired.
        for (ExecutableSemanticInterpretation si : executationCollection) {
            si.execute(context, workingScope);
        }

        // Since the last item was a rule, we can return rules.latest
        return context.evaluateString(workingScope, "rules.latest();",
                "SISR from MatchConsumer", 0, null);
    }

}
