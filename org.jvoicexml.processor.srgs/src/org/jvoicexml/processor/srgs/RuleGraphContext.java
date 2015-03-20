package org.jvoicexml.processor.srgs;

import java.net.URI;
import java.util.Map;
import java.util.Stack;

import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;

public class RuleGraphContext implements Cloneable {
    private GrammarManager manager;
    private RuleGrammar grammar;
    private RuleComponent rootComponent;
    Stack<CompoundStackItem> currentCompoundStack;
    private Map<RuleComponent, RuleGraphContext> subContexts;

    private RuleGraphContext() {
        subContexts = new java.util.HashMap<RuleComponent, RuleGraphContext>();
        currentCompoundStack = new Stack<CompoundStackItem>();
    }

    public RuleGraphContext(final GrammarManager grammarManager,
            final RuleGrammar ruleGrammar) {
        manager = grammarManager;
        grammar = ruleGrammar;
        currentCompoundStack = new Stack<CompoundStackItem>();
        subContexts = new java.util.HashMap<RuleComponent, RuleGraphContext>();
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        rootComponent = rule.getRuleComponent();
    }

    @Override
    protected RuleGraphContext clone() {
        try {
            final RuleGraphContext context = (RuleGraphContext) super.clone();
            context.currentCompoundStack = new Stack<CompoundStackItem>();
            for (CompoundStackItem item : currentCompoundStack) {
                final CompoundStackItem clone = item.clone();
                context.currentCompoundStack.add(clone);
            }
            context.subContexts = new java.util.HashMap<RuleComponent, RuleGraphContext>();
            for (RuleComponent key : subContexts.keySet()) {
                final RuleGraphContext value = subContexts.get(key);
                final RuleGraphContext clone = value.clone();
                context.subContexts.put(key, clone);
            }
            return context;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

    public RuleGraphContext nextContext(final RuleComponent component) {
        final RuleGraphContext context = new RuleGraphContext();
        context.manager = manager;
        context.grammar = grammar;
        context.rootComponent = rootComponent;
        context.currentCompoundStack.addAll(currentCompoundStack);
        if (isCompoundRuleComponent(component)) {
            pushCompound(context, component);
        }
        return context;
    }

    public RuleGraphContext getSubContext(final RuleComponent component) {
        return subContexts.get(component);
    }

    private void pushCompound(final RuleGraphContext context,
            final RuleComponent component) {
        final CompoundStackItem item = new CompoundStackItem(component);
        context.currentCompoundStack.push(item);
    }

    private RuleComponent getCurrentCompound() {
        if (currentCompoundStack.isEmpty()) {
            return null;
        }
        final CompoundStackItem item = currentCompoundStack.peek();
        return item.getCurrentCompound();
    }

    public boolean isCurrentCompound(final RuleComponent component) {
        final RuleComponent top = getCurrentCompound();
        if (top == null) {
            return false;
        }
        return top.equals(component);
    }
    
    public int getCurrentPositionInSequence() {
        if (currentCompoundStack.isEmpty()) {
            return 0;
        }
        final CompoundStackItem item = currentCompoundStack.peek();
        return item.getPositionInSequence();
    }

    public int getCurrentIterationCount() {
        if (currentCompoundStack.isEmpty()) {
            return 0;
        }
        final CompoundStackItem item = currentCompoundStack.peek();
        return item.getIterationCount();
    }

    public int getCurrentLocalMaxIterationCount() {
        if (currentCompoundStack.isEmpty()) {
            return 0;
        }
        final CompoundStackItem item = currentCompoundStack.peek();
        return item.getLocalMaxIterationCount();
    }

    private void adaptCounters(final RuleGraphContext context,
            final RuleComponent component) {
        if (currentCompoundStack.isEmpty()) {
            return;
        }
        final CompoundStackItem item = currentCompoundStack.peek();
        final RuleComponent currentCompoundComponent = getCurrentCompound();
        if (currentCompoundComponent instanceof RuleSequence) {
            item.incrementPositionInSequence();
        } else if (currentCompoundComponent instanceof RuleCount) {
            item.incrementIterationInCount();
        }
    }

    public RuleGrammar getGrammar() {
        return grammar;
    }

    public void setGrammar(RuleGrammar grammar) {
        this.grammar = grammar;
    }

    public GrammarManager getManager() {
        return manager;
    }

    private boolean isCompoundRuleComponent(final RuleComponent component) {
        if (component instanceof RuleAlternatives) {
            return true;
        }
        if (component instanceof RuleCount) {
            return true;
        }
        if (component instanceof RuleReference) {
            return true;
        }
        if (component instanceof RuleSequence) {
            return true;
        }
        return false;
    }

    private boolean isLastInCurrentCompound(final RuleSequence sequence,
            final RuleComponent component) {
        final RuleComponent[] components = sequence.getRuleComponents();
        final CompoundStackItem item = currentCompoundStack.peek();
        final int positionInSequence = item.getPositionInSequence();
        return positionInSequence > components.length - 1;
    }

    private boolean isLastInCurrentCompound(
            final RuleAlternatives alternatives, final RuleComponent component) {
        return true;
    }

    private boolean isLastInCurrentCompound(final RuleCount count,
            final RuleComponent component) {
        final CompoundStackItem item = currentCompoundStack.peek();
        final int min = count.getRepeatMin();
        final int iterationInCount = item.getIterationCount();
        return min > iterationInCount || item.reachedMaxIterationCount();
    }

    public boolean isLastInCurrentCompund(final RuleComponent component) {
        if (currentCompoundStack.isEmpty()) {
            return true;
        }
        final RuleComponent currentCompoundComponent = getCurrentCompound();
        if (currentCompoundComponent instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) currentCompoundComponent;
            return isLastInCurrentCompound(sequence, component);
        } else if (currentCompoundComponent instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) currentCompoundComponent;
            return isLastInCurrentCompound(alternatives, component);
        } else if (currentCompoundComponent instanceof RuleCount) {
            final RuleCount count = (RuleCount) currentCompoundComponent;
            return isLastInCurrentCompound(count, component);
        }
        return true;
    }

