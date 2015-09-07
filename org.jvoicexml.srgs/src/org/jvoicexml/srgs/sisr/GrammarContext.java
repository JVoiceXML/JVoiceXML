package org.jvoicexml.srgs.sisr;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.SrgsSisrGrammar;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class GrammarContext implements ExecutableSemanticInterpretation {
    private static final Logger LOGGER = Logger.getLogger(GrammarContext.class);

    private SrgsSisrGrammar grammar;
    private ExecutableSemanticInterpretation executableSI;
    private String ruleName;

    public GrammarContext(SrgsSisrGrammar grammar, String ruleName,
            ExecutableSemanticInterpretation executableSI) {
        this.grammar = grammar;
        this.ruleName = ruleName;
        this.executableSI = executableSI;
    }

    @Override
    public void dump(String pad) {
        LOGGER.debug(pad + "GrammarContext(" + grammar.getUri() + ")");
        executableSI.dump(pad + " ");
    }

    @Override
    public void execute(org.mozilla.javascript.Context parentContext,
            Scriptable parentScope) {
        org.mozilla.javascript.Context context = org.mozilla.javascript.Context
                .enter();
        context.setLanguageVersion(org.mozilla.javascript.Context.VERSION_DEFAULT);
        Scriptable globalScope = context.initStandardObjects();

        if (grammar.getGlobalTags() != null)
            grammar.getGlobalTags().execute(context, globalScope);
        ((ScriptableObject) globalScope).sealObject();

        // Set up working scope - note out initialized at this level, but
        // shouldn't be used
        Scriptable grammarScope = context.newObject(globalScope);
        context.evaluateString(
                grammarScope,
                "var out=new Object();\nvar rules=new Object();\n"
                        + "var meta={current: function() {return {text:'', score:1.0}}};\n",
                "SISR init from MatchConsumer", 0, null);

        executableSI.execute(context, grammarScope);

        Object out = Context.getOutApplyDefaultAssignmentIfNeeded(context,
                grammarScope, true);

        updateParentScope(parentContext, parentScope, context, grammarScope,
                out);
    }

    /**
     * Updates the parent scope:
     * <ul>
     * <li>rules.rulename</li>
     * <li>rules.latest()</li>
     * <li>meta.rulename</li>
     * <li>meta.current()</li>
     * </ul>
     * 
     * @param context
     * @param parentScope
     * @param ruleScope
     * @param out
     */
    private void updateParentScope(
            org.mozilla.javascript.Context parentContext,
            Scriptable parentScope,
            org.mozilla.javascript.Context grammarContext,
            Scriptable grammarScope, Object out) {

        // Setup rules
        Scriptable ruleObject = (Scriptable) parentScope.get("rules",
                parentScope);
        ruleObject.put(ruleName, ruleObject, out); // sets rules.rulename
        parentContext.evaluateString(parentScope,
                getRulesLastestScript(ruleName), "Context:rules.Latest", 0,
                null); // sets rules.latest()

        // Setup meta
        // meta.rulename will be set based on the match in the current context
        // meta.current() will be set based on the current value if the parent
        // context plus the value coming out of the rule context
        String ruleMetaCurrent = (String) grammarContext.evaluateString(
                grammarScope, "meta.current().text;", "Context:rule get meta",
                0, null);

        parentContext.evaluateString(parentScope, "meta." + ruleName
                + "=function() {return {text:'" + ruleMetaCurrent
                + "', score:1.0}};", "Context:rule set meta." + ruleName, 0,
                null);

        if (ruleMetaCurrent.length() > 0) {
            String parentMetaCurrent = (String) parentContext.evaluateString(
                    parentScope, "meta.current().text;",
                    "Context:parent get meta", 0, null);

            if (parentMetaCurrent.length() == 0) {
                parentContext.evaluateString(parentScope,
                        "meta.current=function() {return {text:'"
                                + ruleMetaCurrent + "', score:1.0}};",
                        "AddToMatchedText:set meta1", 0, null);
            } else {
                parentContext.evaluateString(parentScope,
                        "meta.current=function() {return {text:'"
                                + parentMetaCurrent + " " + ruleMetaCurrent
                                + "', score:1.0}};",
                        "AddToMatchedText:set meta1", 0, null);
            }
        }
    }

    private String getRulesLastestScript(String contextName) {
        return "rules.latest = function() {return this." + contextName + ";}"; // We
                                                                               // are
                                                                               // within
                                                                               // the
                                                                               // rules
                                                                               // context,
                                                                               // so
                                                                               // the
                                                                               // rule
                                                                               // id
                                                                               // should
                                                                               // be
                                                                               // valid
    }

}
