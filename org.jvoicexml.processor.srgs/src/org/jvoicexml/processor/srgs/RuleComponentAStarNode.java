package org.jvoicexml.processor.srgs;

import java.net.URI;
import java.util.Collection;
import java.util.Queue;

import org.jvoicexml.processor.srgs.grammar.Grammar;
import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleTag;
import org.jvoicexml.processor.srgs.grammar.RuleToken;

public class RuleComponentAStarNode implements AStarNode {
    private double costsFromStart;
    private double priority;
    private RuleComponentAStarNode predecessor;
    private GrammarManager manager;
    private RuleGrammar ruleGrammar;
    private RuleComponent component;
    private final Queue<String> input;
    private RuleComponent lastComponentInSequence;

    public RuleComponentAStarNode(final GrammarManager grammarManager,
            final RuleComponent ruleComponent, final Queue<String> tokenQueue) {
        manager = grammarManager;
        component = ruleComponent;
        if (component instanceof RuleToken) {
            final RuleToken token = (RuleToken) component;
            final String text = token.getText();
            final String[] tokens = text.split(" ");
            input = new java.util.LinkedList<String>(tokenQueue);
            for (String current : tokens) {
                final String peek = input.peek();
                if (current.equalsIgnoreCase(peek)) {
                    input.poll();
                } else {
                    break;
                }
            }
        } else {
            input = tokenQueue;
        }
    }

    @Override
    public int compareTo(AStarNode other) {
        double diff = priority - other.getPriority();
        return (int) Math.signum(diff);
    }

    @Override
    public void setPredecessor(AStarNode node) {
        predecessor = (RuleComponentAStarNode) node;
    }

    @Override
    public double getCostsFromStart() {
        return costsFromStart;
    }

    @Override
    public void setCostsFromStart(double costs) {
        costsFromStart = costs;
    }

    @Override
    public double costsTo(AStarNode successor) {
        final RuleComponentAStarNode other = (RuleComponentAStarNode) successor;
        
        final double h = getHeuristicCosts();
        final double otherH = other.getHeuristicCosts();
        if (otherH < h) {
            return h - otherH;
        }
        return otherH;
    }

    @Override
    public double getHeuristicCosts() {
        return input.size();
    }

    @Override
    public void setPriority(double value) {
        priority = value;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleSequence sequence, final RuleComponent current)
            throws GrammarException {
        final RuleComponent[] components = sequence.getRuleComponents();
        boolean found = false;
        for (RuleComponent component : components) {
            if (found) {
                return component;
            }
            found = component.equals(current);
            if (!found) {
                final RuleComponent foundComponent = getNextRuleComponent(
                        grammar, component, current);
                if (foundComponent != null) {
                    return foundComponent;
                }
            }
            lastComponentInSequence = component;
        }
        return null;
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleAlternatives alternatives, final RuleComponent current)
            throws GrammarException {
        final RuleComponent[] components = alternatives.getRuleComponents();
        for (RuleComponent component : components) {
            final RuleComponent foundComponent = getNextRuleComponent(grammar,
                    component, current);
            if (foundComponent != null) {
                return foundComponent;
            }
        }
        return null;
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleCount count, final RuleComponent current)
            throws GrammarException {
        final RuleComponent component = count.getRuleComponent();
        return getNextRuleComponent(grammar, component, current);
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleReference reference, final RuleComponent current)
            throws GrammarException {
        final RuleReference resolvedReference = grammar.resolve(reference);
        final Rule rule = manager.resolve(resolvedReference);
        if (rule == null) {
            return null;
        }
        final RuleComponent component = rule.getRuleComponent();
        final URI uri = reference.getGrammarReference();
        final RuleGrammar nextGrammar = (RuleGrammar) manager.getGrammar(uri);
        return getNextRuleComponent(nextGrammar, component, current);
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleComponent component, final RuleComponent current)
            throws GrammarException {
        if (current.equals(lastComponentInSequence)) {
            return component;
        }
        if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            return getNextRuleComponent(grammar, sequence, current);
        } else if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            return getNextRuleComponent(grammar, alternatives, current);
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            return getNextRuleComponent(grammar, count, current);
        } else if (component instanceof RuleToken) {
            return null;
        } else if (component instanceof RuleTag) {
            return null;
        } else if (component instanceof RuleReference) {
            final RuleReference reference = (RuleReference) component;
            return getNextRuleComponent(grammar, reference, current);
        } else {
            return null;
        }
    }

    private RuleComponent getNextRuleComponent(final RuleGrammar grammar,
            final RuleComponent current) throws GrammarException {
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        final RuleComponent component = rule.getRuleComponent();
        return getNextRuleComponent(grammar, component, current);
    }

    protected RuleComponent getNextRuleComponent(final RuleComponent current)
            throws GrammarException {
        lastComponentInSequence = null;
        if (ruleGrammar == null) {
            final Grammar[] grammars = manager.listGrammars();
            for (Grammar grammar : grammars) {
                final RuleGrammar gram = (RuleGrammar) grammar;
                final RuleComponent component = getNextRuleComponent(gram,
                        current);
                if (component != null) {
                    ruleGrammar = gram;
                    return component;
                }
            }
            return null;
        } else {
            return getNextRuleComponent(ruleGrammar, current);
        }
    }

    private RuleComponent[] getRuleComponents() throws GrammarException {
        RuleComponent[] components = RuleComponentHelper.getRuleComponents(
                manager, component);
        if (components.length != 0) {
            return components;
        }
        final RuleComponent next = getNextRuleComponent(component);
        if (next == null) {
            return null;
        }
        return new RuleComponent[] { next };
    }

    @Override
    public Collection<AStarNode> getSuccessors() {
        final Collection<AStarNode> successors = new java.util.ArrayList<AStarNode>();
        try {
            final RuleComponent[] components = getRuleComponents();
            if (components == null) {
                return successors;
            }
            for (RuleComponent current : components) {
                final AStarNode node = new RuleComponentAStarNode(manager,
                        current, input);
                successors.add(node);
            }
        } catch (GrammarException e) {
            e.printStackTrace();
        }
        return successors;
    }
}