    private RuleComponent[] getNext(final RuleAlternatives alternatives) {
        return alternatives.getRuleComponents();
    }

    private RuleComponent[] getNext(final RuleCount count)
            throws GrammarException {
        final RuleComponent component = count.getRuleComponent();
        final int min = count.getRepeatMin();
        if (min > 0) {
            adaptCounters(this, count);
            return new RuleComponent[] { component };
        } else {
            final RuleComponent[] components = getNextAfterRuleCount(count);
            adaptCounters(this, count);
            return combine(component, components);
        }
    }

    private RuleComponent[] getNext(final RuleSequence sequence) {
        final RuleComponent[] components = sequence.getRuleComponents();
        adaptCounters(this, sequence);
        return new RuleComponent[] { components[0] };
    }

    private RuleComponent[] getNext(final RuleReference reference)
            throws GrammarException {
        final RuleReference resolvedReference = grammar.resolve(reference);
        final Rule rule = manager.resolve(resolvedReference);
        final URI uri = resolvedReference.getGrammarReference();
        grammar = (RuleGrammar) manager.getGrammar(uri);
        final RuleComponent component = rule.getRuleComponent();
        return new RuleComponent[] { component };
    }

    private RuleComponent[] getNextInSequence(final RuleSequence sequence) {
        final CompoundStackItem item = currentCompoundStack.peek();
        final int positionInSequence = item.getPositionInSequence();
        final RuleComponent[] components = sequence.getRuleComponents();
        if (positionInSequence <= components.length - 1) {
            adaptCounters(this, sequence);
            return new RuleComponent[] { components[positionInSequence] };
        }
        return null;
    }

    private RuleComponent[] getNextAfterRuleCount(final RuleCount count)
            throws GrammarException {
        // Try to see what would happen, if we had finished the rule component
        // in the count
        final RuleComponent component = count.getRuleComponent();
        final RuleGraphContext context = nextContext(component).clone();
        // Adapt the local maximal iterations for the count
        final CompoundStackItem item = context.currentCompoundStack.peek();
        final int currentIterationCount = item.getIterationCount();
        item.setLocalMaxIterationCount(currentIterationCount);
        final RuleComponent[] components = context.getNext(component);
        if (components == null) {
            return null;
        }

        // remember the sub contexts
        for (RuleComponent current : components) {
            subContexts.put(current, context);
        }

        return components;
    }

    private RuleComponent[] combine(final RuleComponent component1,
            final RuleComponent[] component2) {
        if (component1 == null && component2 == null) {
            return null;
        }
        if (component1 == null) {
            return component2;
        }
        if (component2 == null) {
            return new RuleComponent[] { component1 };
        }
        if (component2.length == 1 && component1.equals(component2[0])) {
            return new RuleComponent[] { component1 };
        }
        final RuleComponent[] components = new RuleComponent[component2.length + 1];
        components[0] = component1;
        System.arraycopy(component2, 0, components, 1, component2.length);
        return components;
    }

    private RuleComponent[] getNextInRuleCount(final RuleCount count)
            throws GrammarException {
        final int min = count.getRepeatMax();
        final int max = count.getRepeatMax();
        final CompoundStackItem item = currentCompoundStack.peek();
        final int iterationInCount = item.getIterationCount();
        if (min <= iterationInCount && iterationInCount < max) {
            adaptCounters(this, count);
            final RuleComponent component = count.getRuleComponent();
            final RuleComponent[] componentAfterCount = getNextAfterRuleCount(count);
            return combine(component, componentAfterCount);
        }
        return null;
    }

    private RuleComponent[] getRootComponent() {
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        final RuleComponent component = rule.getRuleComponent();
        return new RuleComponent[] { component };
    }

    public RuleComponent[] getNext(final RuleComponent component)
            throws GrammarException {
        if (component == null) {
            return getRootComponent();
        }
        // First, try to retrieve the next components within compounds
        if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            return getNext(alternatives);
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            return getNext(count);
        } else if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            return getNext(sequence);
        } else if (component instanceof RuleReference) {
            final RuleReference reference = (RuleReference) component;
            return getNext(reference);
        }

        // Then try to find the next component within compounds
        if (isLastInCurrentCompund(component)) {
            // We are done with the current compound. Pop and retry
            if (currentCompoundStack.isEmpty()) {
                return null;
            }
            currentCompoundStack.pop();
            return getNext(component);
        } else {
            // Find the next within the current compound
            final CompoundStackItem item = currentCompoundStack.peek();
            final RuleComponent currentCompoundComponent = item
                    .getCurrentCompound();
            if (currentCompoundComponent instanceof RuleSequence) {
                final RuleSequence sequence = (RuleSequence) currentCompoundComponent;
                return getNextInSequence(sequence);
            } else if (currentCompoundComponent instanceof RuleCount) {
                final RuleCount count = (RuleCount) currentCompoundComponent;
                return getNextInRuleCount(count);
            }
        }
        return null;
    }
}
