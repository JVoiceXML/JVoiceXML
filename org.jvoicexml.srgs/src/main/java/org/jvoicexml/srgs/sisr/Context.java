/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs.sisr;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

public class Context implements ExecutableSemanticInterpretation {
    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(Context.class);
    private List<ExecutableSemanticInterpretation> executationCollection =
            new java.util.ArrayList<ExecutableSemanticInterpretation>();
    private String ruleName;

    public Context(final String contextName) {
        this.ruleName = contextName;
    }

    public List<ExecutableSemanticInterpretation> getExecutationCollection() {
        return executationCollection;
    }

    public void addExecutableContent(
            final List<ExecutableSemanticInterpretation> si) {
        executationCollection.addAll(si);
    }

    public void addExecutableContent(
            final ExecutableSemanticInterpretation si) {
        executationCollection.add(si);
    }

    public void setExecutableContent(
            final List<ExecutableSemanticInterpretation> si) {
        executationCollection = si;
    }

    public void dump(final String pad) {
        LOGGER.debug(pad + "Context(" + ruleName + ")");
        for (ExecutableSemanticInterpretation si : executationCollection) {
            si.dump(pad + " ");
        }
    }

    @Override
    public void execute(final org.mozilla.javascript.Context context,
            final Scriptable parentScope) {
        LOGGER.debug("===================================================================================");
        LOGGER.debug("Starting context " + ruleName);

        // Create local scope
        Scriptable ruleScope = context.newObject(parentScope);

        // Initialize out, rules and meta
        context.evaluateString(ruleScope, 
                contextInitSemanticInterpretation(ruleName),
                "Context:init", 0, null);

        // Perform SI execution
        boolean ruleRefProcessed = false;
        for (ExecutableSemanticInterpretation si : executationCollection) {
            si.execute(context, ruleScope);
            if (si instanceof Context) {
                ruleRefProcessed = true;
            }
        }

        // get result from ruleScope
        Object out = getOutApplyDefaultAssignmentIfNeeded(context, ruleScope,
                ruleRefProcessed);

        LOGGER.debug("out=" + out);
        if (out instanceof Scriptable) {
            dumpScope((Scriptable) out, " ");
        }

        // Apply to parentScope
        updateParentScope(context, parentScope, ruleScope, out);

        LOGGER.debug("Dumping parent scope:");
        dumpScope(parentScope);
        LOGGER.debug("Ending context " + ruleName);
        LOGGER.debug("===================================================================================");
    }

    static Object getOutApplyDefaultAssignmentIfNeeded(
            final org.mozilla.javascript.Context context, 
            final Scriptable ruleScope, final boolean ruleRefProcessed) {

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
                        + ruleMetaCurrent + "';", "Context:out", 0, null);
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
     * </ul>.
     * 
     * @param context
     * @param parentScope
     * @param ruleScope
     * @param out
     */
    private void updateParentScope(final org.mozilla.javascript.Context context,
            final Scriptable parentScope, final Scriptable ruleScope, 
            final Object out) {
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

//        LOGGER.debug("ruleMetaCurrent="+ruleMetaCurrent);
//        LOGGER.debug("about to execute: "+"meta."+ruleName+"=function() {return {text:'"+ruleMetaCurrent+"', score:1.0}};");
        context.evaluateString(parentScope, "meta." + ruleName
                + "=function() {return {text:'" + ruleMetaCurrent
                + "', score:1.0};};", "Context:rule set meta." + ruleName, 0,
                null);

        if (ruleMetaCurrent.length() > 0) {
            String parentMetaCurrent = (String) context.evaluateString(
                    parentScope, "meta.current().text;",
                    "Context:parent get meta", 0, null);

            if (parentMetaCurrent.length() == 0) {
                context.evaluateString(parentScope,
                        "meta.current=function() {return {text:'"
                                + ruleMetaCurrent + "', score:1.0};};",
                        "AddToMatchedText:set meta1", 0, null);
            } else {
                context.evaluateString(parentScope,
                        "meta.current=function() {return {text:'"
                                + parentMetaCurrent + " " + ruleMetaCurrent
                                + "', score:1.0};};",
                        "AddToMatchedText:set meta1", 0, null);
            }
        }
    }

    private String getRulesLastestScript(final String contextName) {
        return "rules.latest = function() {return this." + contextName + ";};";
        // We are within the rules context, so the rule id should be valid
    }

    private String contextInitSemanticInterpretation(final String contextName) {
        return "var out=new Object();\n"
                + "var rules=new Object();\n"
                + "var meta={current: function() {"
                + "return {text:'', score:1.0};}};\n";
    }

    public void dumpScope(final Scriptable scope) {
        dumpScope(scope, " ");
    }

    public void dumpScope(final Scriptable scope, final String pad) {
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

            if (o instanceof Scriptable) {
                dumpScope((Scriptable) o, pad + " ");
            } else if (o instanceof NativeObject) {
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
