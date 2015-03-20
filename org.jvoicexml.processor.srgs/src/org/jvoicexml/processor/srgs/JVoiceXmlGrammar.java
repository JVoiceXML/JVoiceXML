package org.jvoicexml.processor.srgs;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleParse;
import org.jvoicexml.processor.srgs.grammar.RuleReference;

public class JVoiceXmlGrammar implements RuleGrammar {
    private final GrammarManager manager;
    private final URI reference;
    private String root;
    private final Map<String, Rule> rules;
    
    public JVoiceXmlGrammar(final GrammarManager grammarManager, final URI ref) {
        manager = grammarManager;
        reference = ref;
        rules = new java.util.HashMap<String, Rule>();
    }
    
    @Override
    public int getActivationMode() {
        return 0;
    }

    @Override
    public GrammarManager getGrammarManager() {
        return manager;
    }

    @Override
    public URI getReference() {
        return reference;
    }

    @Override
    public void setActivationMode(int mode) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActivatable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setActivatable(boolean activatable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isActivatable(String ruleName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setActivatable(String ruleName, boolean activatable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addElement(String element) throws GrammarException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeElement(String element) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Rule getRule(String ruleName) {
        return rules.get(ruleName);
    }

    @Override
    public void addRule(Rule rule) {
        final String ruleName = rule.getRuleName();
        rules.put(ruleName, rule);
    }

    @Override
    public void addRule(String ruleText) throws GrammarException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addRules(Rule[] rules) {
        for (Rule rule : rules) {
            addRule(rule);
        }
    }

    @Override
    public void removeRule(String ruleName) throws IllegalArgumentException {
        rules.remove(ruleName);
    }

    @Override
    public String[] listRuleNames() {
        final Set<String> keys = rules.keySet();
        final String[] names = new String[keys.size()];
        return keys.toArray(names);
    }

    @Override
    public void setAttribute(String attribute, String value)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getAttribute(String attribute)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getElements() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RuleParse parse(String[] tokens, String ruleName)
            throws IllegalArgumentException, GrammarException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RuleParse parse(String text, String ruleName)
            throws IllegalArgumentException, GrammarException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RuleReference resolve(RuleReference ruleReference)
            throws GrammarException {
        final URI uri = ruleReference.getGrammarReference();
        final String name = ruleReference.getRuleName();
        if (uri == null) {
            return new RuleReference(reference, name);
        }
        return ruleReference;
    }

    @Override
    public void setRoot(String rootName) {
        root = rootName;
    }

    @Override
    public String getRoot() {
        return root;
    }

}
