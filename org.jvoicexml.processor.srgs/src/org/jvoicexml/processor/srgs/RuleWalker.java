package org.jvoicexml.processor.srgs;

import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;

public class RuleWalker implements Cloneable {
    private int position;
    private RuleComponent component;
    private RuleGrammar grammar;
    
    public RuleWalker() {
    }

    
    public RuleWalker(RuleWalker walker) {
        component = walker.getComponent();
        position = walker.getPosition();
        grammar = walker.getGrammar();
    }

    public RuleWalker(RuleWalker walker, RuleComponent comp) {
        component = comp;
        position = walker.getPosition();
        grammar = walker.getGrammar();
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int pos) {
        this.position = pos;
    }
    public void increasePosition() {
        ++position;
    }
    
    public RuleComponent getComponent() {
        return component;
    }
    public void setComponent(RuleComponent component) {
        this.component = component;
    }

    public RuleGrammar getGrammar() {
        return grammar;
    }

    public void setGrammar(RuleGrammar grammar) {
        this.grammar = grammar;
    }

    
}
