package org.jvoicexml.srgs.sisr;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

public class Context implements ExecutableSemanticInterpretation {
    private static final Logger LOGGER = Logger.getLogger(Context.class);
    private ArrayList<ExecutableSemanticInterpretation> executationCollection = new ArrayList<ExecutableSemanticInterpretation>();
    private String ruleName;

    public Context(String contextName) {
        this.ruleName = contextName;
    }

    public ArrayList<ExecutableSemanticInterpretation> getExecutationCollection() {
        return executationCollection;
    }

    public void addExecutableContent(ArrayList<ExecutableSemanticInterpretation> si) {
        executationCollection.addAll(si);
    }

    public void addExecutableContent(ExecutableSemanticInterpretation si) {
        executationCollection.add(si);
    }

    public void setExecutableContent(ArrayList<ExecutableSemanticInterpretation> si) {
        executationCollection = si;
    }

    public void dump(String pad) {
        LOGGER.debug(pad + "Context(" + ruleName + ")");
        for (ExecutableSemanticInterpretation si : executationCollection)
            si.dump(pad + " ");
    }

    @Override
    public void execute(org.mozilla.javascript.Context context,
            Scriptable parentScope) {
        LOGGER.debug("===================================================================================");
        LOGGER.debug("Starting context " + ruleName);

        // Create local scope
        Scriptable ruleScope = context.newObject(parentScope);

        // Initialize out, rules and meta
        context.evaluateString(ruleScope, contextInitSI(ruleName),
                "Context:init", 0, null);

        // Perform SI execution
        boolean ruleRefProcessed = false;
        for (ExecutableSemanticInterpretation si : executationCollection) {
            si.execute(context, ruleScope);
            if (si instanceof Context)
                ruleRefProcessed = true;
        }

        // get result from ruleScope
        Object out = getOutApplyDefaultAssignmentIfNeeded(context, ruleScope,
                ruleRefProcessed);

        LOGGER.debug("out=" + out);
        if (out instanceof Scriptable)
            dumpScope((Scriptable) out, " ");

        // Apply to parentScope
        updateParentScope(context, parentScope, ruleScope, out);

        LOGGER.debug("Dumping parent scope:");
        dumpScope(parentScope);
        LOGGER.debug("Ending context " + ruleName);
        LOGGER.debug("===================================================================================");
    }

    static Object getOutApplyDefaultAssignmentIfNeeded(
            org.mozilla.javascript.Context context, Scriptable ruleScope,
            boolean ruleRefProcessed) {

        Object out = context.evaluateString(ruleScope, "out;", "Context:out",
                0, null);
        // If out is still an empty object, perform the default assignment
        if (out instanceof NativeObject
                && ((NativeObject) out).getIds().length == 0) {
            if (ruleRefProcessed) {
                LOGGER.debug("default assignment(lastrule)");
                out = context.evaluateString(ruleScope,
                        "out = rules.latest();", "Context:out", 0, null);
            } else {
                String ruleMetaCurrent = (String) context.evaluateString(
                        ruleScope, "meta.current().text;",
                        "Context:rule get meta", 0, null);
                LOGGER.debug("default assignment(meta): " + ruleMetaCurrent);
                out = context.evaluateString(ruleScope, "out = '"
                        + ruleMetaCurrent + "'", "Context:out", 0, null);
            }
        }
        return out;
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
    private void updateParentScope(org.mozilla.javascript.Context context,
            Scriptable parentScope, Scriptable ruleScope, Object out) {
        // Setup rules
        Scriptable ruleObject = (Scriptable) parentScope.get("rules",
                parentScope);
        ruleObject.put(ruleName, ruleObject, out); // sets rules.rulename
        context.evaluateString(parentScope, getRulesLastestScript(ruleName),
                "Context:rules.Latest", 0, null); // sets rules.latest()

        // Setup meta
        // meta.rulename will be set based on the match in the current context
        // meta.current() will be set based on the current value if the parent
        // context plus the value coming out of the rule context
        String ruleMetaCurrent = (String) context.evaluateString(ruleScope,
                "meta.current().text;", "Context:rule get meta", 0, null);

        // LOGGER.debug("ruleMetaCurrent="+ruleMetaCurrent);
        // LOGGER.debug("about to execute: "+"meta."+ruleName+"=function() {return {text:'"+ruleMetaCurrent+"', score:1.0}};");
        context.evaluateString(parentScope, "meta." + ruleName
                + "=function() {return {text:'" + ruleMetaCurrent
                + "', score:1.0}};", "Context:rule set meta." + ruleName, 0,
                null);

        if (ruleMetaCurrent.length() > 0) {
            String parentMetaCurrent = (String) context.evaluateString(
                    parentScope, "meta.current().text;",
                    "Context:parent get meta", 0, null);

            if (parentMetaCurrent.length() == 0) {
                context.evaluateString(parentScope,
                        "meta.current=function() {return {text:'"
                                + ruleMetaCurrent + "', score:1.0}};",
                        "AddToMatchedText:set meta1", 0, null);
            } else {
                context.evaluateString(parentScope,
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

    private String contextInitSI(String contextName) {
        return "var out=new Object();\n"
                + "var rules=new Object();\n"
                + "var meta={current: function() {return {text:'', score:1.0}}};\n";
    }

    public void dumpScope(final Scriptable scope) {
        dumpScope(scope, " ");
    }

    public void dumpScope(final Scriptable scope, String pad) {
        java.lang.Object[] ids = scope.getIds();
        for (Object id : ids) {
            Object o = null;
            if (id instanceof String) {
                o = scope.get((String) id, scope);
                LOGGER.debug(pad + id + "=" + o + " ("
                        + o.getClass().getCanonicalName() + ")");
            } else {
                o = scope.get((int) id, scope);
                LOGGER.debug(pad + id + "=" + o + " ("
                        + o.getClass().getCanonicalName() + ")");
            }

            if (o instanceof Scriptable)
                dumpScope((Scriptable) o, pad + " ");
            else if (o instanceof NativeObject) {
                for (Map.Entry<Object, Object> item : ((NativeObject) o)
                        .entrySet()) {
                    LOGGER.debug(pad + " " + item.getKey() + "="
                            + item.getValue() + " ("
                            + item.getValue().getClass().getCanonicalName()
                            + ")");
                }
            }
        }
    }
}
