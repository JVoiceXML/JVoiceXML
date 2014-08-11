package org.jvoicexml.processor.srgs;

import java.net.URI;

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

public class RuleComponentHelper {
    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleSequence sequence,
            final RuleComponent current) throws GrammarException {
        final RuleComponent[] components = sequence.getRuleComponents();
        for (RuleComponent component : components) {
            if (component.equals(current)) {
                return grammar;
            }
            final RuleGrammar foundGrammar = findGrammar(manager, grammar,
                    component, current);
            if (foundGrammar != null) {
                return grammar;
            }
        }
        return null;
    }

    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleAlternatives alternatives,
            final RuleComponent current) throws GrammarException {
        final RuleComponent[] components = alternatives.getRuleComponents();
        for (RuleComponent component : components) {
            final RuleGrammar foundGramamr = findGrammar(manager, grammar,
                    component, current);
            if (foundGramamr != null) {
                return foundGramamr;
            }
        }
        return null;
    }

    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleCount count,
            final RuleComponent current) throws GrammarException {
        final RuleComponent component = count.getRuleComponent();
        return findGrammar(manager, grammar, component, current);
    }

    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleReference reference,
            final RuleComponent current) throws GrammarException {
        final RuleReference resolvedReference = grammar.resolve(reference);
        final Rule rule = manager.resolve(resolvedReference);
        if (rule == null) {
            return null;
        }
        final RuleComponent component = rule.getRuleComponent();
        final URI uri = reference.getGrammarReference();
        final RuleGrammar nextGrammar = (RuleGrammar) manager.getGrammar(uri);
        return findGrammar(manager, nextGrammar, component, current);
    }

    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleComponent component,
            final RuleComponent current) throws GrammarException {
        if (component.equals(current)) {
            return grammar;
        }
        if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            return findGrammar(manager, grammar, sequence, current);
        } else if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            return findGrammar(manager, grammar, alternatives, current);
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            return findGrammar(manager, grammar, count, current);
        } else if (component instanceof RuleReference) {
            final RuleReference reference = (RuleReference) component;
            return findGrammar(manager, grammar, reference, current);
        } else {
            return null;
        }
    }

    private static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleGrammar grammar, final RuleComponent current)
            throws GrammarException {
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        final RuleComponent component = rule.getRuleComponent();
        return findGrammar(manager, grammar, component, current);
    }

    public static RuleGrammar findGrammar(final GrammarManager manager,
            final RuleComponent component) throws GrammarException {
        final Grammar[] grammars = manager.listGrammars();
        for (Grammar grammar : grammars) {
            final RuleGrammar gram = (RuleGrammar) grammar;
            final RuleGrammar foundGrammar = findGrammar(manager, gram,
                    component);
            if (foundGrammar != null) {
                return gram;
            }
        }
        return null;

    }

    public static RuleComponent getReferencedRuleComponent(
            final GrammarManager manager, final RuleGrammar grammar,
            final RuleReference reference) throws GrammarException {
        final RuleReference resolvedReference = grammar.resolve(reference);
        final Rule rule = manager.resolve(resolvedReference);
        if (rule == null) {
            return null;
        }
        return rule.getRuleComponent();
    }

    public static RuleComponent[] getRuleComponents(
            final GrammarManager manager, final RuleComponent component)
            throws GrammarException {
        if (component instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) component;
            return sequence.getRuleComponents();
        } else if (component instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) component;
            return alternatives.getRuleComponents();
        } else if (component instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            return new RuleComponent[] { count.getRuleComponent() };
        } else if (component instanceof RuleToken) {
            return null;
        } else if (component instanceof RuleTag) {
            return null;
        } else if (component instanceof RuleReference) {
            final RuleReference reference = (RuleReference) component;
            final RuleGrammar grammar = findGrammar(manager, component);
            final RuleComponent referencedRuleComponent = getReferencedRuleComponent(manager, grammar, reference);
            return getRuleComponents(manager, referencedRuleComponent);
        } else {
            return null;
        }
    }
}
