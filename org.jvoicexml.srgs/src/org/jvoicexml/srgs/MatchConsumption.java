package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Returned when a match of a rule expansion is successful. It informs the
 * caller how many tokens were actually consumed and the resulting SI that needs
 * to be executed to generate a result.
 * 
 * @author Jim Rush
 *
 */
public class MatchConsumption {
    private static final Logger LOGGER = Logger
            .getLogger(MatchConsumption.class);
    private int tokensConsumed = 0;
    private ArrayList<String> tokens = new ArrayList<String>();
    private ExecutableSemanticInterpretation globalExecutation = null;
    private ArrayList<ExecutableSemanticInterpretation> executationCollection = new ArrayList<ExecutableSemanticInterpretation>();

    public MatchConsumption() {
    }

    public MatchConsumption(int tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public MatchConsumption(int tokensConsumed,
            ArrayList<ExecutableSemanticInterpretation> executationCollection) {
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

    public ArrayList<ExecutableSemanticInterpretation> getExecutationCollection() {
        return executationCollection;
    }

    public void setExecutationCollection(ArrayList<ExecutableSemanticInterpretation> newValue) {
        executationCollection = newValue;
    }

    public void addExecutableSI(ExecutableSemanticInterpretation si) {
        if (si != null) {
            executationCollection.add(si);
        }
    }

    public void setGlobalExecutableSI(ExecutableSemanticInterpretation si) {
        globalExecutation = si;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void addTokens(ArrayList<String> tokens) {
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

    public void addExecutableSI(ArrayList<ExecutableSemanticInterpretation> si) {
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
        Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_DEFAULT);
        Scriptable globalScope = context.initStandardObjects();

        if (globalExecutation != null)
            globalExecutation.execute(context, globalScope);
        ((ScriptableObject) globalScope).sealObject();

        // Set up working scope - note out initialized at this level, but
        // shouldn't be used
        Scriptable workingScope = context.newObject(globalScope);
        context.evaluateString(
                workingScope,
                "var out=new Object();\nvar rules=new Object();\n"
                        + "var meta={current: function() {return {text:'', score:1.0}}};\n",
                "SISR init from MatchConsumer", 0, null);

        if (executationCollection.size() != 1)
            LOGGER.error("Execution collection was not 1: "
                    + executationCollection.size());

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
