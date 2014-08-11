package org.jvoicexml.processor.srgs;

import java.util.Arrays;
import java.util.List;

import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleParse;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleToken;

public class BacktrackingGrammarChecker {
    public RuleComponent backtrack(RuleGraphContext context, String[] input)
            throws GrammarException {
        final List<String> inputList = Arrays.asList(input);
        final RuleComponent component = backtrack(context, inputList, null);
        if (component == null) {
            return null;
        }
        final RuleGrammar grammar = context.getGrammar();
        final String root = grammar.getRoot();
        final RuleReference reference = new RuleReference(root);
        return new RuleParse(reference, component);
    }

    private RuleComponent backtrack(RuleGraphContext context,
            List<String> input, RuleComponent component)
            throws GrammarException {
        if (solves(context, component, input)) {
            // indicate that we found a solution
            dump("solved:", component);
            return component;
        }
        final RuleComponent[] next = getNext(context, component);
        if (next == null) {
            dump("dead end:", component);
            return null;
        }
        dump("checking:", component);
        for (RuleComponent c : next) {
            dump("child:", c);
        }
        dump(context);
        for (RuleComponent candidate : next) {
            final List<String> nextInput = new java.util.ArrayList<String>();
            if (promising(candidate, input, nextInput)) {
                dump("promising:", candidate);
                final RuleGraphContext nextContext = getContextForCandidate(
                        context, candidate);
                final RuleComponent parse = backtrack(nextContext, nextInput,
                        candidate);
                if (parse != null) {
                    if (component == null) {
                        return parse;
                    }
                    dump("compon:", component);
                    dump("adding:", candidate);
                    dump("parse: ", parse);
                    dump(context);
                    RuleComponent added;
                    if (context.isCurrentCompound(component)) {
                        added = addToSolution(context, component, parse);
                    } else {
                        added = addToSolution(context, candidate, parse);

                    }
                    dump("added: ", added);
                    return added;
                }
            }
        }
        return null;
    }

    private void dump(String str, RuleComponent component) {
        if (component == null) {
            System.out.println(str + " " + component);
        } else {
            System.out.println(str + " " + component.getClass().getSimpleName()
                    + " " + component);
        }
    }

    private void dump(RuleGraphContext context) {
        for (CompoundStackItem item : context.currentCompoundStack) {
            System.out.println(item);
        }
    }

    private RuleGraphContext getContextForCandidate(RuleGraphContext context,
            RuleComponent component) {
        final RuleGraphContext subContext = context.getSubContext(component);
        if (subContext == null) {
            return context.nextContext(component);
        } else {
            return subContext.nextContext(component);
        }
    }

    private boolean solves(RuleGraphContext context, RuleComponent component,
            List<String> input) {
        if (!input.isEmpty()) {
            return false;
        }
        return context.isLastInCurrentCompund(component);
    }

    private boolean promising(RuleToken token, List<String> input,
            List<String> nextInput) {
        final String text = token.getText();
        final String[] tokens = text.split(" ");
        int matchedTokens = 0;

        // check if the first n tokens match
        for (; matchedTokens < tokens.length; matchedTokens++) {
            // do not go to far
            if (matchedTokens >= input.size()) {
                return false;
            }
            final String expected = tokens[matchedTokens];
            final String actual = input.get(matchedTokens);
            if (!expected.equalsIgnoreCase(actual)) {
                return false;
            }
        }

        // keep the remaining input tokens for future checks
        for (int i = matchedTokens; i < input.size(); i++) {
            final String remaining = input.get(i);
            nextInput.add(remaining);
        }
        return true;
    }

    private boolean promising(RuleComponent component, List<String> input,
            List<String> nextInput) {
        if (component instanceof RuleToken) {
            final RuleToken token = (RuleToken) component;
            return promising(token, input, nextInput);
        }
        nextInput.addAll(input);
        return true;
    }

    private RuleComponent[] getNext(final RuleGraphContext context,
            final RuleComponent component) throws GrammarException {
        return context.getNext(component);
    }

    private RuleComponent makeSequence(final RuleComponent component,
            final RuleComponent parse) {
        if (parse == null) {
            return component;
        }
        if (parse instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) parse;
            final RuleComponent[] components = sequence.getRuleComponents();
            final RuleComponent[] adaptedComponents;
            if (component instanceof RuleSequence) {
                final RuleSequence preceedingSeq = (RuleSequence) component;
                final RuleComponent[] sequenceComponents = preceedingSeq
                        .getRuleComponents();
                adaptedComponents = Arrays.copyOf(sequenceComponents,
                        sequenceComponents.length + components.length);
                System.arraycopy(components, 0, adaptedComponents,
                        sequenceComponents.length, components.length);
            } else {
                adaptedComponents = new RuleComponent[components.length + 1];
                adaptedComponents[0] = component;
                System.arraycopy(components, 0, adaptedComponents, 1,
                        components.length);
            }
            return new RuleSequence(adaptedComponents);
        } else {
            final RuleComponent[] components = new RuleComponent[] { component,
                    parse };
            return new RuleSequence(components);
        }
    }

    private RuleComponent addToSolution(final RuleToken token,
            final RuleComponent parse) {
        return makeSequence(token, parse);
    }

    private RuleComponent addToSolution(final RuleAlternatives alternatives,
            final RuleComponent parse) {
        if (parse == null) {
            return alternatives;
        }
        final RuleComponent[] components = new RuleComponent[] { parse };
        return new RuleAlternatives(components);
    }

    private RuleComponent addToSolution(final RuleSequence sequence,
            final RuleComponent parse) {
        return makeSequence(sequence, parse);
    }

    private RuleComponent addToSolution(final RuleGraphContext context,
            final RuleCount count, final RuleComponent parse) {
        if (parse == null) {
            return count;
        }
        final int iterationCount = context.getCurrentLocalMaxIterationCount();
        if (iterationCount == 0) {
            final RuleComponent component = count.getRuleComponent();
            final RuleCount adaptedCount = new RuleCount(component, 0, 0);
            return makeSequence(adaptedCount, parse);
        } else {
            if (parse instanceof RuleSequence) {
                final RuleComponent component = count.getRuleComponent();
                final RuleCount adaptedCount = new RuleCount(component,
                        iterationCount, iterationCount);
                return makeSequence(adaptedCount, parse);
            } else {
                return new RuleCount(parse, iterationCount, iterationCount);
            }
        }
    }

    private RuleComponent addToSolution(final RuleGraphContext context,
            final RuleComponent candidate, final RuleComponent parse) {
        if (candidate == parse) {
            return candidate;
        }
        if (candidate instanceof RuleToken) {
            final RuleToken token = (RuleToken) candidate;
            return addToSolution(token, parse);
        } else if (candidate instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) candidate;
            return addToSolution(alternatives, parse);
        } else if (candidate instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) candidate;
            return addToSolution(sequence, parse);
        } else if (candidate instanceof RuleCount) {
            final RuleCount count = (RuleCount) candidate;
            return addToSolution(context, count, parse);
        }
        return null;
    }
}