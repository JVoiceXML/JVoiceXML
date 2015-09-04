package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.ExecutableSI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Returned when a successful match of a rule expansion is successful. It
 * informs the caller how many tokens were actually consumed and the resulting
 * SI that needs to be executed to generate a result.
 * 
 * @author jrush
 *
 */
public class MatchConsumption {
    private static final Logger LOGGER = Logger
            .getLogger(MatchConsumption.class);
    private int tokensConsumed = 0;
    private ArrayList<String> tokens = new ArrayList<String>();
    private ExecutableSI globalExecutation = null;
    private ArrayList<ExecutableSI> executationCollection = new ArrayList<ExecutableSI>();

    public MatchConsumption() {
    }

    public MatchConsumption(int tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public MatchConsumption(int tokensConsumed,
            ArrayList<ExecutableSI> executationCollection) {
        this.tokensConsumed = tokensConsumed;
        this.executationCollection = executationCollection;
    }

    public MatchConsumption(int tokensConsumed, ExecutableSI si) {
        this.tokensConsumed = tokensConsumed;
        this.executationCollection.add(si);
    }

    public MatchConsumption(ExecutableSI executableSI) {
        executationCollection.add(executableSI);
    }

    public int getTokensConsumed() {
        return tokensConsumed;
    }

    public void setTokensConsumed(int tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public ArrayList<ExecutableSI> getExecutationCollection() {
        return executationCollection;
    }

    public void setExecutationCollection(ArrayList<ExecutableSI> newValue) {
        executationCollection = newValue;
    }

    public void addExecutableSI(ExecutableSI si) {
        if (si != null)
            executationCollection.add(si);
    }

    public void setGlobalExecutableSI(ExecutableSI si) {
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

    public void addExecutableSI(ArrayList<ExecutableSI> si) {
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
            for (ExecutableSI si : executationCollection) {
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
        for (ExecutableSI si : executationCollection) {
            si.execute(context, workingScope);
        }

        // Since the last item was a rule, we can return rules.latest
        return context.evaluateString(workingScope, "rules.latest();",
                "SISR from MatchConsumer", 0, null);
    }

}
